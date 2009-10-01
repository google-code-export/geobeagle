package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;

public class CachesProviderStub implements ICachesProviderArea {

    private ArrayList<Geocache> mGeocaches = new ArrayList<Geocache>();
    private double mLatLow = 0.0;
    private double mLatHigh = 0.0;
    private double mLonLow = 0.0;
    private double mLonHigh = 0.0;
    private boolean mHasChanged = true;

    private int mBoundsCalls = 0;
    
    public void addCache(Geocache geocache) {
        mGeocaches.add(geocache);
    }

    private ArrayList<Geocache> fetchCaches() {
        ArrayList<Geocache> selection = new ArrayList<Geocache>();
        for (Geocache geocache : mGeocaches) {
            if (geocache.getLatitude() >= mLatLow
                && geocache.getLatitude() <= mLatHigh
                && geocache.getLongitude() >= mLonLow
                && geocache.getLongitude() <= mLonHigh) {
                selection.add(geocache);
            }
        }
        return selection;
    }
    
    @Override
    public ArrayList<Geocache> getCaches() {
        return fetchCaches();
    }

    @Override
    public int getCount() {
        return fetchCaches().size();
    }

    public int getSetBoundsCalls() {
        return mBoundsCalls;
    }

    @Override
    public void setBounds(double latLow, double lonLow, double latHigh, double lonHigh) {
        mBoundsCalls += 1;
        mLatLow = latLow;
        mLatHigh = latHigh;
        mLonLow = lonLow;
        mLonHigh = lonHigh;
        mHasChanged = true;
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged;
    }

    @Override
    public void setChanged(boolean changed) {
        mHasChanged = changed;
    }

    @Override
    public void setExtraCondition(String condition) {
    }
}
