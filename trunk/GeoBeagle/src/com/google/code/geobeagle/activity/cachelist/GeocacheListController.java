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

import com.google.code.geobeagle.actions.ContextActions;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class GeocacheListController {

    public static class CacheListOnCreateContextMenuListener implements OnCreateContextMenuListener {
        private final GeocacheVectors mGeocacheVectors;

        public CacheListOnCreateContextMenuListener(GeocacheVectors geocacheVectors) {
            mGeocacheVectors = geocacheVectors;
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
            if (acmi.position > 0) {
                menu.setHeaderTitle(mGeocacheVectors.get(acmi.position - 1).getId());
                menu.add(0, MENU_VIEW, 0, "View");
                menu.add(0, MENU_EDIT, 1, "Edit");
                menu.add(0, MENU_DELETE, 2, "Delete");
            }
        }
    }

    static final int MENU_DELETE = 0;
    static final int MENU_VIEW = 1;
    static final int MENU_EDIT = 2;
    public static final String SELECT_CACHE = "SELECT_CACHE";
    private final CacheListRefresh mCacheListRefresh;
    private final Aborter mAborter;
    private final Injector mInjector;

    @Inject
    public GeocacheListController(CacheListRefresh cacheListRefresh,
            Injector injector, Aborter aborter) {
        mCacheListRefresh = cacheListRefresh;
        mAborter = aborter;
        mInjector = injector;
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo)menuItem
                .getMenuInfo();
        mInjector.getInstance(ContextActions.class).act(menuItem.getItemId(),
                adapterContextMenuInfo.position - 1);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mInjector.getInstance(CacheListMenuActions.class).onCreateOptionsMenu(menu);
    }

    public void onListItemClick(int position) {
        if (position > 0)
            mInjector.getInstance(ContextActions.class).act(MENU_VIEW, position - 1);
        else
            mCacheListRefresh.forceRefresh();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mInjector.getInstance(CacheListMenuActions.class).act(item.getItemId());
    }

    public void onPause() {
        Log.d("GeoBeagle", "onPause aborting");
        mAborter.abort();
        mInjector.getInstance(MenuActionSyncGpx.class).abort();
    }

    public void onResume(boolean fImport) {
        mCacheListRefresh.forceRefresh();
        if (fImport)
            mInjector.getInstance(MenuActionSyncGpx.class).act();
    }
}
