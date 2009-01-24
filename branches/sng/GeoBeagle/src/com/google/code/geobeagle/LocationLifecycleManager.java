
package com.google.code.geobeagle;

import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationListener;
import android.location.LocationManager;
/*
 * Handle onPause and onResume for the LocationManager.
 */
public class LocationLifecycleManager implements LifecycleManager {
    private final LocationListener mLocationListener;
    private final LocationManager mLocationManager;

    public LocationLifecycleManager(LocationListener locationListener, LocationManager locationManager) {
        mLocationListener = locationListener;
        mLocationManager = locationManager;
    }

    /*
     * (non-Javadoc)
     * @see com.google.code.geobeagle.LifecycleManager#onPause()
     */
    public void onPause(Editor editor) {
        mLocationManager.removeUpdates(mLocationListener);
    }

    /*
     * (non-Javadoc)
     * @see com.google.code.geobeagle.LifecycleManager#onResume()
     */
    public void onResume(SharedPreferences preferences, ErrorDisplayer errorDisplayer) {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                mLocationListener);
    }

}
