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

import com.google.code.geobeagle.IPausable;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.database.CachesProviderDb;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class CacheListDelegate {
    static class ImportIntentManager {
        static final String INTENT_EXTRA_IMPORT_TRIGGERED = "com.google.code.geabeagle.import_triggered";
        private final Activity mActivity;

        ImportIntentManager(Activity activity) {
            mActivity = activity;
        }

        boolean isImport() {
            final Intent intent = mActivity.getIntent();
            if (intent == null)
                return false;

            String action = intent.getAction();
            if (action == null)
                return false;

            if (!action.equals("android.intent.action.VIEW"))
                return false;

            if (intent.getBooleanExtra(INTENT_EXTRA_IMPORT_TRIGGERED, false))
                return false;

            // Need to alter the intent so that the import isn't retriggered if
            // pause/resume is a result of the phone going to sleep and then
            // waking up again.
            intent.putExtra(INTENT_EXTRA_IMPORT_TRIGGERED, true);
            return true;
        }
    }

    private final ActivitySaver mActivitySaver;
    private final GeocacheListController mController;
    private final DbFrontend mDbFrontend;
    private final ImportIntentManager mImportIntentManager;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;
    private final CacheListView.ScrollListener mScrollListener;
    private final CacheListOnCreateContextMenuListener mMenuCreator;
    private final CacheListAdapter mCacheList;
    private final View mGpsStatusWidget;
    private final ListActivity mListActivity;
    private final DistanceFormatterManager mDistanceFormatterManager;
    private final GeocacheSummaryRowInflater mGeocacheSummaryRowInflater;
    private final CachesProviderDb mCachesToFlush;
    private final IPausable[] mPausables;

    public CacheListDelegate(ImportIntentManager importIntentManager, ActivitySaver activitySaver,
            GeocacheListController geocacheListController,
            DbFrontend dbFrontend,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable,
            View gpsStatusWidget,
            CacheListOnCreateContextMenuListener menuCreator,
            CacheListAdapter cacheList,
            GeocacheSummaryRowInflater geocacheSummaryRowInflater,
            ListActivity listActivity,
            CacheListView.ScrollListener scrollListener,
            DistanceFormatterManager distanceFormatterManager,
            CachesProviderDb cachesToFlush,
            IPausable[] pausables) {
        mActivitySaver = activitySaver;
        mController = geocacheListController;
        mImportIntentManager = importIntentManager;
        mDbFrontend = dbFrontend;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mGpsStatusWidget = gpsStatusWidget;
        mMenuCreator = menuCreator;
        mCacheList = cacheList;
        mGeocacheSummaryRowInflater = geocacheSummaryRowInflater;
        mListActivity = listActivity;
        mScrollListener = scrollListener;
        mDistanceFormatterManager = distanceFormatterManager;
        mCachesToFlush = cachesToFlush;
        mPausables = pausables;
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        return mController.onContextItemSelected(menuItem);
    }

    public void onCreate() {
        mListActivity.setContentView(R.layout.cache_list);
        final ListView listView = mListActivity.getListView();
        listView.addHeaderView(mGpsStatusWidget);
        mListActivity.setListAdapter(mCacheList);
        listView.setOnCreateContextMenuListener(mMenuCreator);
        listView.setOnScrollListener(mScrollListener);
        mUpdateGpsWidgetRunnable.run();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mController.onCreateOptionsMenu(menu);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        mController.onListItemClick(l, v, position, id);
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        return mController.onMenuOpened(featureId, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mController.onOptionsItemSelected(item);
    }

    public void onPause() {
        for (IPausable pausable : mPausables)
            pausable.onPause();
        mController.onPause();
        mActivitySaver.save(ActivityType.CACHE_LIST);
        mDbFrontend.closeDatabase();
    }

    public void onResume() {
        Log.d("GeoBeagle", "CacheListDelegate.onResume()");
        mDistanceFormatterManager.setFormatter();
        final SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(mListActivity);
        final boolean absoluteBearing = sharedPreferences.getBoolean("absolute-bearing", false);
        mGeocacheSummaryRowInflater.setBearingFormatter(absoluteBearing);

        mController.onResume(mImportIntentManager.isImport());
        for (IPausable pausable : mPausables)
            pausable.onResume();
    }

    public void onActivityResult() {
        Log.d("GeoBeagle", "CacheListDelegate.onActivityResult()");
        mCachesToFlush.notifyOfDbChange();
        mCacheList.forceRefresh();
    }
}
