package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;

/** Uses a DB to fetch the caches within a defined region, or all caches if no 
 * bounds were specified */
public class CachesProviderArea implements ICachesProviderArea {

    private DbFrontend mDbFrontend;
    private double mLatLow;
    private double mLonLow;
    private double mLatHigh;
    private double mLonHigh;
    private String mWhere = null;
    private ArrayList<Geocache> mCaches;
    private boolean mHasChanged = true;
    private String mExtraCondition = null;
    private boolean mHasLimits = false;

    public CachesProviderArea(DbFrontend dbFrontend) {
        mDbFrontend = dbFrontend;
        //TODO: Must initialize bounds too?
    }

    @Override
    public void setExtraCondition(String condition) {
        if (condition == null) {
            if (mExtraCondition == null) 
                return;
        } else if (condition.equals(mExtraCondition)) {
            return;
        }

        //Log.d("GeoBeagle", "area.setExtraCondition " + condition);
        mWhere = null;
        mExtraCondition = condition;
        mHasChanged = true;
        mCaches = null;
    }
    
    private String getWhere() {
        if (mWhere == null) {
            if (mHasLimits) {
                mWhere = "Latitude >= " + mLatLow + " AND Latitude < " + mLatHigh + 
                " AND Longitude >= " + mLonLow + " AND Longitude < " + mLonHigh;
                if (mExtraCondition != null)
                    mWhere += " AND " + mExtraCondition;
            } else {
                if (mExtraCondition != null)
                    mWhere = mExtraCondition;
            }
        }
        return mWhere;
    }
    
    @Override
    public ArrayList<Geocache> getCaches() {
        if (mCaches == null) {
            mCaches = mDbFrontend.loadCaches(getWhere());
        }
        return mCaches;
    }

    @Override
    public int getCount() {
        if (mCaches == null) {
            return mDbFrontend.count(getWhere());
        }
        return mCaches.size();
    }

    @Override
    public void setBounds(double latLow, double lonLow, double latHigh, double lonHigh) {
        //TODO: OK to compare doubles?
        if (latLow == mLatLow && latHigh == mLatHigh 
                && lonLow == mLonLow && lonHigh == mLonHigh) {
            return;
        }
        mLatLow = latLow;
        mLatHigh = latHigh;
        mLonLow = lonLow;
        mLonHigh = lonHigh;
        mCaches = null;  //Flush old caches
        mWhere = null;
        mHasChanged = true;
        mHasLimits = true;
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged;
    }

    @Override
    public void setChanged(boolean changed) {
        mHasChanged = changed;
    }
}
