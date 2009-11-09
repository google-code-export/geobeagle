package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.LocationAndDirection;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.database.CachesProviderSorted;
import com.google.code.geobeagle.database.ICachesProviderCenter;

import android.location.Location;

/** Sends location and azimuth updates to CacheList */
public class CacheListPositionUpdater implements Refresher {
    private final CacheListAdapter mCacheList;
    private final LocationAndDirection mLocationAndDirection;
    private final ICachesProviderCenter mSearchCenter;
    private final CachesProviderSorted mSortCenter;

    public CacheListPositionUpdater(LocationAndDirection locationAndDirection,
            CacheListAdapter cacheList, ICachesProviderCenter searchCenter,
            CachesProviderSorted sortCenter) {
        mLocationAndDirection = locationAndDirection;
        mCacheList = cacheList;
        mSearchCenter = searchCenter;
        mSortCenter = sortCenter;
    }

    public void refresh() {
        final Location location = mLocationAndDirection.getLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mSearchCenter.setCenter(latitude, longitude);
            mSortCenter.setCenter(latitude, longitude);
            mCacheList.refresh();
        }
    }

    @Override
    public void forceRefresh() {
        refresh();
    }
}
