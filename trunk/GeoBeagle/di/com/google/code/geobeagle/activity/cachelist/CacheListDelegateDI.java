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

import com.google.code.geobeagle.actions.MenuActionMap;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegate.CacheListDelegateFactory;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.GeocacheListControllerFactory;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextAction;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionEdit;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionView;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionDeleteAllCaches;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.CacheList;
import com.google.inject.Injector;
import com.google.inject.Key;

import roboguice.activity.GuiceListActivity;

public class CacheListDelegateDI {
    public static CacheListDelegate create(GuiceListActivity listActivity) {
        final Injector injector = listActivity.getInjector();
        final InflatedGpsStatusWidget inflatedGpsStatusWidget = injector.getInstance(Key.get(
                InflatedGpsStatusWidget.class, CacheList.class));
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = injector.getInstance(Key.get(
                GpsStatusWidgetDelegate.class, CacheList.class));

        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);

        final GeocacheListPresenter geocacheListPresenter = injector
                .getInstance(GeocacheListPresenter.class);
        final MenuActionSyncGpx menuActionSyncGpx = injector.getInstance(MenuActionSyncGpx.class);
        final MenuActions menuActions = injector.getInstance(MenuActions.class);

        menuActions.add(menuActionSyncGpx);
        menuActions.add(injector.getInstance(MenuActionDeleteAllCaches.class));
        menuActions.add(injector.getInstance(MenuActionMyLocation.class));
        menuActions.add(injector.getInstance(MenuActionSearchOnline.class));
        menuActions.add(injector.getInstance(MenuActionMap.class));
        menuActions.add(injector.getInstance(MenuActionSettings.class));

        final ContextActionView contextActionView = injector.getInstance(ContextActionView.class);
        final ContextActionEdit contextActionEdit = injector.getInstance(ContextActionEdit.class);
        final ContextActionDelete contextActionDelete = injector
                .getInstance(ContextActionDelete.class);

        final ContextAction[] contextActions = new ContextAction[] {
                contextActionDelete, contextActionView, contextActionEdit
        };
        final GeocacheListControllerFactory geocacheListControllerFactory = injector
                .getInstance(GeocacheListControllerFactory.class);
        final GeocacheListController geocacheListController = geocacheListControllerFactory.create(
                contextActions, menuActionSyncGpx, menuActions);
        final CacheListDelegateFactory cacheListDelegateFactory = injector
                .getInstance(CacheListDelegateFactory.class);
        return cacheListDelegateFactory.create(geocacheListController, geocacheListPresenter);
    }
}
