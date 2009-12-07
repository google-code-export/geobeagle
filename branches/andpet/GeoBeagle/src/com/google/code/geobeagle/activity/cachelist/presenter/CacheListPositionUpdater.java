package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.database.CachesProviderCenterThread;
import com.google.code.geobeagle.database.ICachesProviderCenter;

import android.util.Log;

/** Sends location and azimuth updates to CacheList */
public class CacheListPositionUpdater implements Refresher {
    private final CacheListAdapter mCacheList;
    private final GeoFixProvider mGeoFixProvider;
    private final ICachesProviderCenter mSearchCenter;
    private final CachesProviderCenterThread  mSortCenterThread;

    public CacheListPositionUpdater(GeoFixProvider geoFixProvider,
            CacheListAdapter cacheList, ICachesProviderCenter searchCenter,
            CachesProviderCenterThread sortCenter) {
        mGeoFixProvider = geoFixProvider;
        mCacheList = cacheList;
        mSearchCenter = searchCenter;
        mSortCenterThread = sortCenter;
    }

    public void refresh() {
        Log.d("GeoBeagle", "refresh()");
        final GeoFix location = mGeoFixProvider.getLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mSearchCenter.setCenter(latitude, longitude);
            mSortCenterThread.setCenter(latitude, longitude, mCacheList);
        }
    }

    @Override
    public void forceRefresh() {
        refresh();
    }
}
