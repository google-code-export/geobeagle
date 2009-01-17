
package com.google.code.geobeagle;

import android.location.LocationListener;
import android.location.LocationManager;

public class GpsLifecycleManager {
    private final LocationListener mLocationListener;
    private final LocationManager mLocationManager;

    public GpsLifecycleManager(LocationListener locationListener, LocationManager locationManager) {
        mLocationListener = locationListener;
        mLocationManager = locationManager;
    }

    public void onPause() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    public void onResume() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                mLocationListener);
    }
}
