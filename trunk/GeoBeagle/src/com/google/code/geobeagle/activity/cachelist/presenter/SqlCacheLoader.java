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
import com.google.code.geobeagle.Timing;
import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.location.Location;

import java.util.ArrayList;

public class SqlCacheLoader implements RefreshAction {
    private final CacheListData mCacheListData;
    private final FilterNearestCaches mFilterNearestCaches;
    private final Provider<DbFrontend> mDbFrontendProvider;
    private final LocationControlBuffered mLocationControlBuffered;
    private final Timing mTiming;
    private final TitleUpdater mTitleUpdater;
    private final ActivityVisible mActivityVisible;

    @Inject
    public SqlCacheLoader(Provider<DbFrontend> dbFrontendProvider,
            FilterNearestCaches filterNearestCaches, CacheListData cacheListData,
            LocationControlBuffered locationControlBuffered, TitleUpdater titleUpdater,
            Timing timing, ActivityVisible activityVisible) {
        mDbFrontendProvider = dbFrontendProvider;
        mFilterNearestCaches = filterNearestCaches;
        mCacheListData = cacheListData;
        mLocationControlBuffered = locationControlBuffered;
        mTiming = timing;
        mTitleUpdater = titleUpdater;
        mActivityVisible = activityVisible;
    }

    public void refresh() {
        if (!mActivityVisible.getVisible())
            return;
        final Location location = mLocationControlBuffered.getLocation();
        double latitude = 0;
        double longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        // Log.d("GeoBeagle", "Location: " + location);
        DbFrontend dbFrontend = mDbFrontendProvider.get();
        ArrayList<Geocache> geocaches = dbFrontend.loadCaches(latitude, longitude,
                mFilterNearestCaches.getWhereFactory());
        mTiming.lap("SQL time");

        mCacheListData.add(geocaches, mLocationControlBuffered);
        mTiming.lap("add to list time");

        final int nearestCachesCount = mCacheListData.size();
        mTitleUpdater.update(dbFrontend.countAll(), nearestCachesCount);
    }
}
