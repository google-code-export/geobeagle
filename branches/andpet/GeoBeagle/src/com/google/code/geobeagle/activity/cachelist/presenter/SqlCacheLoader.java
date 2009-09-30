/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI.Timing;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.database.ICachesProviderCenter;

import android.location.Location;

import java.util.ArrayList;

public class SqlCacheLoader implements RefreshAction {
    private final CacheListData mCacheListData;
    private final ICachesProviderCenter mCachesProviderCenter;
    private final LocationControlBuffered mLocationControlBuffered;
    private final Timing mTiming;
    private final TitleUpdater mTitleUpdater;

    public SqlCacheLoader(ICachesProviderCenter cachesProvider,
            CacheListData cacheListData, LocationControlBuffered locationControlBuffered,
            TitleUpdater titleUpdater, Timing timing) {
        mCachesProviderCenter = cachesProvider;
        mCacheListData = cacheListData;
        mLocationControlBuffered = locationControlBuffered;
        mTiming = timing;
        mTitleUpdater = titleUpdater;
    }

    public void refresh() {
        final Location location = mLocationControlBuffered.getLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mCachesProviderCenter.setCenter(latitude, longitude);
        }
        // Log.d("GeoBeagle", "Location: " + location);
        ArrayList<Geocache> geocaches = mCachesProviderCenter.getCaches();
        mTiming.lap("SQL time");

        mCacheListData.add(geocaches, mLocationControlBuffered);
        mTiming.lap("add to list time");

        final int sqlCount = geocaches.size();
        final int nearestCachesCount = mCacheListData.size();
        mTitleUpdater.update(sqlCount, nearestCachesCount);
    }
}
