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
import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory.GpxImporterFactoryFactory;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextAction;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionEdit;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionView;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionDeleteAllCaches.MenuActionDeleteAllCachesFactory;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx.MenuActionSyncGpxFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter.GeocacheListPresenterFactory;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.GpsStatusWidgetFactory;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater.GpsWidgetAndUpdaterFactory;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationListener.CombinedLocationListenerFactory;
import com.google.code.geobeagle.xmlimport.MessageHandlerInterface;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory.CachePersisterFacadeFactoryFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.inject.Injector;

import roboguice.activity.GuiceListActivity;

import android.util.Log;

import java.util.Calendar;

public class CacheListDelegateDI {
    public static class Timing {
        private long mStartTime;

        public void lap(CharSequence msg) {
            long finishTime = Calendar.getInstance().getTimeInMillis();
            Log.d("GeoBeagle", "****** " + msg + ": " + (finishTime - mStartTime));
            mStartTime = finishTime;
        }

        public void start() {
            mStartTime = Calendar.getInstance().getTimeInMillis();
        }

        public long getTime() {
            return Calendar.getInstance().getTimeInMillis();
        }
    }

    public static CacheListDelegate create(GuiceListActivity listActivity) {
        final Injector injector = listActivity.getInjector();
        final InflatedGpsStatusWidget inflatedGpsStatusWidget = injector
                .getInstance(InflatedGpsStatusWidget.class);
        final GpsStatusWidgetFactory gpsStatusWidgetFactory = injector
                .getInstance(GpsStatusWidgetFactory.class);
        final GpsStatusWidget gpsStatusWidget = gpsStatusWidgetFactory
                .create(inflatedGpsStatusWidget);
        final GpsWidgetAndUpdaterFactory gpsWidgetAndUpdaterFactory = injector
                .getInstance(GpsWidgetAndUpdaterFactory.class);
        final GpsWidgetAndUpdater gpsWidgetAndUpdater = gpsWidgetAndUpdaterFactory
                .create(gpsStatusWidget);
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = gpsWidgetAndUpdater
                .getGpsStatusWidgetDelegate();

        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);

        final CombinedLocationListenerFactory combinedLocationListenerFactory = injector
                .getInstance(CombinedLocationListenerFactory.class);
        final CombinedLocationListener combinedLocationListener = combinedLocationListenerFactory
                .create(gpsStatusWidgetDelegate);

        final UpdateGpsWidgetRunnable updateGpsWidgetRunnable = gpsWidgetAndUpdater
                .getUpdateGpsWidgetRunnable();

        final CacheListRefresh cacheListRefresh = injector.getInstance(CacheListRefresh.class);

        final GeocacheListPresenterFactory geocacheListPresenterFactory = injector
                .getInstance(GeocacheListPresenterFactory.class);
        final GeocacheListPresenter geocacheListPresenter = geocacheListPresenterFactory.create(
                combinedLocationListener, gpsStatusWidget, updateGpsWidgetRunnable);
        final MessageHandlerInterface messageHandler = injector.getInstance(MessageHandler.class);
        final CachePersisterFacadeFactoryFactory cachePersisterFacadeFactoryFactory = injector
                .getInstance(CachePersisterFacadeFactoryFactory.class);
        final CachePersisterFacadeFactory cachePersisterFacadeFactory = cachePersisterFacadeFactoryFactory
                .create(messageHandler);

        final GpxImporterFactoryFactory gpxImporterFactoryFactory = injector
                .getInstance(GpxImporterFactoryFactory.class);
        final GpxImporterFactory gpxImporterFactory = gpxImporterFactoryFactory.create(
                cachePersisterFacadeFactory, geocacheListPresenter, messageHandler);

        final MenuActionSyncGpxFactory menuActionSyncGpxFactory = injector
                .getInstance(MenuActionSyncGpxFactory.class);
        final MenuActionSyncGpx menuActionSyncGpx = menuActionSyncGpxFactory.create(
                cacheListRefresh, gpxImporterFactory);
        final MenuActions menuActions = injector.getInstance(MenuActions.class);
        
        menuActions.add(menuActionSyncGpx);
        final MenuActionDeleteAllCachesFactory menuActionDeleteAllCachesFactory = injector
                .getInstance(MenuActionDeleteAllCachesFactory.class);
        
        menuActions.add(menuActionDeleteAllCachesFactory.create(cacheListRefresh));
        menuActions.add(injector.getInstance(MenuActionMyLocation.class));
        menuActions.add(injector.getInstance(MenuActionSearchOnline.class));
        menuActions.add(injector.getInstance(MenuActionMap.class));
        menuActions.add(injector.getInstance(MenuActionSettings.class));

        final ContextActionView contextActionView = injector.getInstance(ContextActionView.class);
        final ContextActionEdit contextActionEdit = injector.getInstance(ContextActionEdit.class);
        final ContextActionDelete contextActionDelete = injector.getInstance(ContextActionDelete.class);

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
