
package com.google.code.geobeagle;

import com.google.code.geobeagle.ui.LocationViewer;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
/*
 * Listener for the Location control.
 */
public class GeoBeagleLocationListener implements LocationListener {
    private final LocationViewer mLocationViewer;
    private final LocationControl mGpsControl;

    public GeoBeagleLocationListener(LocationControl locationControl, LocationViewer locationViewer) {
        mLocationViewer = locationViewer;
        mGpsControl = locationControl;
    }

    public void onLocationChanged(Location location) {
        mLocationViewer.setLocation(mGpsControl.getLocation());
    }

    public void onProviderDisabled(String provider) {
        mLocationViewer.setDisabled();
    }

    public void onProviderEnabled(String provider) {
        mLocationViewer.setEnabled();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        mLocationViewer.setStatus(provider, status);
    }
}
