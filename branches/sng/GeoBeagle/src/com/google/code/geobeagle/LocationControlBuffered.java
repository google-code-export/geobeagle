
package com.google.code.geobeagle;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationControlBuffered implements LocationListener {
    LocationControl mLocationControl;
    Location mLocation;

    LocationControlBuffered(LocationControl locationControl) {
        mLocationControl = locationControl;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void onLocationChanged(Location location) {
        mLocation = mLocationControl.getLocation();
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
