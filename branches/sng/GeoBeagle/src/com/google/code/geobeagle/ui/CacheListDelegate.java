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

import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.data.Destination.DestinationFactory;
import com.google.code.geobeagle.io.DatabaseFactory;
import com.google.code.geobeagle.io.LoadGpx;
import com.google.code.geobeagle.io.LocationBookmarksSql;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class CacheListDelegate {

    public static class SimpleAdapterFactory {
        public SimpleAdapter createSimpleAdapter(Context context,
                ArrayList<Map<String, Object>> arrayList, int view_layout, String[] from, int[] to) {
            return new SimpleAdapter(context, arrayList, view_layout, from, to);
        }
    }

    public static final String[] ADAPTER_FROM = {
            "cache", "distance"
    };
    public static final int[] ADAPTER_TO = {
            R.id.txt_cache, R.id.distance
    };
    public static final String SELECT_CACHE = "SELECT_CACHE";

    private final CacheListData mCacheListData;
    private final DatabaseFactory mDatabaseFactory;
    private final ErrorDisplayer mErrorDisplayer;
    private final Intent mIntent;
    private final LocationBookmarksSql mLocationBookmarks;
    private final LocationControl mLocationControl;
    private final ListActivity mParent;
    private final SimpleAdapterFactory mSimpleAdapterFactory;

    public static CacheListDelegate create(ListActivity parent) {
        final ErrorDisplayer errorDisplayer = new ErrorDisplayer(parent);
        final DatabaseFactory databaseFactory = DatabaseFactory.create(parent);
        final ResourceProvider resourceProvider = new ResourceProvider(parent);
        final Pattern[] destinationPatterns = Destination.getDestinationPatterns(resourceProvider);
        final DestinationFactory destinationFactory = new DestinationFactory(destinationPatterns);
        final LocationBookmarksSql locationBookmarks = LocationBookmarksSql.create(parent,
                databaseFactory, destinationFactory, errorDisplayer);
        final SimpleAdapterFactory simpleAdapterFactory = new SimpleAdapterFactory();
        final Intent intent = new Intent(parent, GeoBeagle.class);
        final CacheListData cacheListData = CacheListData.create(destinationFactory, parent);
        final LocationControl locationControl = LocationControl.create(((LocationManager)parent
                .getSystemService(Context.LOCATION_SERVICE)));

        return new CacheListDelegate(parent, locationBookmarks, locationControl,
                simpleAdapterFactory, cacheListData, intent, errorDisplayer, databaseFactory);
    }

    public CacheListDelegate(ListActivity parent, LocationBookmarksSql locationBookmarks,
            LocationControl locationControl, SimpleAdapterFactory simpleAdapterFactory,
            CacheListData cacheListData, Intent intent, ErrorDisplayer errorDisplayer,
            DatabaseFactory databaseFactory) {
        mParent = parent;
        mLocationBookmarks = locationBookmarks;
        mLocationControl = locationControl;
        mSimpleAdapterFactory = simpleAdapterFactory;
        mCacheListData = cacheListData;
        mIntent = intent;
        mErrorDisplayer = errorDisplayer;
        mDatabaseFactory = databaseFactory;
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_list);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        mIntent.putExtra("location", mCacheListData.getLocation(position)).setAction(SELECT_CACHE);
        mParent.startActivity(mIntent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            LoadGpx loadGpx = LoadGpx.create(mParent, mErrorDisplayer, mDatabaseFactory);
            if (loadGpx != null) {
                loadGpx.load();
            }
        } catch (final FileNotFoundException e) {
            mErrorDisplayer.displayError("Unable to open file '" + e.getMessage()
                    + "'.  Please ensure that the cache import file exists "
                    + "and that your sdcard is unmounted.");
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
        onResume();
        return true;
    }

    public void onResume() {
        try {
            mLocationBookmarks.onResume(null);
            mCacheListData.add(mLocationBookmarks.getLocations(), mLocationControl.getLocation());

            mParent.setListAdapter(mSimpleAdapterFactory.createSimpleAdapter(mParent,
                    mCacheListData.getAdapterData(), R.layout.cache_row, ADAPTER_FROM, ADAPTER_TO));
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}
