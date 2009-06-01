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

package com.google.code.geobeagle.cachelistactivity.presenter;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachelistactivity.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.cachelistactivity.model.GeocacheVectors;
import com.google.code.geobeagle.cachelistactivity.model.LocationControlBuffered;
import com.google.code.geobeagle.cachelistactivity.view.GpsStatusWidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.database.Database;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.location.CombinedLocationManager;

import android.app.ListActivity;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class GeocacheListPresenter {
    static final int UPDATE_DELAY = 1000;

    public static class CacheListRefreshLocationListener implements LocationListener {
        private final CacheListRefresh mCacheListRefresh;

        public CacheListRefreshLocationListener(CacheListRefresh cacheListRefresh) {
            mCacheListRefresh = cacheListRefresh;
        }

        public void onLocationChanged(Location location) {
            Log.v("GeoBeagle", "location changed");
            mCacheListRefresh.refresh();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private final CacheListRefreshLocationListener mCacheListRefreshLocationListener;
    private final CombinedLocationManager mCombinedLocationManager;
    private final Database mDatabase;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;
    private final LocationListener mGpsStatusWidgetLocationListener;
    private final View mGpsWidgetView;
    private final ListActivity mListActivity;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SQLiteWrapper mSQLiteWrapper;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;
    private final SensorManager mSensorManager;
    // private final SensorEventListener mCompassListener;
    @SuppressWarnings("deprecation")
    private final SensorListener mCompassListener;

    // private Sensor mCompassSensor;

    @SuppressWarnings("deprecation")
    public GeocacheListPresenter(CombinedLocationManager combinedLocationManager,
            LocationControlBuffered locationControlBuffered,
            LocationListener gpsStatusWidgetLocationListener, View gpsWidgetView,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable, GeocacheVectors geocacheVectors,
            CacheListRefreshLocationListener cacheListRefreshLocationListener,
            ListActivity listActivity, GeocacheListAdapter geocacheListAdapter,
            ErrorDisplayer errorDisplayer, SQLiteWrapper sqliteWrapper, Database database,
            SensorManager sensorManager, SensorListener compassListener) {
        mCombinedLocationManager = combinedLocationManager;
        mLocationControlBuffered = locationControlBuffered;
        mGpsStatusWidgetLocationListener = gpsStatusWidgetLocationListener;
        mGpsWidgetView = gpsWidgetView;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mGeocacheVectors = geocacheVectors;
        mCacheListRefreshLocationListener = cacheListRefreshLocationListener;
        mListActivity = listActivity;
        mSQLiteWrapper = sqliteWrapper;
        mDatabase = database;
        mGeocacheListAdapter = geocacheListAdapter;
        mSensorManager = sensorManager;
        mErrorDisplayer = errorDisplayer;
        mCompassListener = compassListener;
    }

    public void onCreate() {
        mListActivity.setContentView(R.layout.cache_list);
        ListView listView = mListActivity.getListView();
        listView.addHeaderView(mGpsWidgetView);
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
        mCombinedLocationManager.removeUpdates(mGpsStatusWidgetLocationListener);
        mSensorManager.unregisterListener(mCompassListener);
        mCombinedLocationManager.removeUpdates(mCacheListRefreshLocationListener);
    }

    // static public class CompassListener implements SensorEventListener {
    @SuppressWarnings("deprecation")
    static public class CompassListener implements SensorListener {

        private final Refresher mRefresher;
        private final LocationControlBuffered mLocationControlBuffered;
        private float mLastAzimuth;

        public CompassListener(Refresher refresher,
                LocationControlBuffered locationControlBuffered, float lastAzimuth) {
            mRefresher = refresher;
            mLocationControlBuffered = locationControlBuffered;
            mLastAzimuth = lastAzimuth;
        }

        // public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // }

        // public void onSensorChanged(SensorEvent event) {
        // onSensorChanged(SensorManager.SENSOR_ORIENTATION, event.values);
        // }

        public void onAccuracyChanged(int sensor, int accuracy) {
        }

        public void onSensorChanged(int sensor, float[] values) {
            final float currentAzimuth = values[0];
            if (Math.abs(currentAzimuth - mLastAzimuth) > 5) {
                // Log.v("GeoBeagle", "azimuth now " + sensor +", " +
                // currentAzimuth);
                mLocationControlBuffered.setAzimuth(((int)currentAzimuth / 5) * 5);
                mRefresher.refresh();
                mLastAzimuth = currentAzimuth;
            }
        }
    }

    public void onResume() {
        try {
            mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                    mLocationControlBuffered);
            mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                    mGpsStatusWidgetLocationListener);
            mCombinedLocationManager.requestLocationUpdates(UPDATE_DELAY, 0,
                    mCacheListRefreshLocationListener);
            // mSensorManager.registerListener(mCompassListener, mCompassSensor,
            // SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                    SensorManager.SENSOR_DELAY_UI);
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}
