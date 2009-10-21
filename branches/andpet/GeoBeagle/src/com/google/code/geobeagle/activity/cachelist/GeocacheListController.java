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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.CachesProviderToggler;

import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class GeocacheListController implements OnCreateContextMenuListener {

    public static final String SELECT_CACHE = "SELECT_CACHE";
    private final CacheListRefresh mCacheListRefresh;
    private final CacheAction[] mCacheActions;
    private final CachesProviderToggler mCachesProviderToggler;
    private final MenuActions mMenuActions;
    private final MenuActionSyncGpx mMenuActionSyncGpx;
    private final GeocacheVectors mGeocacheVectors;
    private final CacheAction mSelectAction;
    private final Resources mResources;

    public GeocacheListController(CacheListRefresh cacheListRefresh,
            CacheAction selectAction,
            CacheAction[] cacheActions, CachesProviderToggler cachesProviderToggler,
            MenuActionSyncGpx menuActionSyncGpx, MenuActions menuActions,
            GeocacheVectors geocacheVectors,
            Resources resources) {
        mCacheListRefresh = cacheListRefresh;
        mCacheActions = cacheActions;
        mCachesProviderToggler = cachesProviderToggler;
        mMenuActionSyncGpx = menuActionSyncGpx;
        mMenuActions = menuActions;
        mGeocacheVectors = geocacheVectors;
        mSelectAction = selectAction;
        mResources = resources;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
        if (acmi.position > 0) {
            menu.setHeaderTitle(mGeocacheVectors.get(acmi.position - 1).getId());
            for (int ix = 0; ix < mCacheActions.length; ix++) {
                CacheAction action = mCacheActions[ix];
                menu.add(0, action.getId(), ix, mResources.getString(action.getId()));
            }
        }
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo adapterContextMenuInfo = 
            (AdapterContextMenuInfo)menuItem.getMenuInfo();
        int index = adapterContextMenuInfo.position - 1;
        mCacheActions[menuItem.getItemId()].act(getGeocacheAt(index));
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuActions.onCreateOptionsMenu(menu);
    }
    
    private Geocache getGeocacheAt(int listIndex) {
        return mGeocacheVectors.get(listIndex).getGeocache();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position > 0) {
            mSelectAction.act(getGeocacheAt(position - 1));
        } else {
            mCacheListRefresh.forceRefresh();
        }
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        boolean nearest = mCachesProviderToggler.isShowingNearest();
        int menuString = nearest ? R.string.menu_show_all_caches : R.string.menu_show_nearest_caches;
        menu.findItem(R.string.menu_toggle_filter).setTitle(menuString);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

    public void onPause() {
        mMenuActionSyncGpx.abort();
    }

    public void onResume(CacheListRefresh cacheListRefresh, boolean fImport) {
        mCacheListRefresh.forceRefresh();
        if (fImport)
            mMenuActionSyncGpx.act();
    }
}
