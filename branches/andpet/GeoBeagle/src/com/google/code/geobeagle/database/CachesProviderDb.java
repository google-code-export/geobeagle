package com.google.code.geobeagle.database;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.GeocacheList;

/** Uses a DB to fetch the caches within a defined region, or all caches if no 
 * bounds were specified */
public class CachesProviderDb implements ICachesProviderArea {

    private DbFrontend mDbFrontend;
    private double mLatLow;
    private double mLonLow;
    private double mLatHigh;
    private double mLonHigh;
    private String mWhere = null;
    private GeocacheList mCaches;
    private boolean mHasChanged = true;
    private boolean mHasLimits = false;
    private final CacheFilter mCacheFilter;
    private String mFilter;
    //TODO: mIsFirstListLoad currently disabled. Beneficial to use?
    private boolean mIsFirstListLoad = false;
    /** If greater than zero, this is the max number that mCaches 
     * was allowed to contain when loaded. (This limit can change on subsequent loads) */
    private int mCachesCappedToCount = 0;

    public CachesProviderDb(DbFrontend dbFrontend, CacheFilter cacheFilter) {
        mDbFrontend = dbFrontend;
        mCacheFilter = cacheFilter;
    }

    private String getWhere() {
        if (mWhere == null) {
            if (mFilter == null)
                mFilter = mCacheFilter.getSqlWhereClause();

            if (mHasLimits) {
                mWhere = "Latitude >= " + mLatLow + " AND Latitude < " + mLatHigh + 
                " AND Longitude >= " + mLonLow + " AND Longitude < " + mLonHigh;
                if (mFilter != null)
                    mWhere += " AND " + mFilter;
            } else {
                if (mFilter != null)
                    mWhere = mFilter;
            }
        }
        return mWhere;
    }
    
    @Override
    public GeocacheList getCaches() {
        if (mCaches == null || mCachesCappedToCount > 0) {
            String where = getWhere();
            if (mIsFirstListLoad && where != null) {
                mCaches = mDbFrontend.loadCachesPrecomputed(where);
            } else {
                mCaches = mDbFrontend.loadCaches(where);
            }
            mIsFirstListLoad = false;
        }
        return mCaches;
    }

    @Override
    public GeocacheList getCaches(int maxResults) {
        if (mCaches == null || (mCachesCappedToCount > 0 && 
                                mCachesCappedToCount < maxResults)) {
            mCaches = mDbFrontend.loadCaches(getWhere(), maxResults);
            if (mCaches.size() == maxResults)
                mCachesCappedToCount = maxResults;
            else
                //The cap didn't limit the search result
                mCachesCappedToCount = 0;
        }
        return mCaches;
    }
    
    @Override
    public int getCount() {
        if (mCaches == null || mCachesCappedToCount > 0) {
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
    public void resetChanged() {
        mHasChanged = false;
    }

    /** Tells this class that the contents in the database have changed. 
     * The cached list isn't reliable any more. */
    public void notifyOfDbChange() {
        mCaches = null;
        mHasChanged = true;
    }
    
    public void reloadFilter() {
        mCacheFilter.reload();
        String newFilter = mCacheFilter.getSqlWhereClause();
        if ((newFilter == null && mFilter != null) 
                || (newFilter != null && !newFilter.equals(mFilter))) {
            mHasChanged = true;
            mFilter = newFilter;
            mWhere = null;  //Flush
            mCaches = null;  //Flush old caches
        }
    }
}
