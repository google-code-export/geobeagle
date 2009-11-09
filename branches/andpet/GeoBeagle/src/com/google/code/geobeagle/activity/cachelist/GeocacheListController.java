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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.ICachesProvider;
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

    //TODO: Resources should be provided to individual CacheAction instead
    public static class CacheListOnCreateContextMenuListener implements OnCreateContextMenuListener {
        private final ICachesProvider mCachesProvider;
        private final CacheAction mCacheActions[];

        public CacheListOnCreateContextMenuListener(ICachesProvider cachesProvider,
                CacheAction cacheActions[]) {
            mCachesProvider = cachesProvider;
            mCacheActions = cacheActions;
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
            if (acmi.position > 0) {
                Geocache geocache = mCachesProvider.getCaches().get(acmi.position - 1);
                menu.setHeaderTitle(geocache.getId());
                for (int ix = 0; ix < mCacheActions.length; ix++) {
                    menu.add(0, ix, ix, mCacheActions[ix].getLabel());
                }
            }
        }
    }

    public static final String SELECT_CACHE = "SELECT_CACHE";
    private final CacheListAdapter mCacheList;
    private final CacheAction mCacheActions[];
    private final MenuActions mMenuActions;
    private final MenuActionSyncGpx mMenuActionSyncGpx;
    private final CacheAction mDefaultCacheAction;

    public GeocacheListController(CacheListAdapter cacheList,
            CacheAction[] cacheActions,
            MenuActionSyncGpx menuActionSyncGpx, MenuActions menuActions,
            CacheAction defaultCacheAction) {
        mCacheList = cacheList;
        mCacheActions = cacheActions;
        mMenuActionSyncGpx = menuActionSyncGpx;
        mMenuActions = menuActions;
        mDefaultCacheAction = defaultCacheAction;
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo adapterContextMenuInfo = 
            (AdapterContextMenuInfo)menuItem.getMenuInfo();
        int index = adapterContextMenuInfo.position - 1;
        Log.d("GeoBeagle", "Act doing action " + menuItem.getItemId() + " = " +
                mCacheActions[menuItem.getItemId()].toString());
        mCacheActions[menuItem.getItemId()].act(mCacheList.getGeocacheAt(index));
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuActions.onCreateOptionsMenu(menu);
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position > 0) {
            mDefaultCacheAction.act(mCacheList.getGeocacheAt(position - 1));
        } else {
            mCacheList.forceRefresh();
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
        mCacheList.forceRefresh();
        if (fImport)
            mMenuActionSyncGpx.act();
    }
}
