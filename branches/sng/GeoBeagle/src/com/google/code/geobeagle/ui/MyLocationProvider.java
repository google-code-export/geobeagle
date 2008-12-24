
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.GpsControl;
import com.google.code.geobeagle.R;

import android.location.Location;

public class MyLocationProvider {
    private final GpsControl mGpsControl;
    private final ErrorDialog mErrorDialog;

    public MyLocationProvider(GpsControl gpsControl, ErrorDialog errorDialog) {
        mGpsControl = gpsControl;
        mErrorDialog = errorDialog;
    }

    public Location getLocation() {
        Location location = mGpsControl.getLocation();
        if (null == location) {
            mErrorDialog.show(R.string.error_cant_get_location);
        }
        return location;
    }
}