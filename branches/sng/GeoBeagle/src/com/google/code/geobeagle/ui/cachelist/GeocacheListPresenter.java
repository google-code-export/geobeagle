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

import com.google.code.geobeagle.LocationControl;
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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;

public class GeocacheListPresenter {
    static class SortRunnable implements Runnable {
        private final GeocacheListPresenter mGeocacheListPresenter;

        SortRunnable(GeocacheListPresenter geocacheListPresenter) {
            mGeocacheListPresenter = geocacheListPresenter;
        }

        public void run() {
            mGeocacheListPresenter.sort();
        }
    }

    private final CacheListData mCacheListData;
    private final Database mDatabase;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocachesSql mGeocachesSql;
    private final GeocacheVectors mGeocacheVectors;
    private final Handler mHandler;
    private final LocationControl mLocationControl;
    private final LocationListener mLocationListener;
    private final LocationManager mLocationManager;
    private final ListActivity mParent;
    private final SQLiteWrapper mSQLiteWrapper;
    private final UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;

    public GeocacheListPresenter(LocationManager locationManager, LocationControl locationControl,
            LocationListener locationListener, UpdateGpsWidgetRunnable updateGpsWidgetRunnable,
            GeocachesSql geocachesSql, GeocacheVectors geocacheVectors,
            GeocacheListAdapter geocacheListAdapter, CacheListData cacheListData,
            ListActivity listActivity, Handler handler, ErrorDisplayer errorDisplayer,
            SQLiteWrapper sqliteWrapper, Database database) {
        mLocationManager = locationManager;
        mLocationListener = locationListener;
        mGeocachesSql = geocachesSql;
        mSQLiteWrapper = sqliteWrapper;
        mDatabase = database;
        mCacheListData = cacheListData;
        mParent = listActivity;
        mGeocacheVectors = geocacheVectors;
        mGeocacheListAdapter = geocacheListAdapter;
        mLocationControl = locationControl;
        mUpdateGpsWidgetRunnable = updateGpsWidgetRunnable;
        mErrorDisplayer = errorDisplayer;
        mHandler = handler;
    }

    public void doSort() {
        Toast.makeText(mParent, R.string.sorting, Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new SortRunnable(this), 200);
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_list);
        mParent.getListView().setOnCreateContextMenuListener(
                new CacheListOnCreateContextMenuListener(mGeocacheVectors));
        mUpdateGpsWidgetRunnable.run();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mParent.getMenuInflater().inflate(R.menu.cache_list_menu, menu);
        return true;
    }

    public void onPause() {
        mLocationManager.removeUpdates(mLocationListener);
        mSQLiteWrapper.close();
    }

    public void onResume() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    mLocationListener);
            mSQLiteWrapper.openWritableDatabase(mDatabase);
            sort();
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    void sort() {
        mGeocachesSql.loadNearestCaches();
        ArrayList<Geocache> geocaches = mGeocachesSql.getGeocaches();
        mCacheListData.add(geocaches, mLocationControl.getLocation());
        mParent.setListAdapter(mGeocacheListAdapter);
        mParent.setTitle(mParent.getString(R.string.cache_list_title, geocaches.size(),
                mGeocachesSql.getCount()));
    }
}
