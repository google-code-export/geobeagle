package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.database.CachesProviderSorted;
import com.google.code.geobeagle.database.ICachesProviderCenter;

/** Sends location and azimuth updates to CacheList */
public class CacheListPositionUpdater implements Refresher {
    private final CacheListAdapter mCacheList;
    private final GeoFixProvider mGeoFixProvider;
    private final ICachesProviderCenter mSearchCenter;
    private final CachesProviderSorted mSortCenter;

    public CacheListPositionUpdater(GeoFixProvider geoFixProvider,
            CacheListAdapter cacheList, ICachesProviderCenter searchCenter,
            CachesProviderSorted sortCenter) {
        mGeoFixProvider = geoFixProvider;
        mCacheList = cacheList;
        mSearchCenter = searchCenter;
        mSortCenter = sortCenter;
    }

    public void refresh() {
        final GeoFix location = mGeoFixProvider.getLocation();
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
