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

package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.CombinedLocationManager;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.ui.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;

import android.app.ListActivity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class GeocacheListPresenter {
    static class BaseAdapterLocationListener implements LocationListener {
        private final BaseAdapter mBaseAdapter;

        BaseAdapterLocationListener(BaseAdapter baseAdapter) {
            mBaseAdapter = baseAdapter;
        }

        public void onLocationChanged(Location location) {
            Log.v("GeoBeagle", "location changed");
            mBaseAdapter.notifyDataSetChanged();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private final BaseAdapterLocationListener mBaseAdapterLocationListener;
    private final CombinedLocationManager mCombinedLocationManager;
    private final Database mDatabase;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheVectors mGeocacheVectors;
    private final LocationListener mGpsStatusWidgetLocationListener;
    private final View mGpsWidgetView;
    private final ListActivity mListActivity;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SQLiteWrapper mSQLiteWrapper;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;

    public GeocacheListPresenter(CombinedLocationManager combinedLocationManager,
            LocationControlBuffered locationControlBuffered,
            LocationListener gpsStatusWidgetLocationListener, View gpsWidgetView,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable, GeocacheVectors geocacheVectors,
            BaseAdapterLocationListener baseAdapterLocationListener, ListActivity listActivity,
            ErrorDisplayer errorDisplayer, SQLiteWrapper sqliteWrapper, Database database) {
        mCombinedLocationManager = combinedLocationManager;
        mLocationControlBuffered = locationControlBuffered;
        mGpsStatusWidgetLocationListener = gpsStatusWidgetLocationListener;
        mGpsWidgetView = gpsWidgetView;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mGeocacheVectors = geocacheVectors;
        mBaseAdapterLocationListener = baseAdapterLocationListener;
        mListActivity = listActivity;
        mSQLiteWrapper = sqliteWrapper;
        mDatabase = database;
        mErrorDisplayer = errorDisplayer;
    }

    public void onCreate() {
        mLocationControlBuffered.onLocationChanged(null);
        mListActivity.setContentView(R.layout.cache_list);
        ListView listView = mListActivity.getListView();
        listView.addHeaderView(mGpsWidgetView);
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                mGeocacheVectors));
        mUpdateGpsWidgetRunnable.run();
    }

    public void onPause() {
        mCombinedLocationManager.removeUpdates(mLocationControlBuffered);
        mCombinedLocationManager.removeUpdates(mGpsStatusWidgetLocationListener);
        mCombinedLocationManager.removeUpdates(mBaseAdapterLocationListener);
        mSQLiteWrapper.close();
    }

    public void onResume() {
        try {
            mCombinedLocationManager.requestLocationUpdates(0, 0, mLocationControlBuffered);
            mCombinedLocationManager.requestLocationUpdates(0, 0, mGpsStatusWidgetLocationListener);
            mCombinedLocationManager.requestLocationUpdates(0, 10, mBaseAdapterLocationListener);

            mSQLiteWrapper.openWritableDatabase(mDatabase);
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}
