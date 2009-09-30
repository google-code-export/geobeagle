package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;

public class CachesProviderRadius implements ICachesProviderCenter {

    private ICachesProviderArea mCachesProviderArea;
    private double mLatitude;
    private double mLongitude;
    private double mDegrees;

    public CachesProviderRadius(ICachesProviderArea area) {
        mCachesProviderArea = area;
    }

    @Override
    public void setExtraCondition(String condition) {
        mCachesProviderArea.setExtraCondition(condition);
    }
    
    @Override
    public ArrayList<Geocache> getCaches() {
        return mCachesProviderArea.getCaches();
    }

    @Override
    public int getCount() {
        return mCachesProviderArea.getCount();
    }

    @Override
    public boolean hasChanged() {
        return mCachesProviderArea.hasChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        mCachesProviderArea.setChanged(changed);
    }
    
    public void setRadius(double radius) {
        mDegrees = radius;
        updateBounds();
    }
    
    @Override
    public void setCenter(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        updateBounds();
    }

    private void updateBounds() {
        double latLow = mLatitude - mDegrees;
        double latHigh = mLatitude + mDegrees;
        double lonLow = mLongitude - mDegrees;
        double lonHigh = mLongitude + mDegrees;
        /*
        double lat_radians = Math.toRadians(mLatitude);
        double cos_lat = Math.cos(lat_radians);
        double lonLow = Math.max(-180, mLongitude - mDegrees / cos_lat);
        double lonHigh = Math.min(180, mLongitude + mDegrees / cos_lat);
        */
        mCachesProviderArea.setBounds(latLow, lonLow, latHigh, lonHigh);
    }
}
