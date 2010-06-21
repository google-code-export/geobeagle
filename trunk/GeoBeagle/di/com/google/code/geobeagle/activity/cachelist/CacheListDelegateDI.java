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
import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.GraphicsGenerator.AttributePainter;
import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.ListViewBitmapCopier;
import com.google.code.geobeagle.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.actions.MenuActionMap;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegate.ImportIntentManager;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextAction;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionEdit;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionView;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete.ContextActionDeleteDialogHelper;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionDeleteAllCaches;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.ActionAndTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.AdapterCachesSorter;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceUpdater;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.cachelist.presenter.LocationAndAzimuthTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.LocationTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.SensorManagerWrapper;
import com.google.code.geobeagle.activity.cachelist.presenter.SqlCacheLoader;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.activity.cachelist.presenter.ToleranceStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.activity.cachelist.view.NameFormatter;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.code.geobeagle.database.ISQLiteDatabase;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.database.TagWriterImpl;
import com.google.code.geobeagle.database.TagWriterNull;
import com.google.code.geobeagle.database.WhereFactoryAllCaches;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches;
import com.google.code.geobeagle.database.CacheWriter.ClearCachesFromSourceImpl;
import com.google.code.geobeagle.database.DatabaseDI.SearchFactory;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.WhereStringFactory;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.xmlimport.MessageHandlerInterface;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;
import com.google.inject.Injector;
import com.google.inject.Provider;

