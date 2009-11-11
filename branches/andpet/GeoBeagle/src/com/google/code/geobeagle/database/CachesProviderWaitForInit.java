package com.google.code.geobeagle.database;

import com.google.code.geobeagle.GeocacheList;

public class CachesProviderWaitForInit implements ICachesProviderCenter {
    private final ICachesProviderCenter mProvider;
    private boolean mInited = false;
    private static final GeocacheList EMPTYLIST = new GeocacheList();
    
    public CachesProviderWaitForInit(ICachesProviderCenter provider) {
        mProvider = provider;
    }
    
    @Override
    public void setCenter(double latitude, double longitude) {
        mInited = true;
        mProvider.setCenter(latitude, longitude);
    }

    @Override
    public GeocacheList getCaches() {
        if (!mInited)
            return EMPTYLIST;
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
