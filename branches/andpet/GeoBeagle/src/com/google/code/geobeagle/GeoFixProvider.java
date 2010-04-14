package com.google.code.geobeagle;

public interface GeoFixProvider extends IPausable {

    //From IPausableAndResumable:
    public void onResume();
    public void onPause();

    public GeoFix getLocation();

    public void addObserver(Refresher refresher);

    //TODO: Rename to "areUpdatesEnabled" or something
    public boolean isProviderEnabled();

    /* Returns the direction the device is pointing, measured in degrees. */
    public float getAzimuth();
}
