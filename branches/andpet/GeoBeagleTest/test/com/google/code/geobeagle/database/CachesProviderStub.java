package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheList;

import java.util.ArrayList;

public class CachesProviderStub implements ICachesProviderArea {

    private GeocacheList mGeocaches = new GeocacheList();
    private double mLatLow = 0.0;
    private double mLatHigh = 0.0;
    private double mLonLow = 0.0;
    private double mLonHigh = 0.0;
    private boolean mHasChanged = true;

    private int mBoundsCalls = 0;
    private boolean mIsInitialized = false;
    
    public void addCache(Geocache geocache) {
        mGeocaches.add(geocache);
    }

    private GeocacheList fetchCaches() {
        if (!mIsInitialized)
            return mGeocaches;
        
        ArrayList<Geocache> selection = new ArrayList<Geocache>();
        for (Geocache geocache : mGeocaches) {
            if (geocache.getLatitude() >= mLatLow
                && geocache.getLatitude() <= mLatHigh
                && geocache.getLongitude() >= mLonLow
                && geocache.getLongitude() <= mLonHigh) {
                selection.add(geocache);
            }
        }
        return new GeocacheList(selection);
    }
    
    @Override
    public GeocacheList getCaches() {
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
        mIsInitialized = true;
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged;
    }

    @Override
    public void resetChanged() {
        mHasChanged = false;
    }

    public void setChanged(boolean changed) {
        mHasChanged = changed;
    }
}
