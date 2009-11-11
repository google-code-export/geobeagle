package com.google.code.geobeagle;

import android.content.SharedPreferences;

public interface GeoFixProvider {

    public GeoFix getLocation();

    public void addObserver(Refresher refresher);

    public void onResume(SharedPreferences sharedPreferences);

    public void onPause();

    //TODO: Rename to "areUpdatesEnabled" or something
    public boolean isProviderEnabled();

    public float getAzimuth();
}
