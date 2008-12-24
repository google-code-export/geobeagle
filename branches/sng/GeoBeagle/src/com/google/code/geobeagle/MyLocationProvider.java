
package com.google.code.geobeagle;

import android.app.AlertDialog;
import android.location.Location;

public class MyLocationProvider {
    private final GpsControl mGpsControl;
    private final AlertDialog mDlgError;

    public MyLocationProvider(GpsControl gpsControl, AlertDialog dlgError) {
        mGpsControl = gpsControl;
        mDlgError = dlgError;
    }

    public Location getLocation() {
        Location location = mGpsControl.getLocation();
        if (null == location) {
            mDlgError
                    .setMessage("Location cannot be determined.  Please ensure that your GPS is enabled and try again.");
            mDlgError.show();
        }
        return location;
    }
}
