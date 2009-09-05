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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI.Timing;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.activity.cachelist.presenter.SqlCacheLoader;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.code.geobeagle.database.GeocachesSql;

public class SqlCacheLoaderFactory {

    private final CacheListData mCacheListData;
    private final FilterNearestCaches mFilterNearestCaches;
    private final LocationControlBuffered mLocationControlBuffered;
    private final Timing mTiming;

    public SqlCacheLoaderFactory(CacheListData cacheListData,
            FilterNearestCaches filterNearestCaches, LocationControlBuffered locationControlBuffered,
            Timing timing) {
        mFilterNearestCaches = filterNearestCaches;
        mCacheListData = cacheListData;
        mLocationControlBuffered = locationControlBuffered;
        mTiming = timing;
    }

    public SqlCacheLoader create(GeocachesSql geocachesSql, TitleUpdater titleUpdater) {
        return new SqlCacheLoader(geocachesSql, mFilterNearestCaches, mCacheListData,
                mLocationControlBuffered, titleUpdater, mTiming);
    }

}
