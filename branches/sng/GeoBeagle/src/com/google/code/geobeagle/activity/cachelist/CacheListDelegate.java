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

import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.database.DbFrontend;

import android.app.Activity;
import android.content.Intent;
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
    private final CacheListRefresh mCacheListRefresh;
    private final GeocacheListController mController;
    private final DbFrontend mDbFrontend;
    private final ImportIntentManager mImportIntentManager;
    private final GeocacheListPresenter mPresenter;

    public CacheListDelegate(ImportIntentManager importIntentManager, ActivitySaver activitySaver,
            CacheListRefresh cacheListRefresh, GeocacheListController geocacheListController,
            GeocacheListPresenter geocacheListPresenter, DbFrontend dbFrontend) {
        mActivitySaver = activitySaver;
        mCacheListRefresh = cacheListRefresh;
        mController = geocacheListController;
        mPresenter = geocacheListPresenter;
        mImportIntentManager = importIntentManager;
        mDbFrontend = dbFrontend;
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        return mController.onContextItemSelected(menuItem);
    }

    public void onCreate() {
        mPresenter.onCreate();
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
        mPresenter.onPause();
        mController.onPause();
        mActivitySaver.save(ActivityType.CACHE_LIST);
        mDbFrontend.closeDatabase();
    }

    public void onResume() {
        // TODO: No need to re-initialize these
        mPresenter.onResume(mCacheListRefresh);
        mController.onResume(mCacheListRefresh, mImportIntentManager.isImport());
    }
}
