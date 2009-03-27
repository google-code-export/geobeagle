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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.Action;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.GpxImporter;

import android.app.ListActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.util.ArrayList;

public class CacheListDelegate {

    public static class CacheListOnCreateContextMenuListener implements OnCreateContextMenuListener {
        public static class Factory {
            public OnCreateContextMenuListener create(GeocacheVectors geocacheVectors) {
                return new CacheListOnCreateContextMenuListener(geocacheVectors);
            }
        }

        private final GeocacheVectors mGeocacheVectors;

        CacheListOnCreateContextMenuListener(GeocacheVectors geocacheVectors) {
            mGeocacheVectors = geocacheVectors;
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mGeocacheVectors.get(acmi.position).getId());
            menu.add(0, MENU_VIEW, 0, "ViewAction");
            if (acmi.position > 0)
                menu.add(0, MENU_DELETE, 1, "DeleteAction");
        }
    }

    public static final int MENU_DELETE = 0;
    public static final int MENU_VIEW = 1;
    public static final String SELECT_CACHE = "SELECT_CACHE";

    private final Action mActions[];
    private final CacheListData mCacheListData;
    private final GeocachesSql mGeocachesSql;
    private final CacheListOnCreateContextMenuListener.Factory mCreateContextMenuFactory;
    private final ErrorDisplayer mErrorDisplayer;
    private final GpxImporter mGpxImporter;
    private final LocationControl mLocationControl;
    private final ListActivity mParent;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;

    public CacheListDelegate(ListActivity parent, GeocachesSql geocachesSql,
            LocationControl locationControl, CacheListData cacheListData,
            GeocacheVectors geocacheVectors, GeocacheListAdapter geocacheListAdapter,
            ErrorDisplayer errorDisplayer, Action[] actions,
            CacheListOnCreateContextMenuListener.Factory factory, GpxImporter gpxImporter) {
        mParent = parent;
        mGeocachesSql = geocachesSql;
        mLocationControl = locationControl;
        mCacheListData = cacheListData;
        mGeocacheVectors = geocacheVectors;
        mErrorDisplayer = errorDisplayer;
        mActions = actions;
        mCreateContextMenuFactory = factory;
        mGpxImporter = gpxImporter;
        mGeocacheListAdapter = geocacheListAdapter;
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        try {
            AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo)menuItem
                    .getMenuInfo();
            mActions[menuItem.getItemId()].act(adapterContextMenuInfo.position,
                    mGeocacheListAdapter);
            return true;
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
        return false;
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_list);
        mParent.getListView().setOnCreateContextMenuListener(
                mCreateContextMenuFactory.create(mGeocacheVectors));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.menu_import_gpx);
        return true;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        try {
            mActions[MENU_VIEW].act(position, mGeocacheListAdapter);
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        mGpxImporter.importGpxs(this);
        return true;
    }

    public void onPause() {
        try {
            mGpxImporter.abort();
        } catch (InterruptedException e) {
            // Nothing we can do here! There is no chance to communicate to the
            // user.
        }
    }

    public void onResume() {
        try {
            mGeocachesSql.loadNearestCaches();
            ArrayList<Geocache> geocaches = mGeocachesSql.getGeocaches();
            mCacheListData.add(geocaches, mLocationControl.getLocation());
            mParent.setListAdapter(mGeocacheListAdapter);
            mParent.setTitle("Nearest Unfound Caches (" + geocaches.size() + " / "
                    + mGeocachesSql.getCount() + ")");
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

}
