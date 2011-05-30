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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.ContextActions;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.xmlimport.AbortState;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class GeocacheListController {
    //TODO(sng): Rename to CacheListController.
    public static class CacheListOnCreateContextMenuListener implements OnCreateContextMenuListener {
        private final GeocacheVectors mGeocacheVectors;

        public CacheListOnCreateContextMenuListener(GeocacheVectors geocacheVectors) {
            mGeocacheVectors = geocacheVectors;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
            if (acmi.position > 0) {
                menu.setHeaderTitle(mGeocacheVectors.get(acmi.position - 1).getId());
                if (Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.HONEYCOMB) {
                    menu.add(0, MENU_VIEW, 0, R.string.context_menu_view);
                }
                menu.add(0, MENU_EDIT, 1, R.string.context_menu_edit);
                menu.add(0, MENU_DELETE, 2, R.string.context_menu_delete);
            }
        }
    }

    static final int MENU_EDIT = 0;
    static final int MENU_DELETE = 1;
    static final int MENU_VIEW = 2;
    public static final String SELECT_CACHE = "SELECT_CACHE";
    private final CacheListRefresh mCacheListRefresh;
    private final AbortState mAborter;
    private final Provider<MenuActionSyncGpx> mMenuActionSyncGpxProvider;
    private final Provider<CacheListMenuActions> mCacheListMenuActionsProvider;
    private final Provider<ContextActions> mContextActionsProvider;

    @Inject
    public GeocacheListController(Injector injector) {
        mCacheListRefresh = injector.getInstance(CacheListRefresh.class);
        mAborter = injector.getInstance(AbortState.class);
        mMenuActionSyncGpxProvider = injector.getProvider(MenuActionSyncGpx.class);
        mCacheListMenuActionsProvider = injector.getProvider(CacheListMenuActions.class);
        mContextActionsProvider = injector.getProvider(ContextActions.class);
    }

    public GeocacheListController(CacheListRefresh cacheListRefresh,
            AbortState abortState,
            Provider<MenuActionSyncGpx> menuActionSyncProvider,
            Provider<CacheListMenuActions> cacheListMenuActionsProvider,
            Provider<ContextActions> contextActionsProvider) {
        mCacheListRefresh = cacheListRefresh;
        mAborter = abortState;
        mMenuActionSyncGpxProvider = menuActionSyncProvider;
        mCacheListMenuActionsProvider = cacheListMenuActionsProvider;
        mContextActionsProvider = contextActionsProvider;
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo)menuItem
                .getMenuInfo();
        mContextActionsProvider.get()
                .act(menuItem.getItemId(), adapterContextMenuInfo.position - 1);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mCacheListMenuActionsProvider.get().onCreateOptionsMenu(menu);
    }

    public void onListItemClick(int position) {
        if (position > 0)
            mContextActionsProvider.get().act(MENU_VIEW, position - 1);
        else
            mCacheListRefresh.forceRefresh();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mCacheListMenuActionsProvider.get().act(item.getItemId());
    }

    public void onPause() {
        Log.d("GeoBeagle", "onPause aborting");
        mAborter.abort();
        mMenuActionSyncGpxProvider.get().abort();
    }

    public void onResume(boolean fImport) {
        mCacheListRefresh.forceRefresh();
        if (fImport)
            mMenuActionSyncGpxProvider.get().act();
    }
}
