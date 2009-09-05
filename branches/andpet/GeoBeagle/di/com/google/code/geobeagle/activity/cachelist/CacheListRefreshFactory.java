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
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.SqlCacheLoader;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.GeocachesSql;
import com.google.code.geobeagle.database.ISQLiteDatabase;

public class CacheListRefreshFactory {

    private final ActionManagerFactory mActionManagerFactory;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SqlCacheLoaderFactory mSqlCacheLoaderFactory;
    private final Timing mTiming;

    public CacheListRefreshFactory(ActionManagerFactory actionManagerFactory,
            LocationControlBuffered locationControlBuffered,
            SqlCacheLoaderFactory sqlCacheLoaderFactory, Timing timing) {
        mTiming = timing;
        mLocationControlBuffered = locationControlBuffered;
        mActionManagerFactory = actionManagerFactory;
        mSqlCacheLoaderFactory = sqlCacheLoaderFactory;
    }

    public CacheListRefresh create(TitleUpdater titleUpdater, ISQLiteDatabase sqliteWrapper) {
        final GeocachesSql geocachesSql = DatabaseDI.createGeocachesSql(sqliteWrapper);
        final SqlCacheLoader sqlCacheLoader = mSqlCacheLoaderFactory.create(geocachesSql,
                titleUpdater);
        final ActionManager actionManager = mActionManagerFactory.create(sqlCacheLoader);
        return new CacheListRefresh(actionManager, mTiming, mLocationControlBuffered, sqliteWrapper);
    }
}
