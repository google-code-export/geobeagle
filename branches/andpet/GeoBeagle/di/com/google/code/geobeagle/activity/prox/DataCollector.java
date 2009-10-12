/**
 * 
 */
package com.google.code.geobeagle.activity.prox;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.SurfaceHolder;

@SuppressWarnings("deprecation")
class DataCollector implements SensorListener,
LocationListener {
    
    private final ProximityPainter mProximityPainter;
    private final SensorManager mSensorManager;
    //private final Sensor mCompassSensor;
    private LocationManager mLocationManager;
    
    public DataCollector(Activity activity, SurfaceHolder holder,
            ProximityPainter proximityPainter) {
        mProximityPainter = proximityPainter;
        mSensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
        mLocationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public void start() {
        mSensorManager.registerListener(this, SensorManager.SENSOR_ORIENTATION,  
                SensorManager.SENSOR_DELAY_UI);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mProximityPainter.setUserLocation(location.getLatitude(), 
                location.getLongitude(), location.getAccuracy()); 
    }
    
    public void pause() {
        mSensorManager.unregisterListener(this);
        mLocationManager.removeUpdates(this);
    }
    
    // ***********************************
    // ** Implement SensorListener **
    
    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {
    }

    //public void onSensorChanged(SensorEvent event)  //SensorEventListener
    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ORIENTATION)
            mProximityPainter.setUserDirection(values[0]);
    }

    // ***********************************
    // ** Implement LocationListener **

    private static final long RETAIN_GPS_MILLIS = 10000L;
    private long mLastGpsFixTime;
    private boolean mHaveLocation;
    private Location mNetworkLocation;
    private boolean mGpsAvailable;
    private boolean mNetworkAvailable;
    
    @Override
    public void onLocationChanged(Location location) {
        // Log.d("GeoBeagle", "radarview::onLocationChanged");
        mHaveLocation = true;

        final long now = SystemClock.uptimeMillis();
        boolean useLocation = false;
        final String provider = location.getProvider();
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            // Use GPS if available
            mLastGpsFixTime = SystemClock.uptimeMillis();
            useLocation = true;
        } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
            // Use network provider if GPS is getting stale
            useLocation = now - mLastGpsFixTime > RETAIN_GPS_MILLIS;
            if (mNetworkLocation == null) {
                mNetworkLocation = new Location(location);
            } else {
                mNetworkLocation.set(location);
            }

            mLastGpsFixTime = 0L;
        }
        if (useLocation) {
            mProximityPainter.setUserLocation(location.getLatitude(),
                    location.getLongitude(), location.getAccuracy());
            if (location.hasBearing() && location.hasSpeed()) {
                mProximityPainter.setUserMovement(location.getBearing(),
                        location.getSpeed());
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.d("GeoBeagle", "onStatusChanged " + provider + ", " + status);
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    mGpsAvailable = true;
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    mGpsAvailable = false;

                    if (mNetworkLocation != null && mNetworkAvailable) {
                        // Fallback to network location
                        mLastGpsFixTime = 0L;
                        onLocationChanged(mNetworkLocation);
                    }

                    break;
            }

        } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    mNetworkAvailable = true;
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    mNetworkAvailable = false;
                    break;
            }
        }
    }
}