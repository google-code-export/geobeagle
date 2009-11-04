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

import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationAndDirection;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.actions.CacheActionDelete;
import com.google.code.geobeagle.actions.CacheActionEdit;
import com.google.code.geobeagle.actions.CacheActionView;
import com.google.code.geobeagle.actions.MenuActionChooseFilter;
import com.google.code.geobeagle.actions.MenuActionMap;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegate.ImportIntentManager;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.actions.Abortable;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionToggleFilter;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheList;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListUpdater;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManagerDi;
import com.google.code.geobeagle.activity.cachelist.presenter.ListTitleFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.database.CachesProviderArea;
import com.google.code.geobeagle.database.CachesProviderCount;
import com.google.code.geobeagle.database.CachesProviderSorted;
import com.google.code.geobeagle.database.CachesProviderToggler;
import com.google.code.geobeagle.database.CachesProviderWaitForInit;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.ICachesProviderCenter;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;

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

    public static CacheListDelegate create(ListActivity listActivity, LayoutInflater layoutInflater) {
        final OnClickListener onClickListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        };
        final ErrorDisplayer errorDisplayer = new ErrorDisplayer(listActivity, onClickListener);
        final LocationAndDirection locationAndDirection = 
            LocationControlDi.create(listActivity);
        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        final GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = new GeocacheFromMyLocationFactory(
                geocacheFactory, locationAndDirection);
        final BearingFormatter relativeBearingFormatter = new RelativeBearingFormatter();
        final DistanceFormatterManager distanceFormatterManager = DistanceFormatterManagerDi
                .create(listActivity);
        final XmlPullParserWrapper xmlPullParserWrapper = new XmlPullParserWrapper();

        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                distanceFormatterManager.getFormatter(), layoutInflater,
                relativeBearingFormatter, listActivity.getResources());

        final InflatedGpsStatusWidget inflatedGpsStatusWidget = new InflatedGpsStatusWidget(
                listActivity);
        final GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(listActivity);

        gpsStatusWidget.addView(inflatedGpsStatusWidget, LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        final GpsWidgetAndUpdater gpsWidgetAndUpdater = new GpsWidgetAndUpdater(listActivity,
                gpsStatusWidget, locationAndDirection,
                distanceFormatterManager.getFormatter());
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = gpsWidgetAndUpdater
                .getGpsStatusWidgetDelegate();

        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);

        final UpdateGpsWidgetRunnable updateGpsWidgetRunnable = gpsWidgetAndUpdater
                .getUpdateGpsWidgetRunnable();
        updateGpsWidgetRunnable.run();
        
        final ListTitleFormatter listTitleFormatter = new ListTitleFormatter();
        final CacheListDelegateDI.Timing timing = new CacheListDelegateDI.Timing();

        final CacheFilter cacheFilter = new CacheFilter(listActivity);
        
        final DbFrontend dbFrontend = new DbFrontend(listActivity, geocacheFactory);
        final CachesProviderArea cachesProviderArea = new CachesProviderArea(dbFrontend, cacheFilter);
        final ICachesProviderCenter cachesProviderCount = new CachesProviderWaitForInit(new CachesProviderCount(cachesProviderArea, 15, 30));
        final CachesProviderSorted cachesProviderSorted = new CachesProviderSorted(cachesProviderCount);
        //final Clock clock = new Clock();
        //TODO: Use Lazy
        //final CachesProviderLazy cachesProviderLazy = new CachesProviderLazy(cachesProviderSorted, 0.01, 2000, clock);
        ICachesProviderCenter cachesProviderLazy = cachesProviderSorted;
        final CachesProviderArea cachesProviderAll = new CachesProviderArea(dbFrontend, cacheFilter);
        final CachesProviderToggler cachesProviderToggler = 
            new CachesProviderToggler(cachesProviderLazy, cachesProviderAll);
        final TitleUpdater titleUpdater = new TitleUpdater(listActivity, 
                cachesProviderToggler, listTitleFormatter, timing, dbFrontend);

        distanceFormatterManager.addHasDistanceFormatter(geocacheSummaryRowInflater);
        distanceFormatterManager.addHasDistanceFormatter(gpsStatusWidgetDelegate);
        final CacheList cacheList = new CacheList(cachesProviderToggler, 
                cachesProviderSorted, geocacheSummaryRowInflater, titleUpdater);
        final CacheListUpdater cacheListUpdater = new CacheListUpdater(
                locationAndDirection, cacheList, cachesProviderCount, cachesProviderSorted);
        locationAndDirection.addObserver(cacheListUpdater);
        final CacheListView.ScrollListener scrollListener = new CacheListView.ScrollListener(
                cacheList);
        final CacheTypeFactory cacheTypeFactory = new CacheTypeFactory();

        final Aborter aborter = new Aborter();
        final MessageHandler messageHandler = MessageHandler.create(listActivity);
        final CachePersisterFacadeFactory cachePersisterFacadeFactory = new CachePersisterFacadeFactory(
                messageHandler, cacheTypeFactory);

        final GpxImporterFactory gpxImporterFactory = new GpxImporterFactory(aborter,
                cachePersisterFacadeFactory, errorDisplayer, locationAndDirection, listActivity,
                messageHandler, xmlPullParserWrapper);

        final Abortable nullAbortable = new Abortable() {
            public void abort() {
            }
        };

        final Resources resources = listActivity.getResources();
        final MenuActionSyncGpx menuActionSyncGpx = new MenuActionSyncGpx(nullAbortable,
                cacheList, gpxImporterFactory, dbFrontend, resources);
        final MenuActions menuActions = new MenuActions();
        menuActions.add(menuActionSyncGpx);
        menuActions.add(new MenuActionToggleFilter(cachesProviderToggler, cacheList, resources));
        menuActions.add(new MenuActionMyLocation(cacheList, errorDisplayer,
                geocacheFromMyLocationFactory, dbFrontend, resources));
        menuActions.add(new MenuActionSearchOnline(listActivity));
        final CachesProviderArea[] providers = { cachesProviderArea, };
        menuActions.add(new MenuActionChooseFilter(listActivity, cacheFilter, 
                providers, cacheList));
        menuActions.add(new MenuActionMap(listActivity, locationAndDirection));
        
        final Intent geoBeagleMainIntent = new Intent(listActivity, GeoBeagle.class);
        final CacheActionView cacheActionView = new CacheActionView(
                listActivity, geoBeagleMainIntent);
        final CacheActionEdit cacheActionEdit = new CacheActionEdit(listActivity);
        final CacheActionDelete cacheActionDelete = 
            new CacheActionDelete(cacheList, titleUpdater, dbFrontend, resources);
            
        final CacheAction[] contextActions = new CacheAction[] {
                cacheActionView, cacheActionEdit, cacheActionDelete
        };
        final GeocacheListController geocacheListController = 
            new GeocacheListController(cacheList, contextActions, menuActionSyncGpx, menuActions, cacheActionView);

        final ActivitySaver activitySaver = ActivityDI.createActivitySaver(listActivity);
        final ImportIntentManager importIntentManager = new ImportIntentManager(listActivity);
        final CacheListOnCreateContextMenuListener menuCreator = 
            new CacheListOnCreateContextMenuListener(cachesProviderToggler, contextActions);

        return new CacheListDelegate(importIntentManager, activitySaver,
                geocacheListController, dbFrontend, locationAndDirection, updateGpsWidgetRunnable, gpsStatusWidget, menuCreator, cacheList, geocacheSummaryRowInflater, listActivity, scrollListener, distanceFormatterManager);
    }
}
