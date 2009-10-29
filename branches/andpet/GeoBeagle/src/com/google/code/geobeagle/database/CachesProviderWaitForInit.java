package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;

public class CachesProviderWaitForInit implements ICachesProviderCenter {
    private final ICachesProviderCenter mProvider;
    private boolean mInited = false;
    
    public CachesProviderWaitForInit(ICachesProviderCenter provider) {
        mProvider = provider;
    }
    
    @Override
    public void setCenter(double latitude, double longitude) {
        mInited = true;
        mProvider.setCenter(latitude, longitude);
    }

    @Override
    public ArrayList<Geocache> getCaches() {
        if (!mInited)
            return new ArrayList<Geocache>();
        return mProvider.getCaches();
    }

    @Override
    public int getCount() {
        if (!mInited) {
            return 0;
        }
        return mProvider.getCount();
    }

    //Could cause a bug if the first setCenter() doesn't make mProvider report hasChanged()
    @Override
    public boolean hasChanged() {
        if (!mInited)
            return false;
        return mProvider.hasChanged();
    }

    @Override
    public void resetChanged() {
        if (mInited) {
            mProvider.resetChanged();
        }
    }

}
