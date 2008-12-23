
package com.google.code.geobeagle;

import android.location.Location;

public interface GpsControl {
    public abstract Location getLocation();

    public abstract void onPause();

    public abstract void onResume();
}
