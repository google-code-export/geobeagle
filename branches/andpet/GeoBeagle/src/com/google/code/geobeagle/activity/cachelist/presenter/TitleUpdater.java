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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI;
import com.google.code.geobeagle.database.CachesProviderToggler;
import com.google.code.geobeagle.database.DbFrontend;

import android.app.ListActivity;
import android.widget.TextView;

public class TitleUpdater implements RefreshAction {
    private final CachesProviderToggler mCachesProviderToggler;
    private final ListActivity mListActivity;
    private final ListTitleFormatter mListTitleFormatter;
    private final CacheListDelegateDI.Timing mTiming;
    private final DbFrontend mDbFrontend;

    public TitleUpdater(ListActivity listActivity, CachesProviderToggler cachesProviderToggler, 
            ListTitleFormatter listTitleFormatter, CacheListDelegateDI.Timing timing,
            DbFrontend dbFrontend) {
        mListActivity = listActivity;
        mCachesProviderToggler = cachesProviderToggler;
        mListTitleFormatter = listTitleFormatter;
        mTiming = timing;
        mDbFrontend = dbFrontend;
    }

    public void refresh() {
        int sqlCount = mDbFrontend.count(null); //count all caches
        int nearestCachesCount = mCachesProviderToggler.getCount();
        int title =
            mCachesProviderToggler.isShowingNearest() ? 
                    R.string.cache_list_title : R.string.cache_list_title_all;
        
        mListActivity.setTitle(mListActivity.getString(title, nearestCachesCount, sqlCount));
        if (0 == nearestCachesCount) {
            TextView textView = (TextView)mListActivity.findViewById(android.R.id.empty);
            textView.setText(mListTitleFormatter.getBodyText(sqlCount));
        }
    }
}