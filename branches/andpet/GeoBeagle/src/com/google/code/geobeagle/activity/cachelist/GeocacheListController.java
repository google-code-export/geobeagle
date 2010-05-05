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

import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.actions.CacheFilterUpdater;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class GeocacheListController {

    public static final String SELECT_CACHE = "SELECT_CACHE";
    private final CacheListAdapter mCacheListAdapter;
    private final MenuActions mMenuActions;
    private final MenuActionSyncGpx mMenuActionSyncGpx;
    private final CacheAction mDefaultCacheAction;
    private final CacheFilterUpdater mCacheFilterUpdater;
    
    public GeocacheListController(CacheListAdapter cacheListAdapter,
            MenuActionSyncGpx menuActionSyncGpx, 
            MenuActions menuActions,
            CacheAction defaultCacheAction, 
            CacheFilterUpdater cacheFilterUpdater) {
        mCacheListAdapter = cacheListAdapter;
        mMenuActionSyncGpx = menuActionSyncGpx;
        mMenuActions = menuActions;
        mDefaultCacheAction = defaultCacheAction;
        mCacheFilterUpdater = cacheFilterUpdater;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuActions.onCreateOptionsMenu(menu);
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position > 0) {
            mDefaultCacheAction.act(mCacheListAdapter.getGeocacheAt(position - 1));
        } else {
            mCacheListAdapter.forceRefresh();
        }
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        return mMenuActions.onMenuOpened(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

    public void onPause() {
        mMenuActionSyncGpx.abort();
    }

    public void onResume(boolean fImport) {
        mCacheFilterUpdater.loadActiveFilter();
        mCacheListAdapter.forceRefresh();
        if (fImport)
            mMenuActionSyncGpx.act();
    }
}