import roboguice.activity.GuiceListActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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

    public static CacheListDelegate create(GuiceListActivity listActivity,
            LayoutInflater layoutInflater) {
        final Injector injector = listActivity.getInjector();
        final ErrorDisplayer errorDisplayer = injector.getInstance(ErrorDisplayer.class);
        final CombinedLocationManager combinedLocationManager = injector
                .getInstance(CombinedLocationManager.class);
        final LocationControlBuffered locationControlBuffered = injector
                .getInstance(LocationControlBuffered.class);
        final GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = injector
                .getInstance(GeocacheFromMyLocationFactory.class);
        final BearingFormatter relativeBearingFormatter = injector
                .getInstance(BearingFormatter.class);
        final DistanceFormatterManager distanceFormatterManager = injector
                .getInstance(DistanceFormatterManager.class);
        final GeocacheVectors geocacheVectors = injector.getInstance(GeocacheVectors.class);
        final CacheListData cacheListData = new CacheListData(geocacheVectors);
        final XmlPullParserWrapper xmlPullParserWrapper = new XmlPullParserWrapper();

        final Resources resources = listActivity.getResources();
        final AttributePainter attributePainter = new AttributePainter(new Paint(), new Rect());
        final DifficultyAndTerrainPainter difficultyAndTerrainPainter = new DifficultyAndTerrainPainter(
                attributePainter);
        final IconRenderer iconRenderer = new IconRenderer(resources, difficultyAndTerrainPainter);
        final NameFormatter nameFormatter = injector.getInstance(NameFormatter.class);
        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                distanceFormatterManager.getFormatter(), layoutInflater, relativeBearingFormatter,
                iconRenderer, new ListViewBitmapCopier(), injector
                        .getInstance(IconOverlayFactory.class), nameFormatter);
        final UpdateFlag updateFlag = new UpdateFlag();
        final ActivityVisible activityVisible = injector.getInstance(ActivityVisible.class);
        final GeocacheListAdapter geocacheListAdapter = new GeocacheListAdapter(geocacheVectors,
                geocacheSummaryRowInflater, activityVisible);

        final InflatedGpsStatusWidget inflatedGpsStatusWidget = new InflatedGpsStatusWidget(
                listActivity);
        final GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(listActivity);

        gpsStatusWidget.addView(inflatedGpsStatusWidget, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        final GpsWidgetAndUpdater gpsWidgetAndUpdater = new GpsWidgetAndUpdater(listActivity,
                gpsStatusWidget, locationControlBuffered, combinedLocationManager,
                distanceFormatterManager.getFormatter(), activityVisible);
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = gpsWidgetAndUpdater
                .getGpsStatusWidgetDelegate();

        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);

        final CombinedLocationListener combinedLocationListener = new CombinedLocationListener(
                locationControlBuffered, gpsStatusWidgetDelegate);

        final UpdateGpsWidgetRunnable updateGpsWidgetRunnable = gpsWidgetAndUpdater
                .getUpdateGpsWidgetRunnable();


        final WhereFactoryAllCaches whereFactoryAllCaches = new WhereFactoryAllCaches();
        final SearchFactory searchFactory = new SearchFactory();
        final WhereStringFactory whereStringFactory = new WhereStringFactory();
        final WhereFactoryNearestCaches whereFactoryNearestCaches = new WhereFactoryNearestCaches(
                searchFactory, whereStringFactory);

        final FilterNearestCaches filterNearestCaches = new FilterNearestCaches(
                whereFactoryAllCaches, whereFactoryNearestCaches);
        final CacheListDelegateDI.Timing timing = new CacheListDelegateDI.Timing();

        final AdapterCachesSorter adapterCachesSorter = new AdapterCachesSorter(cacheListData,
                timing, locationControlBuffered);
        final GpsDisabledLocation gpsDisabledLocation = new GpsDisabledLocation();
        final DistanceUpdater distanceUpdater = new DistanceUpdater(geocacheListAdapter);
        final ToleranceStrategy sqlCacheLoaderTolerance = new LocationTolerance(500,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy adapterCachesSorterTolerance = new LocationTolerance(6,
                gpsDisabledLocation, 1000);
        final LocationTolerance distanceUpdaterLocationTolerance = new LocationTolerance(1,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy distanceUpdaterTolerance = new LocationAndAzimuthTolerance(
                distanceUpdaterLocationTolerance, 720);
        final ActionAndTolerance[] actionAndTolerances = new ActionAndTolerance[] {
                null, new ActionAndTolerance(adapterCachesSorter, adapterCachesSorterTolerance),
                new ActionAndTolerance(distanceUpdater, distanceUpdaterTolerance)
        };
        final TitleUpdater titleUpdater = new TitleUpdater(listActivity, filterNearestCaches,
                timing);
        final Provider<DbFrontend> dbFrontendProvider = injector.getProvider(DbFrontend.class);
        final SqlCacheLoader sqlCacheLoader = new SqlCacheLoader(dbFrontendProvider,
                filterNearestCaches, cacheListData, locationControlBuffered, titleUpdater, timing,
                activityVisible);
        actionAndTolerances[0] = new ActionAndTolerance(sqlCacheLoader, sqlCacheLoaderTolerance);
        final ActionManager actionManager = new ActionManager(actionAndTolerances);
        final CacheListRefresh cacheListRefresh = new CacheListRefresh(actionManager, timing,
                locationControlBuffered, updateFlag);

        final SensorManager sensorManager = (SensorManager)listActivity
                .getSystemService(Context.SENSOR_SERVICE);
        final CompassListenerFactory compassListenerFactory = new CompassListenerFactory(
                locationControlBuffered);

        distanceFormatterManager.addHasDistanceFormatter(geocacheSummaryRowInflater);
        distanceFormatterManager.addHasDistanceFormatter(gpsStatusWidgetDelegate);
        final CacheListView.ScrollListener scrollListener = new CacheListView.ScrollListener(
                updateFlag);
        final SensorManagerWrapper sensorManagerWrapper = new SensorManagerWrapper(sensorManager);
        final GeocacheListPresenter geocacheListPresenter = new GeocacheListPresenter(
                combinedLocationListener, combinedLocationManager, compassListenerFactory,
                distanceFormatterManager, geocacheListAdapter, geocacheVectors, gpsStatusWidget, listActivity, locationControlBuffered,
                sensorManagerWrapper, updateGpsWidgetRunnable, scrollListener);
        final CacheTypeFactory cacheTypeFactory = new CacheTypeFactory();
 
        final Aborter aborter = injector.getInstance(Aborter.class);
        final Provider<ImportBCachingWorker> importBCachingWorkerProvider = injector
                .getProvider(ImportBCachingWorker.class);
        final MessageHandlerInterface messageHandler = injector.getInstance(MessageHandler.class);
        final Provider<ISQLiteDatabase> databaseProvider = injector
                .getProvider(ISQLiteDatabase.class);
        final TagWriterImpl tagWriterImpl = new TagWriterImpl(databaseProvider);
        final TagWriterNull tagWriterNull = new TagWriterNull();
        final FilePathStrategy filePathStrategy = injector.getInstance(FilePathStrategy.class);
        final ClearCachesFromSourceImpl clearCachesFromSourceImpl = injector.getInstance(ClearCachesFromSourceImpl.class);
        final CachePersisterFacadeFactory cachePersisterFacadeFactory = new CachePersisterFacadeFactory(
                messageHandler, cacheTypeFactory, tagWriterImpl, tagWriterNull, filePathStrategy,
                clearCachesFromSourceImpl);

        final GpxImporterFactory gpxImporterFactory = new GpxImporterFactory(aborter,
                cachePersisterFacadeFactory, errorDisplayer, geocacheListPresenter, listActivity,
                messageHandler, xmlPullParserWrapper, injector);

        final NullAbortable nullAbortable = new NullAbortable();

        final Provider<CacheWriter> cacheWriterProvider = injector.getProvider(CacheWriter.class);
        final MenuActionSyncGpx menuActionSyncGpx = new MenuActionSyncGpx(
                importBCachingWorkerProvider, nullAbortable, cacheListRefresh, gpxImporterFactory,
                cacheWriterProvider);
        final MenuActions menuActions = new MenuActions(resources);
        menuActions.add(menuActionSyncGpx);
        menuActions.add(new MenuActionDeleteAllCaches(cacheListRefresh, listActivity,
                dbFrontendProvider, new AlertDialog.Builder(listActivity), injector
                        .getInstance(BCachingStartTime.class)));
        menuActions.add(new MenuActionMyLocation(listActivity, errorDisplayer, geocacheFromMyLocationFactory,
                new LocationSaver(cacheWriterProvider)));
        menuActions.add(new MenuActionSearchOnline(listActivity));
        menuActions.add(new MenuActionMap(listActivity, locationControlBuffered));
        menuActions.add(injector.getInstance(MenuActionSettings.class));

        final Intent geoBeagleMainIntent = new Intent(listActivity, GeoBeagle.class);
        final ContextActionView contextActionView = new ContextActionView(geocacheVectors,
                listActivity, geoBeagleMainIntent);
        final ContextActionEdit contextActionEdit = new ContextActionEdit(geocacheVectors,
                listActivity);
        final ContextActionDelete contextActionDelete = new ContextActionDelete(
                geocacheListAdapter, geocacheVectors, titleUpdater, cacheWriterProvider,
                listActivity, 0);

        final ContextAction[] contextActions = new ContextAction[] {
                contextActionDelete, contextActionView, contextActionEdit
        };
        final OnClickListener contextActionDeleteOnClickOkListener = new ContextActionDelete.OnClickOk(
                contextActionDelete);
        final ContextActionDeleteDialogHelper contextActionDeleteDialogHelper = new ContextActionDeleteDialogHelper(
                contextActionDelete, contextActionDeleteOnClickOkListener);

        final GeocacheListController geocacheListController = new GeocacheListController(
                cacheListRefresh, contextActions, menuActionSyncGpx, menuActions,
                aborter);
        final ActivitySaver activitySaver = injector.getInstance(ActivitySaver.class);
        final ImportIntentManager importIntentManager = injector.getInstance(ImportIntentManager.class);
        return new CacheListDelegate(importIntentManager, activitySaver, cacheListRefresh,
                geocacheListController, geocacheListPresenter, dbFrontendProvider,
                contextActionDeleteDialogHelper, activityVisible);
    }
}
