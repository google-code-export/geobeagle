
package com.google.code.geobeagle;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GpsLocationListener implements LocationListener {
    private final LocationViewer mLocationViewer;

    public GpsLocationListener(LocationViewer locationViewer) {
        this.mLocationViewer = locationViewer;
    }

    public void onLocationChanged(Location location) {
        mLocationViewer.setLocation(location);
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        mLocationViewer.setStatus(status);
    }
}
