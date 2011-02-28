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

    private final LocationListener mCombinedLocationListener;
    private final CombinedLocationManager mCombinedLocationManager;
    private final Provider<CacheListCompassListener> mCacheListCompassListenerProvider;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;
    private final InflatedGpsStatusWidget mInflatedGpsStatusWidget;
    private final ListActivity mListActivity;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SensorManagerWrapper mSensorManagerWrapper;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;
    private final CacheListViewScrollListener mScrollListener;
    private final GpsStatusListener mGpsStatusListener;
    private final UpdateFilterWorker mUpdateFilterWorker;
    private final FilterCleanliness mFilterCleanliness;
    private final ShakeWaker mShakeWaker;
    private final UpdateFilterMediator mUpdateFilterMediator;
    private final SearchTarget mSearchTarget;

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
        mCombinedLocationListener = combinedLocationListener;
        mCombinedLocationManager = combinedLocationManager;
        mCacheListCompassListenerProvider = cacheListCompassListenerProvider;
        mGeocacheListAdapter = geocacheListAdapter;
        mGeocacheVectors = geocacheVectors;
        mInflatedGpsStatusWidget = inflatedGpsStatusWidget;
        mShakeWaker = shakeWaker;
        mListActivity = (ListActivity)listActivity;
        mLocationControlBuffered = locationControlBuffered;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mSensorManagerWrapper = sensorManagerWrapper;
        mScrollListener = cacheListViewScrollListener;
        mGpsStatusListener = gpsStatusListener;
        mUpdateFilterWorker = updateFilterWorker;
        mFilterCleanliness = filterCleanliness;
        mUpdateFilterMediator = updateFilterMediator;
        mSearchTarget = searchTarget;
    }

    public void onCreate() {
        mListActivity.setContentView(R.layout.cache_list);
        final ListView listView = mListActivity.getListView();
        NoCachesView noCachesView = (NoCachesView)listView.getEmptyView();
        noCachesView.setSearchTarget(mSearchTarget);
        listView.addHeaderView((View)mInflatedGpsStatusWidget.getTag());
        mListActivity.setListAdapter(mGeocacheListAdapter);
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                mGeocacheVectors));
        listView.setOnScrollListener(mScrollListener);
    }

    @Override
    public void onPause() {
        mCombinedLocationManager.removeUpdates();
        mSensorManagerWrapper.unregisterListener();
        mShakeWaker.unregister();
    }

    public void onResume(CacheListRefresh cacheListRefresh) {
        if (mFilterCleanliness.isDirty()) {
            mUpdateFilterMediator.startFiltering("Resetting filter");
            mUpdateFilterWorker.start();
        }

        final CacheListRefreshLocationListener cacheListRefreshLocationListener = new CacheListRefreshLocationListener(
                cacheListRefresh);
        final CacheListCompassListener mCompassListener = mCacheListCompassListenerProvider.get();
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0, mLocationControlBuffered);
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0, mCombinedLocationListener);
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                cacheListRefreshLocationListener);

        mCombinedLocationManager.addGpsStatusListener(mGpsStatusListener);

        mUpdateGpsWidgetRunnable.run();
        mSensorManagerWrapper.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        mShakeWaker.register();
        Log.d("GeoBeagle", "GeocacheListPresenter onResume done");
    }
}
