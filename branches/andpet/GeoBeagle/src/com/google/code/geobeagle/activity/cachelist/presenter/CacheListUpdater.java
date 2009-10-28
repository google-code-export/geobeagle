package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.Refresher;

import android.location.Location;

/** Sends location and azimuth updates to CacheList */
public class CacheListUpdater implements Refresher {

    private final CacheList mCacheList;
    private final LocationControlBuffered mLocationControlBuffered;

    public CacheListUpdater(LocationControlBuffered locationControlBuffered,
            CacheList cacheList) {
        mLocationControlBuffered = locationControlBuffered;
        mCacheList = cacheList;
    }

    public void refresh() {
        final Location location = mLocationControlBuffered.getLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mCacheList.setLocation(latitude, longitude);
            mCacheList.refresh();
        }
    }

    @Override
    public void forceRefresh() {
        refresh();
    }
}
