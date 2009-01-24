
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;

import android.location.Location;

public class MyLocationProvider {
    private final ErrorDisplayer mErrorDisplayer;
    private final LocationControl mLocationControl;

    public MyLocationProvider(LocationControl locationControl, ErrorDisplayer errorDisplayer) {
        mLocationControl = locationControl;
        mErrorDisplayer = errorDisplayer;
    }

    public Location getLocation() {
        Location location = mLocationControl.getLocation();
        if (null == location) {
            mErrorDisplayer.displayError(R.string.error_cant_get_location);
        }
        return location;
    }
}
