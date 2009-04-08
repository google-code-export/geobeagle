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
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.GpxImporter;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;

import android.app.ListActivity;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.ArrayList;

public class GeocacheListPresenter {
    private final CacheListData mCacheListData;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocachesSql mGeocachesSql;
    private final GeocacheVectors mGeocacheVectors;
    private final GpxImporter mGpxImporter;
    private final LocationControl mLocationControl;
    private final LocationListener mLocationListener;
    private final LocationManager mLocationManager;
    private final ListActivity mParent;

    public GeocacheListPresenter(LocationManager locationManager,
            LocationControl locationControl, LocationListener locationListener,
            GeocachesSql geocachesSql, GeocacheVectors geocacheVectors,
            GpxImporter gpxImporter, GeocacheListAdapter geocacheListAdapter,
            CacheListData cacheListData, ListActivity listActivity,
            ErrorDisplayer errorDisplayer) {
        mLocationManager = locationManager;
        mLocationListener = locationListener;
        mGeocachesSql = geocachesSql;
        mCacheListData = cacheListData;
        mParent = listActivity;
        mGpxImporter = gpxImporter;
        mGeocacheVectors = geocacheVectors;
        mErrorDisplayer = errorDisplayer;
        mGeocacheListAdapter = geocacheListAdapter;
        mLocationControl = locationControl;
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_list);
        mParent.getListView().setOnCreateContextMenuListener(
                new CacheListOnCreateContextMenuListener(mGeocacheVectors));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = mParent.getMenuInflater();
        inflater.inflate(R.menu.cache_list_menu, menu);
        return true;
    }

    public void onPause() {
        mLocationManager.removeUpdates(mLocationListener);
        try {
            mGpxImporter.abort();
        } catch (InterruptedException e) {
            // Nothing we can do here! There is no chance to communicate to
            // the user.
        }
    }

    public void onResume() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    mLocationListener);
            mGeocachesSql.loadNearestCaches();
            ArrayList<Geocache> geocaches = mGeocachesSql.getGeocaches();
            mCacheListData.add(geocaches, mLocationControl.getLocation());
            mParent.setListAdapter(mGeocacheListAdapter);
            mParent.setTitle(mParent.getString(R.string.cache_list_title, geocaches.size(),
                    mGeocachesSql.getCount()));
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}