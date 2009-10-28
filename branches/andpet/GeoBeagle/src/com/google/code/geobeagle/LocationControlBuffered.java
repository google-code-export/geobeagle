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

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.ArrayList;

//TODO: Rename class
/** Responsible for providing an up-to-date location and compass direction */
@SuppressWarnings("deprecation")
public class LocationControlBuffered implements LocationListener, SensorListener {
    private Location mLocation;
    private final LocationManager mLocationManager;
    private float mAzimuth;
    /** A refresh is sent whenever a sensor changes */
    private final ArrayList<Refresher> mObservers = new ArrayList<Refresher>();
    private final SensorManager mSensorManager;

    public LocationControlBuffered(LocationManager locationManager,
            SensorManager sensorManager) {
        mLocationManager = locationManager;
        mSensorManager = sensorManager;
    }

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
    private static Location choose(Location location1, Location location2) {
        if (location1 == null)
            return location2;
        if (location2 == null)
            return location1;

        if (location2.getTime() > location1.getTime()) {
            if (location2.getAccuracy() <= location1.getAccuracy())
                return location2;
            else if (location1.distanceTo(location2) >= location1.getAccuracy()
                    + location2.getAccuracy()) {
                return location2;
            }
        }
        return location1;
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
    }

    public void onSensorChanged(int sensor, float[] values) {
        final float currentAzimuth = values[0];
        if (Math.abs(currentAzimuth - mAzimuth) > 5) {
            // Log.d("GeoBeagle", "azimuth now " + sensor +", " +
            // currentAzimuth);
            mAzimuth = currentAzimuth;
            notifyObservers();
        }
    }

    public void onResume() {
        mSensorManager.registerListener(this, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, 
                this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0,
                this);
    }
    
    public void onPause() {
        mSensorManager.unregisterListener(this);        
    }
    
    public boolean isProviderEnabled() {
        return mLocationManager.isProviderEnabled("gps")
                || mLocationManager.isProviderEnabled("network");
    }

    //TODO: Remove this method -- getLocation should be the same thing
    public Location getLastKnownLocation() {
        Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null)
            return gpsLocation;
        return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
    
    public float getAzimuth() {
        return mAzimuth;
    }
}
