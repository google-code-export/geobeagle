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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.database.Database;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.location.CombinedLocationManager;

import android.app.ListActivity;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class GeocacheListPresenter {
    public static class CacheListRefreshLocationListener implements LocationListener {
        private final CacheListRefresh mCacheListRefresh;

        public CacheListRefreshLocationListener(CacheListRefresh cacheListRefresh) {
            mCacheListRefresh = cacheListRefresh;
        }

        public void onLocationChanged(Location location) {
            // Log.v("GeoBeagle", "location changed");
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

    private final CacheListRefreshLocationListener mCacheListRefreshLocationListener;
    private final LocationListener mCombinedLocationListener;
    private final CombinedLocationManager mCombinedLocationManager;
    // private final SensorEventListener mCompassListener;
    @SuppressWarnings("deprecation")
    private final SensorListener mCompassListener;
    private final DistanceFormatterManager mDistanceFormatterManager;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;
    private final View mGpsStatusWidget;
    private final ListActivity mListActivity;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SensorManager mSensorManager;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;
    private final GeocacheSummaryRowInflater mGeocacheSummaryRowInflater;

    // private Sensor mCompassSensor;

    @SuppressWarnings("deprecation")
    public GeocacheListPresenter(CombinedLocationManager combinedLocationManager,
            LocationControlBuffered locationControlBuffered,
            LocationListener gpsStatusWidgetLocationListener, View gpsStatusWidget,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable, GeocacheVectors geocacheVectors,
            CacheListRefreshLocationListener cacheListRefreshLocationListener,
            ListActivity listActivity, GeocacheListAdapter geocacheListAdapter,
            ErrorDisplayer errorDisplayer, 
            SensorManager sensorManager, SensorListener compassListener,
            DistanceFormatterManager distanceFormatterManager,
            GeocacheSummaryRowInflater geocacheSummaryRowInflater) {
        mCombinedLocationManager = combinedLocationManager;
        mLocationControlBuffered = locationControlBuffered;
        mCombinedLocationListener = gpsStatusWidgetLocationListener;
        mGpsStatusWidget = gpsStatusWidget;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mGeocacheVectors = geocacheVectors;
        mCacheListRefreshLocationListener = cacheListRefreshLocationListener;
        mListActivity = listActivity;
        mGeocacheListAdapter = geocacheListAdapter;
        mSensorManager = sensorManager;
        mErrorDisplayer = errorDisplayer;
        mCompassListener = compassListener;
        mDistanceFormatterManager = distanceFormatterManager;
        mGeocacheSummaryRowInflater = geocacheSummaryRowInflater;
    }

    public void onCreate() {
        mListActivity.setContentView(R.layout.cache_list);
        ListView listView = mListActivity.getListView();
        listView.addHeaderView(mGpsStatusWidget);
        mListActivity.setListAdapter(mGeocacheListAdapter);
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                mGeocacheVectors));
        mUpdateGpsWidgetRunnable.run();

        // final List<Sensor> sensorList =
        // mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        // mCompassSensor = sensorList.get(0);
    }

    public void onPause() {
        mCombinedLocationManager.removeUpdates(mLocationControlBuffered);
        mCombinedLocationManager.removeUpdates(mCombinedLocationListener);
        mSensorManager.unregisterListener(mCompassListener);
        mCombinedLocationManager.removeUpdates(mCacheListRefreshLocationListener);
    }

    public void onResume() {
        try {
            mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                    mLocationControlBuffered);
            mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                    mCombinedLocationListener);
            mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                    mCacheListRefreshLocationListener);
            mDistanceFormatterManager.setFormatter();
            // mSensorManager.registerListener(mCompassListener, mCompassSensor,
            // SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                    SensorManager.SENSOR_DELAY_UI);
            final boolean absoluteBearing = PreferenceManager.getDefaultSharedPreferences(
                    mListActivity).getBoolean("absolute-bearing", false);
            if (absoluteBearing)
                mGeocacheSummaryRowInflater.setBearingFormatter(new AbsoluteBearingFormatter());
            else
                mGeocacheSummaryRowInflater.setBearingFormatter(new RelativeBearingFormatter());

        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}
