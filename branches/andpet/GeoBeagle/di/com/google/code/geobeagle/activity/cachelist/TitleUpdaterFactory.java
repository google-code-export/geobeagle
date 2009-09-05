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

import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI.Timing;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.activity.cachelist.presenter.ListTitleFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.code.geobeagle.database.GeocachesSql;
import com.google.code.geobeagle.database.ISQLiteDatabase;

import android.app.ListActivity;

public class TitleUpdaterFactory {

    private final CacheListData mCacheListData;
    private final FilterNearestCaches mFilterNearestCaches;
    private final ListActivity mListActivity;
    private final ListTitleFormatter mListTitleFormatter;
    private final Timing mTiming;

    public TitleUpdaterFactory(CacheListData cacheListData, FilterNearestCaches filterNearestCaches,
            ListActivity listActivity, ListTitleFormatter listTitleFormatter, Timing timing) {
        mListActivity = listActivity;
        mFilterNearestCaches = filterNearestCaches;
        mCacheListData = cacheListData;
        mListTitleFormatter = listTitleFormatter;
        mTiming = timing;
    }

    public TitleUpdater create(ISQLiteDatabase sqliteWrapper) {
        final GeocachesSql geocachesSql = DatabaseDI.createGeocachesSql(sqliteWrapper);
        return new TitleUpdater(geocachesSql, mListActivity, mFilterNearestCaches, mCacheListData,
                mListTitleFormatter, mTiming);
    }

}
