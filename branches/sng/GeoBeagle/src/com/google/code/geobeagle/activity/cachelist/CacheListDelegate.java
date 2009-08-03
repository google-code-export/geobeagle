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
import com.google.code.geobeagle.activity.PausableWithDatabase;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.ISQLiteDatabase;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class CacheListDelegate implements PausableWithDatabase {
    private final ActivitySaver mActivitySaver;
    private final CacheListRefreshFactory mCacheListRefreshFactory;
    private IGeocacheListController mController;
    private final GeocacheListControllerFactory mGeocacheListControllerFactory;
    private final GeocacheListControllerNull mGeocacheListControllerNull;
    private final GeocacheListPresenter mPresenter;
    private final TitleUpdaterFactory mTitleUpdaterFactory;

    public CacheListDelegate(ActivitySaver activitySaver,
            CacheListRefreshFactory cacheListRefreshFactory,
            GeocacheListControllerFactory geocacheListControllerFactory,
            GeocacheListControllerNull geocacheListControllerNull,
            GeocacheListPresenter geocacheListPresenter,
            TitleUpdaterFactory titleUpdaterFactory) {
        mActivitySaver = activitySaver;
        mCacheListRefreshFactory = cacheListRefreshFactory;
        mController = geocacheListControllerNull;
        mGeocacheListControllerFactory = geocacheListControllerFactory;
        mGeocacheListControllerNull = geocacheListControllerNull;
        mPresenter = geocacheListPresenter;
        mTitleUpdaterFactory = titleUpdaterFactory;
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
        mController = mGeocacheListControllerNull;
    }

    public void onResume(ISQLiteDatabase sqliteDatabase) {
        final TitleUpdater titleUpdater = mTitleUpdaterFactory.create(sqliteDatabase);
        final CacheListRefresh cacheListRefresh = mCacheListRefreshFactory.create(titleUpdater,
                sqliteDatabase);

        mPresenter.onResume(cacheListRefresh);
        mController = mGeocacheListControllerFactory.create(cacheListRefresh, titleUpdater,
                sqliteDatabase);
        mController.onResume(cacheListRefresh);
    }
}
