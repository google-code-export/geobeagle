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

package com.google.code.geobeagle.cachelistactivity.presenter;

import com.google.code.geobeagle.cachelistactivity.CacheListDelegateDI;
import com.google.code.geobeagle.cachelistactivity.model.CacheListData;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.code.geobeagle.database.GeocachesSql;

import android.app.ListActivity;
import android.widget.TextView;

public class TitleUpdater {
    private final CacheListData mCacheListData;
    private final FilterNearestCaches mFilterNearestCaches;
    private final GeocachesSql mGeocachesSql;
    private final ListActivity mListActivity;
    private final ListTitleFormatter mListTitleFormatter;
    private final CacheListDelegateDI.Timing mTiming;

    public TitleUpdater(GeocachesSql geocachesSql, ListActivity listActivity,
            FilterNearestCaches filterNearestCaches, CacheListData cacheListData,
            ListTitleFormatter listTitleFormatter, CacheListDelegateDI.Timing timing) {
        mGeocachesSql = geocachesSql;
        mListActivity = listActivity;
        mFilterNearestCaches = filterNearestCaches;
        mCacheListData = cacheListData;
        mListTitleFormatter = listTitleFormatter;
        mTiming = timing;
    }

    public void update() {
        final int sqlCount = mGeocachesSql.getCount();
        final int nearestCachesCount = mCacheListData.size();
        mListActivity.setTitle(mListActivity.getString(mFilterNearestCaches.getTitleText(),
                nearestCachesCount, sqlCount));
        if (0 == nearestCachesCount) {
            TextView textView = (TextView)mListActivity.findViewById(android.R.id.empty);
            textView.setText(mListTitleFormatter.getBodyText(sqlCount));
        }
        mTiming.lap("update title time");
    }
}