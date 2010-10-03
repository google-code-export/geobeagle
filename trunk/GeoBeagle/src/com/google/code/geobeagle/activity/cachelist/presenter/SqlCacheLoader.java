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
import com.google.code.geobeagle.database.filter.FilterNearestCaches;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.location.Location;

import java.util.ArrayList;

public class SqlCacheLoader implements RefreshAction {
    private final CacheListData cacheListData;
    private final FilterNearestCaches filterNearestCaches;
    private final Provider<DbFrontend> dbFrontendProvider;
    private final LocationControlBuffered locationControlBuffered;
    private final Timing timing;
    private final TitleUpdater titleUpdater;
    private final ActivityVisible activityVisible;

    @Inject
    public SqlCacheLoader(Provider<DbFrontend> dbFrontendProvider,
            FilterNearestCaches filterNearestCaches, CacheListData cacheListData,
            LocationControlBuffered locationControlBuffered, TitleUpdater titleUpdater,
            Timing timing, ActivityVisible activityVisible) {
        this.dbFrontendProvider = dbFrontendProvider;
        this.filterNearestCaches = filterNearestCaches;
        this.cacheListData = cacheListData;
        this.locationControlBuffered = locationControlBuffered;
        this.timing = timing;
        this.titleUpdater = titleUpdater;
        this.activityVisible = activityVisible;
    }

    @Override
    public void refresh() {
        if (!activityVisible.getVisible())
            return;
        Location location = locationControlBuffered.getLocation();
        double latitude = 0;
        double longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        // Log.d("GeoBeagle", "Location: " + location);
        DbFrontend dbFrontend = dbFrontendProvider.get();
        ArrayList<Geocache> geocaches = dbFrontend.loadCaches(latitude, longitude,
                filterNearestCaches.getWhereFactory());
        timing.lap("SQL time");

        cacheListData.add(geocaches, locationControlBuffered);
        timing.lap("add to list time");

        int nearestCachesCount = cacheListData.size();
        titleUpdater.update(dbFrontend.countAll(), nearestCachesCount);
    }
}
