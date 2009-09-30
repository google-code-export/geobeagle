package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;

public class CachesProviderToggler implements ICachesProviderCenter {
    private ICachesProviderCenter mCachesProviderCenter;
    private CachesProvider mCachesProviderAll;
    private boolean mNearest;
    private boolean mHasChanged;

    public CachesProviderToggler(ICachesProviderCenter cachesProviderCenter,
            CachesProvider cachesProviderAll) {
        mCachesProviderCenter = cachesProviderCenter;
        mCachesProviderAll = cachesProviderAll;
        mNearest = true;
        mHasChanged = true;
    }

    @Override
    public void setExtraCondition(String condition) {
        mCachesProviderCenter.setExtraCondition(condition);
        mCachesProviderAll.setExtraCondition(condition);
    }

    public void toggle() {
        mNearest = !mNearest;
        mHasChanged = true;
    }

    public boolean isShowingNearest() {
        return mNearest;
    }
    
    @Override
    public ArrayList<Geocache> getCaches() {
        return (mNearest ? mCachesProviderCenter : mCachesProviderAll).getCaches();
    }

    @Override
    public int getCount() {
        return (mNearest ? mCachesProviderCenter : mCachesProviderAll).getCount();
    }

    @Override
    public void setCenter(double latitude, double longitude) {
        mCachesProviderCenter.setCenter(latitude, longitude);
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged || 
            (mNearest ? mCachesProviderCenter : mCachesProviderAll).hasChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        mHasChanged = changed;
        if (!changed) {
            (mNearest ? mCachesProviderCenter : mCachesProviderAll).setChanged(false);
        }
    }
}
