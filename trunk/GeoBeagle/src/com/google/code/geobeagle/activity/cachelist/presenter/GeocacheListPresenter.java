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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.CacheListCompassListener;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.CacheListViewScrollListener;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.Pausable;
import com.google.code.geobeagle.activity.cachelist.SearchTarget;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.filter.UpdateFilterMediator;
import com.google.code.geobeagle.database.filter.FilterCleanliness;
import com.google.code.geobeagle.database.filter.UpdateFilterWorker;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.shakewaker.ShakeWaker;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.app.Activity;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class GeocacheListPresenter implements Pausable {
    //TODO(sng): Rename to CacheListPresenter.
    static final int UPDATE_DELAY = 1000;

    private final LocationListener combinedLocationListener;
    private final CombinedLocationManager combinedLocationManager;
    private final Provider<CacheListCompassListener> cacheListCompassListenerProvider;
    private final GeocacheVectors geocacheVectors;
    private final InflatedGpsStatusWidget inflatedGpsStatusWidget;
    private final Activity activity;
    private final LocationControlBuffered locationControlBuffered;
    private final SensorManagerWrapper sensorManagerWrapper;
    private final UpdateGpsWidgetRunnable updateGpsWidgetRunnable;
    private final CacheListViewScrollListener scrollListener;
    private final GpsStatusListener gpsStatusListener;
    private final UpdateFilterWorker updateFilterWorker;
    private final FilterCleanliness filterCleanliness;
    private final ShakeWaker shakeWaker;
    private final UpdateFilterMediator updateFilterMediator;
    private final SearchTarget searchTarget;
    private final ListFragtivityOnCreateHandler listFragtivityOnCreateHandler;

    public GeocacheListPresenter(CombinedLocationListener combinedLocationListener,
            CombinedLocationManager combinedLocationManager,
            ListFragtivityOnCreateHandler listFragtivityOnCreateHandler,
            Provider<CacheListCompassListener> cacheListCompassListenerProvider,
            GeocacheVectors geocacheVectors,
            InflatedGpsStatusWidget inflatedGpsStatusWidget,
            Activity listActivity,
            LocationControlBuffered locationControlBuffered,
            SensorManagerWrapper sensorManagerWrapper,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable,
            CacheListViewScrollListener cacheListViewScrollListener,
            GpsStatusListener gpsStatusListener,
            UpdateFilterWorker updateFilterWorker,
            FilterCleanliness filterCleanliness,
            ShakeWaker shakeWaker,
            UpdateFilterMediator updateFilterMediator,
            SearchTarget searchTarget) {
        this.combinedLocationListener = combinedLocationListener;
        this.combinedLocationManager = combinedLocationManager;
        this.cacheListCompassListenerProvider = cacheListCompassListenerProvider;
        this.geocacheVectors = geocacheVectors;
        this.inflatedGpsStatusWidget = inflatedGpsStatusWidget;
        this.shakeWaker = shakeWaker;
        this.activity = listActivity;
        this.locationControlBuffered = locationControlBuffered;
        this.updateGpsWidgetRunnable = updateGpsWidgetRunnable;
        this.sensorManagerWrapper = sensorManagerWrapper;
        this.scrollListener = cacheListViewScrollListener;
        this.gpsStatusListener = gpsStatusListener;
        this.updateFilterWorker = updateFilterWorker;
        this.filterCleanliness = filterCleanliness;
        this.updateFilterMediator = updateFilterMediator;
        this.searchTarget = searchTarget;
        this.listFragtivityOnCreateHandler = listFragtivityOnCreateHandler;
    }

    @Inject
    public GeocacheListPresenter(Injector injector) {
        this.combinedLocationListener = injector.getInstance(CombinedLocationListener.class);
        this.combinedLocationManager = injector.getInstance(CombinedLocationManager.class);
        this.cacheListCompassListenerProvider = injector
                .getProvider(CacheListCompassListener.class);
        this.geocacheVectors = injector.getInstance(GeocacheVectors.class);
        this.inflatedGpsStatusWidget = injector.getInstance(InflatedGpsStatusWidget.class);
        this.shakeWaker = injector.getInstance(ShakeWaker.class);
        this.activity = injector.getInstance(Activity.class);
        this.locationControlBuffered = injector.getInstance(LocationControlBuffered.class);
        this.updateGpsWidgetRunnable = injector.getInstance(UpdateGpsWidgetRunnable.class);
        this.sensorManagerWrapper = injector.getInstance(SensorManagerWrapper.class);
        this.scrollListener = injector.getInstance(CacheListViewScrollListener.class);
        this.gpsStatusListener = injector.getInstance(GpsStatusListener.class);
        this.updateFilterWorker = injector.getInstance(UpdateFilterWorker.class);
        this.filterCleanliness = injector.getInstance(FilterCleanliness.class);
        this.updateFilterMediator = injector.getInstance(UpdateFilterMediator.class);
        this.searchTarget = injector.getInstance(SearchTarget.class);
        this.listFragtivityOnCreateHandler = injector
                .getInstance(ListFragtivityOnCreateHandler.class);
    }

    public void onCreate() {
        listFragtivityOnCreateHandler.onCreateActivity(activity, this);
    }

    void setupListView(ListView listView) {
        NoCachesView noCachesView = (NoCachesView)listView.getEmptyView();
        noCachesView.setSearchTarget(searchTarget);
        listView.addHeaderView((View)inflatedGpsStatusWidget.getTag());
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                geocacheVectors));
        listView.setOnScrollListener(scrollListener);
    }

    public void onCreateFragment(Object listFragment) {
        listFragtivityOnCreateHandler.onCreateFragment(this, listFragment);
    }

    @Override
    public void onPause() {
        combinedLocationManager.removeUpdates();
        sensorManagerWrapper.unregisterListener();
        shakeWaker.unregister();
    }

    public void onResume(CacheListRefresh cacheListRefresh) {
        if (filterCleanliness.isDirty()) {
            updateFilterMediator.startFiltering("Resetting filter");
            updateFilterWorker.start();
        }

        CacheListRefreshLocationListener cacheListRefreshLocationListener = new CacheListRefreshLocationListener(
                cacheListRefresh);
        CacheListCompassListener compassListener = cacheListCompassListenerProvider.get();
        combinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0, locationControlBuffered);
        combinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0, combinedLocationListener);
        combinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                cacheListRefreshLocationListener);

        combinedLocationManager.addGpsStatusListener(gpsStatusListener);

        updateGpsWidgetRunnable.run();
        sensorManagerWrapper.registerListener(compassListener, SensorManager.SENSOR_DELAY_UI);
        shakeWaker.register();
        Log.d("GeoBeagle", "GeocacheListPresenter onResume done");
    }
}
