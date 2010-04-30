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

import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.CacheListView;
import com.google.code.geobeagle.activity.cachelist.CompassListenerFactory;
import com.google.code.geobeagle.activity.cachelist.CacheListView.ScrollListener;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import android.app.ListActivity;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class GeocacheListPresenter {
    
    public interface GeocacheListPresenterFactory {
        GeocacheListPresenter create(CombinedLocationListener combinedLocationListener,
                DistanceFormatterManager distanceFormatterManager, GpsStatusWidget gpsStatusWidget,
                UpdateGpsWidgetRunnable updateGpsWidgetRunnable);
    }
    
    public static class CacheListRefreshLocationListener implements LocationListener {
        private final CacheListRefresh mCacheListRefresh;

        public CacheListRefreshLocationListener(CacheListRefresh cacheListRefresh) {
            mCacheListRefresh = cacheListRefresh;
        }

        public void onLocationChanged(Location location) {
            // Log.d("GeoBeagle", "location changed");
            mCacheListRefresh.refresh();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    static final int UPDATE_DELAY = 1000;

    private final LocationListener mCombinedLocationListener;
    private final CombinedLocationManager mCombinedLocationManager;
    private final CompassListenerFactory mCompassListenerFactory;
    private final DistanceFormatterManager mDistanceFormatterManager;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;
    private final View mGpsStatusWidget;
    private final ListActivity mListActivity;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SensorManagerWrapper mSensorManagerWrapper;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;
    private final CacheListView.ScrollListener mScrollListener;

    @Inject
    public GeocacheListPresenter(@Assisted CombinedLocationListener combinedLocationListener,
            CombinedLocationManager combinedLocationManager,
            CompassListenerFactory compassListenerFactory,
            @Assisted DistanceFormatterManager distanceFormatterManager,
            GeocacheListAdapter geocacheListAdapter, GeocacheVectors geocacheVectors,
            @Assisted GpsStatusWidget gpsStatusWidget, ListActivity listActivity,
            LocationControlBuffered locationControlBuffered,
            SensorManagerWrapper sensorManagerWrapper,
            @Assisted UpdateGpsWidgetRunnable updateGpsWidgetRunnable, ScrollListener scrollListener) {
        mCombinedLocationListener = combinedLocationListener;
        mCombinedLocationManager = combinedLocationManager;
        mCompassListenerFactory = compassListenerFactory;
        mDistanceFormatterManager = distanceFormatterManager;
        mGeocacheListAdapter = geocacheListAdapter;
        mGeocacheVectors = geocacheVectors;
        mGpsStatusWidget = gpsStatusWidget;
        mListActivity = listActivity;
        mLocationControlBuffered = locationControlBuffered;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mSensorManagerWrapper = sensorManagerWrapper;
        mScrollListener = scrollListener;
    }

    public void onCreate() {
        mListActivity.setContentView(R.layout.cache_list);
        final ListView listView = mListActivity.getListView();
        listView.addHeaderView(mGpsStatusWidget);
        mListActivity.setListAdapter(mGeocacheListAdapter);
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                mGeocacheVectors));
        listView.setOnScrollListener(mScrollListener);
        mUpdateGpsWidgetRunnable.run();

        // final List<Sensor> sensorList =
        // mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        // mCompassSensor = sensorList.get(0);
    }

    public void onPause() {
        mCombinedLocationManager.removeUpdates();
        mSensorManagerWrapper.unregisterListener();
    }

    public void onResume(CacheListRefresh cacheListRefresh) {
        final CacheListRefreshLocationListener cacheListRefreshLocationListener = new CacheListRefreshLocationListener(
                cacheListRefresh);
        final CompassListener mCompassListener = mCompassListenerFactory.create(cacheListRefresh);
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0, mLocationControlBuffered);
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0, mCombinedLocationListener);
        mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                cacheListRefreshLocationListener);
        mDistanceFormatterManager.setFormatter();
        // mSensorManager.registerListener(mCompassListener, mCompassSensor,
        // SensorManager.SENSOR_DELAY_UI);

        mSensorManagerWrapper.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
    }
}
