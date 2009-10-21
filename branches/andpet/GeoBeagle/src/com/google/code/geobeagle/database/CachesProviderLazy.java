package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.activity.main.GeoUtils;

import java.util.ArrayList;

//TODO: Not finished
public class CachesProviderLazy implements ICachesProviderCenter {

    private final ICachesProviderCenter mProvider;
    /** The position mBufferedList reflects */
    private double mBufferedLat;
    private double mBufferedLon;
    private ArrayList<Geocache> mBufferedList;
    
    /** The current center as asked for by the user of this object */
    private double mLastLat;
    private double mLastLon;
    
    private long mLastUpdateTime;
    private boolean mHasChanged;
    private double mMinDistanceKm;
    private Time mTime;
    private final long mMinTimeDiff;
    
    public CachesProviderLazy(ICachesProviderCenter provider, double minDistanceKm, 
            long minTimeDiff, Time time) {
        mProvider = provider;
        mMinDistanceKm = minDistanceKm;
        mMinTimeDiff = minTimeDiff;
        mBufferedList = null;
    }
    
    @Override
    public void setCenter(double latitude, double longitude) {
        mLastLat = latitude;
        mLastLon = longitude;
        
        if (mBufferedList == null)
            return;
        if (mTime.getCurrentTime() - mLastUpdateTime < mMinTimeDiff)
            return;
        
        if (GeoUtils.distanceKm(mBufferedLat, mBufferedLon, latitude, longitude) > mMinDistanceKm) {
            mProvider.setCenter(latitude, longitude);
            mBufferedList = null;
        }
    }

    @Override
    public ArrayList<Geocache> getCaches() {
        if (mBufferedList == null) {
            mBufferedList = mProvider.getCaches();
            mProvider.resetChanged();
            mLastUpdateTime = mTime.getCurrentTime();
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
        if (mTime.getCurrentTime() - mLastUpdateTime >= mMinTimeDiff) {
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
