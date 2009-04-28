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
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.ui.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;

import android.app.ListActivity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

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

    static class SortRunnable implements Runnable {
        private final GeocacheListPresenter mGeocacheListPresenter;

        SortRunnable(GeocacheListPresenter geocacheListPresenter) {
            mGeocacheListPresenter = geocacheListPresenter;
        }

        public void run() {
            mGeocacheListPresenter.sort();
        }
    }

    private BaseAdapterLocationListener mBaseAdapterLocationListener;
    private final CacheListData mCacheListData;
    private final Database mDatabase;
    private final ErrorDisplayer mErrorDisplayer;
    private final BaseAdapter mGeocacheListAdapter;
    private final GeocachesSql mGeocachesSql;
    private final GeocacheVectors mGeocacheVectors;
    private final LocationListener mGpsStatusWidgetLocationListener;
    private final View mGpsWidgetView;
    private final Handler mHandler;
    private final LocationControlBuffered mLocationControlBuffered;
    private final CombinedLocationManager mCombinedLocationManager;
    private final ListActivity mParent;
    private final SQLiteWrapper mSQLiteWrapper;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;

    public GeocacheListPresenter(CombinedLocationManager combinedLocationManager,
            LocationControlBuffered locationControlBuffered,
            LocationListener gpsStatusWidgetLocationListener, View gpsWidgetView,
            UpdateGpsWidgetRunnable updateGpsWidgetRunnable, GeocachesSql geocachesSql,
            GeocacheVectors geocacheVectors, BaseAdapter geocacheListAdapter,
            BaseAdapterLocationListener baseAdapterLocationListener, CacheListData cacheListData,
            ListActivity listActivity, Handler handler, ErrorDisplayer errorDisplayer,
            SQLiteWrapper sqliteWrapper, Database database) {
        mCombinedLocationManager = combinedLocationManager;
        mGpsStatusWidgetLocationListener = gpsStatusWidgetLocationListener;
        mGeocachesSql = geocachesSql;
        mSQLiteWrapper = sqliteWrapper;
        mDatabase = database;
        mCacheListData = cacheListData;
        mParent = listActivity;
        mGeocacheVectors = geocacheVectors;
        mGeocacheListAdapter = geocacheListAdapter;
        mLocationControlBuffered = locationControlBuffered;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mErrorDisplayer = errorDisplayer;
        mHandler = handler;
        mGpsWidgetView = gpsWidgetView;
        mBaseAdapterLocationListener = baseAdapterLocationListener;
    }

    public void doSort() {
        Toast.makeText(mParent, R.string.sorting, Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new SortRunnable(this), 200);
    }

    public void onCreate() {
        mLocationControlBuffered.onLocationChanged(null);
        mParent.setContentView(R.layout.cache_list);
        ListView listView = mParent.getListView();
        listView.addHeaderView(mGpsWidgetView);
        listView.setOnCreateContextMenuListener(new CacheListOnCreateContextMenuListener(
                mGeocacheVectors));
        mUpdateGpsWidgetRunnable.run();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mParent.getMenuInflater().inflate(R.menu.cache_list_menu, menu);
        return true;
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
            sort();
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    void sort() {
        mGeocachesSql.loadNearestCaches();
        ArrayList<Geocache> geocaches = mGeocachesSql.getGeocaches();
        mCacheListData.add(geocaches, mLocationControlBuffered);
        mParent.setListAdapter(mGeocacheListAdapter);
        mParent.setTitle(mParent.getString(R.string.cache_list_title, geocaches.size(),
                mGeocachesSql.getCount()));
    }
}
