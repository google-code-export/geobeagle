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
import com.google.code.geobeagle.activity.cachelist.CacheListView;
import com.google.code.geobeagle.activity.cachelist.CacheListView.ScrollListener;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.database.CachesProvider;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;

import android.app.ListActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

public class GeocacheListPresenter {
    private final DistanceFormatterManager mDistanceFormatterManager;
    private final CacheList mGeocacheListAdapter;
    private final GeocacheSummaryRowInflater mGeocacheSummaryRowInflater;
    private final View mGpsStatusWidget;
    private final ListActivity mListActivity;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;
    private final CacheListView.ScrollListener mScrollListener;
    private final CachesProvider mCachesProvider;
    
    public GeocacheListPresenter(
            DistanceFormatterManager distanceFormatterManager,
            CacheList geocacheListAdapter,
            GeocacheSummaryRowInflater geocacheSummaryRowInflater,
            View gpsStatusWidget, ListActivity listActivity,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable, ScrollListener scrollListener,
            CachesProvider cachesProvider) {
        mDistanceFormatterManager = distanceFormatterManager;
        mGeocacheListAdapter = geocacheListAdapter;
        mGeocacheSummaryRowInflater = geocacheSummaryRowInflater;
        mGpsStatusWidget = gpsStatusWidget;
        mListActivity = listActivity;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mScrollListener = scrollListener;
        mCachesProvider = cachesProvider;
    }

    public void onCreate() {
        mListActivity.setContentView(R.layout.cache_list);
        final ListView listView = mListActivity.getListView();
        listView.addHeaderView(mGpsStatusWidget);
        mListActivity.setListAdapter(mGeocacheListAdapter);
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                mCachesProvider));
        listView.setOnScrollListener(mScrollListener);
        mUpdateGpsWidgetRunnable.run();
    }

    public void onResume() {
        mDistanceFormatterManager.setFormatter();

        final boolean absoluteBearing = PreferenceManager
                .getDefaultSharedPreferences(mListActivity).getBoolean("absolute-bearing", false);
        mGeocacheSummaryRowInflater.setBearingFormatter(absoluteBearing);
    }
}
