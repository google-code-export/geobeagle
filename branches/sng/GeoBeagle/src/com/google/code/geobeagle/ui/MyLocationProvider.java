
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.GpsControl;
import com.google.code.geobeagle.R;

import android.location.Location;

public class MyLocationProvider {
    private final GpsControl mGpsControl;
    private final ErrorDisplayer mErrorDisplayer;

    public MyLocationProvider(GpsControl gpsControl, ErrorDisplayer errorDisplayer) {
        mGpsControl = gpsControl;
        mErrorDisplayer = errorDisplayer;
    }

    public Location getLocation() {
        Location location = mGpsControl.getLocation();
        if (null == location) {
            mErrorDisplayer.displayError(R.string.error_cant_get_location);
        }
        return location;
    }
}
