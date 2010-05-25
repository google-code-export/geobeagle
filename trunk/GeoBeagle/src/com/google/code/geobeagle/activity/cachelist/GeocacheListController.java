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

import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextAction;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListView;
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
    private final ContextAction mContextActions[];
    private final MenuActions mMenuActions;
    private final MenuActionSyncGpx mMenuActionSyncGpx;
    private final Aborter mAborter;

    public GeocacheListController(CacheListRefresh cacheListRefresh,
            ContextAction[] contextActions, MenuActionSyncGpx menuActionSyncGpx,
            MenuActions menuActions, Aborter aborter) {
        mCacheListRefresh = cacheListRefresh;
        mContextActions = contextActions;
        mMenuActionSyncGpx = menuActionSyncGpx;
        mMenuActions = menuActions;
        mAborter = aborter;
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo)menuItem
                .getMenuInfo();
        mContextActions[menuItem.getItemId()].act(adapterContextMenuInfo.position - 1);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuActions.onCreateOptionsMenu(menu);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position > 0)
            mContextActions[MENU_VIEW].act(position - 1);
        else
            mCacheListRefresh.forceRefresh();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

    public void onPause() {
        Log.d("GeoBeagle", "onPause aborting");
        mAborter.abort();
        mMenuActionSyncGpx.abort();
    }

    public void onResume(CacheListRefresh cacheListRefresh, boolean fImport) {
        mCacheListRefresh.forceRefresh();
        if (fImport)
            mMenuActionSyncGpx.act();
    }
}
