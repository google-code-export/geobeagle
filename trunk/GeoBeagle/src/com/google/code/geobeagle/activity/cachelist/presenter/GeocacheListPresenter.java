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
import com.google.code.geobeagle.R;
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
import com.google.inject.Provider;

import android.app.Activity;
import android.app.ListActivity;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class GeocacheListPresenter implements Pausable {

    static final int UPDATE_DELAY = 1000;

    private final LocationListener combinedLocationListener;
    private final CombinedLocationManager combinedLocationManager;
    private final Provider<CacheListCompassListener> cacheListCompassListenerProvider;
    private final GeocacheListAdapter geocacheListAdapter;
    private final GeocacheVectors geocacheVectors;
    private final InflatedGpsStatusWidget inflatedGpsStatusWidget;
    private final ListActivity listActivity;
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

    @Inject
    public GeocacheListPresenter(CombinedLocationListener combinedLocationListener,
            CombinedLocationManager combinedLocationManager,
            Provider<CacheListCompassListener> cacheListCompassListenerProvider,
            GeocacheListAdapter geocacheListAdapter,
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
        this.geocacheListAdapter = geocacheListAdapter;
        this.geocacheVectors = geocacheVectors;
        this.inflatedGpsStatusWidget = inflatedGpsStatusWidget;
        this.shakeWaker = shakeWaker;
        this.listActivity = (ListActivity)listActivity;
        this.locationControlBuffered = locationControlBuffered;
        this.updateGpsWidgetRunnable = updateGpsWidgetRunnable;
        this.sensorManagerWrapper = sensorManagerWrapper;
        this.scrollListener = cacheListViewScrollListener;
        this.gpsStatusListener = gpsStatusListener;
        this.updateFilterWorker = updateFilterWorker;
        this.filterCleanliness = filterCleanliness;
        this.updateFilterMediator = updateFilterMediator;
        this.searchTarget = searchTarget;
    }

    public void onCreate() {
        listActivity.setContentView(R.layout.cache_list);
        setupListView(listActivity.getListView());
        listActivity.setListAdapter(geocacheListAdapter);
    }

    private void setupListView(ListView listView) {
        NoCachesView noCachesView = (NoCachesView)listView.getEmptyView();
        noCachesView.setSearchTarget(searchTarget);
        listView.addHeaderView((View)inflatedGpsStatusWidget.getTag());
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                geocacheVectors));
        listView.setOnScrollListener(scrollListener);
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
        sensorManagerWrapper.registerListener(compassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        shakeWaker.register();
        Log.d("GeoBeagle", "GeocacheListPresenter onResume done");
    }
}
