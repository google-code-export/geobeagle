/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle;

import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import java.util.ArrayList;

/** Responsible for providing an up-to-date location and compass direction */
@SuppressWarnings("deprecation")
public class LocationAndDirection implements LocationListener, SensorListener {
    private Location mLocation;
    private final LocationManager mLocationManager;
    private float mAzimuth;
    /** A refresh is sent whenever a sensor changes */
    private final ArrayList<Refresher> mObservers = new ArrayList<Refresher>();
    private final SensorManager mSensorManager;
    private boolean mUseNetwork = true;

    public LocationAndDirection(LocationManager locationManager,
            SensorManager sensorManager) {
        mLocationManager = locationManager;
        mSensorManager = sensorManager;
        //mLocation = getLastKnownLocation();  //work in constructor..
    }

   
    /** Enable/disable getting the position from the cell network,
     * in addition to GPS. Default is 'enabled'. */
    /*
    public void setUseNetwork(boolean useNetwork) {
        if (mUseNetwork == useNetwork)
            return;
        mUseNetwork = useNetwork;
        //Hack to only register for the correct providers:
        onPause();
        onResume();
    }
    */
    
    public Location getLocation() {
        if (mLocation == null)
            mLocation = getLastKnownLocation();
        return mLocation;
    }

    public void addObserver(Refresher refresher) {
        if (!mObservers.contains(refresher))
            mObservers.add(refresher);
    }

    private void notifyObservers() {
        for (Refresher refresher : mObservers) {
            refresher.refresh();
        }
    }
    
    /**
     * Choose the better of two locations: If one location is newer and more
     * accurate, choose that. (This favors the gps). Otherwise, if one
     * location is newer, less accurate, but farther away than the sum of
     * the two accuracies, choose that. (This favors the network locator if
     * you've driven a distance and haven't been able to get a gps fix yet.)
     */
    private static Location choose(Location oldLocation, Location newLocation) {
        if (oldLocation == null)
            return newLocation;
        if (newLocation == null)
            return oldLocation;

        if (newLocation.getTime() > oldLocation.getTime()) {
            if (newLocation.getAccuracy() <= oldLocation.getAccuracy())
                return newLocation;
            else if (oldLocation.distanceTo(newLocation) >= oldLocation.getAccuracy()
                    + newLocation.getAccuracy()) {
                return newLocation;
            }
        }
        return oldLocation;
    }

    public void onLocationChanged(Location location) {
        mLocation = choose(mLocation, location);
        notifyObservers();
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onAccuracyChanged(int sensor, int accuracy) {
        //Log.d("GeoBeagle", "onAccuracyChanged " + sensor + " accuracy " + accuracy);
    }

    public void onSensorChanged(int sensor, float[] values) {
        final float currentAzimuth = values[0];
        if (Math.abs(currentAzimuth - mAzimuth) > 5) {
            //Log.d("GeoBeagle", "azimuth now " + sensor +", " + currentAzimuth);
            mAzimuth = currentAzimuth;
            notifyObservers();
        }
    }

    public void onResume(SharedPreferences sharedPreferences) {
        mUseNetwork = sharedPreferences.getBoolean("use-network-location", true);
        
        mSensorManager.registerListener(this, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        long minTime = 1000;  //ms
        float minDistance = 0;  //sec
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
                minTime, minDistance, this);
        if (mUseNetwork)
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    minTime, minDistance, this);
        
        onLocationChanged(getLastKnownLocation());
    }
    
    public void onPause() {
        mSensorManager.unregisterListener(this);
        mLocationManager.removeUpdates(this);
    }
    
    //TODO: Rename to "areUpdatesEnabled" or something
    public boolean isProviderEnabled() {
        return mLocationManager.isProviderEnabled("gps")
                || (mUseNetwork && mLocationManager.isProviderEnabled("network"));
    }

    //TODO: Remove this method? Should getLocation be the same thing?
    public Location getLastKnownLocation() {
        Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null)
            return gpsLocation;
        if (mUseNetwork)
            return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return null;
    }
    
    public float getAzimuth() {
        return mAzimuth;
    }
}
