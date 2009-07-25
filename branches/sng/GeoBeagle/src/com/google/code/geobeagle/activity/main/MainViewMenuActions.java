package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSearchOnline;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionToggleFilter;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActions;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;

public class MainViewMenuActions extends MenuActions {

    public MainViewMenuActions(MenuActionSyncGpx menuActionSyncGpx,
            MenuActionMyLocation menuActionMyLocation,
            MenuActionToggleFilter menuActionToggleFilter, CacheListRefresh cacheListRefresh,
            MenuActionSearchOnline menuActionSearchOnline) {
        super(menuActionSyncGpx, menuActionMyLocation, menuActionToggleFilter, cacheListRefresh,
                menuActionSearchOnline);
        // TODO Auto-generated constructor stub
    }

}
