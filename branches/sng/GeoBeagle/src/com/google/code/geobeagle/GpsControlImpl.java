
package com.google.code.geobeagle;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class GpsControlImpl implements GpsControl {
    private final LocationManager mLocationManager;
    private final LocationListener mLocationListener;

    public GpsControlImpl(LocationManager locationManager, LocationListener locationListener) {
        this.mLocationManager = locationManager;
        this.mLocationListener = locationListener;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.android.geobrowse.GpsControlI#getLocation(android.content.Context)
     */
    public Location getLocation() {
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public void onPause() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    public void onResume() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                mLocationListener);
    }
}
