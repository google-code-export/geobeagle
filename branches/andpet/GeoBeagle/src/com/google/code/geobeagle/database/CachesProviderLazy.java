package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Clock;
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.activity.main.GeoUtils;

public class CachesProviderLazy implements ICachesProviderCenter {

    private final ICachesProviderCenter mProvider;
    /** The position mBufferedList reflects */
    private double mBufferedLat;
    private double mBufferedLon;
    private GeocacheList mBufferedList;
    
    /** The current center as asked for by the user of this object */
    private double mLastLat;
    private double mLastLon;
    
    private long mLastUpdateTime;
    private boolean mHasChanged;
    private double mMinDistanceKm;
    private Clock mClock;
    private final long mMinTimeDiff;
    
    public CachesProviderLazy(ICachesProviderCenter provider, double minDistanceKm, 
            long minTimeDiff, Clock clock) {
        mProvider = provider;
        mMinDistanceKm = minDistanceKm;
        mMinTimeDiff = minTimeDiff;
        mBufferedList = null;
        mClock = clock;
    }
    
    @Override
    public void setCenter(double latitude, double longitude) {
        mLastLat = latitude;
        mLastLon = longitude;
        
        if (mBufferedList == null)
            return;
        if (mClock.getCurrentTime() - mLastUpdateTime < mMinTimeDiff)
            return;
        
        if (GeoUtils.distanceKm(mBufferedLat, mBufferedLon, latitude, longitude) > mMinDistanceKm) {
            mProvider.setCenter(latitude, longitude);
            mBufferedList = null;
        }
    }

    @Override
    public GeocacheList getCaches() {
        if (mBufferedList == null) {
            mBufferedList = mProvider.getCaches();
            mProvider.resetChanged();
            mLastUpdateTime = mClock.getCurrentTime();
        }
        return mBufferedList;
    }

    @Override
    public int getCount() {
        return getCaches().size();
    }
    
    @Override
    public boolean hasChanged() {
        if (mHasChanged)
            return true;

        //Update mHasChanged
        if (mClock.getCurrentTime() - mLastUpdateTime >= mMinTimeDiff) {
            if (GeoUtils.distanceKm(mBufferedLat, mBufferedLon, mLastLat, mLastLon) > mMinDistanceKm)
                mHasChanged = true;
            else
                mHasChanged = mProvider.hasChanged();
        }
        
        return mHasChanged;
    }

    @Override
    public void resetChanged() {
        mHasChanged = false;
    }

}
