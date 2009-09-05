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
import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSearchOnline;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionToggleFilter;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.code.geobeagle.database.ISQLiteDatabase;

public class MenuActionsFactory {
    private final FilterNearestCaches mFilterNearestCaches;
    private final MenuActionMyLocationFactory mMenuActionMyLocationFactory;
    private final MenuActionSearchOnline mMenuActionSearchOnline;

    public MenuActionsFactory(FilterNearestCaches filterNearestCaches,
            MenuActionMyLocationFactory menuActionMyLocationFactory,
            MenuActionSearchOnline menuActionSearchOnline) {
        mMenuActionMyLocationFactory = menuActionMyLocationFactory;
        mFilterNearestCaches = filterNearestCaches;
        mMenuActionSearchOnline = menuActionSearchOnline;
    }

    public MenuActions create(MenuActionSyncGpx menuActionSyncGpx,
            CacheListRefresh cacheListRefresh, ISQLiteDatabase writableDatabase) {
        final MenuActionToggleFilter mMenuActionToggleFilter = new MenuActionToggleFilter(
                mFilterNearestCaches, cacheListRefresh);
        final MenuActionMyLocation menuActionMyLocation = mMenuActionMyLocationFactory.create(
                cacheListRefresh, writableDatabase);
        final MenuAction menuActions[] = {
                menuActionSyncGpx, mMenuActionToggleFilter, menuActionMyLocation,
                mMenuActionSearchOnline
        };
        final int menuIds[] = {
                R.id.menu_sync, R.id.menu_toggle_filter, R.id.menu_my_location,
                R.id.menu_search_online,
        };

        return new MenuActions(menuActions, menuIds);
    }
}
