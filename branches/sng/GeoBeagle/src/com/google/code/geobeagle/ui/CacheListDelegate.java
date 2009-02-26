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
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.GpxLoader;
import com.google.code.geobeagle.io.LocationBookmarksSql;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class CacheListDelegate {

    private static class DisplayErrorRunnable implements Runnable {
        private final ErrorDisplayer mErrorDisplayer;
        private final Exception mException;

        private DisplayErrorRunnable(Exception e, ErrorDisplayer errorDisplayer) {
            mException = e;
            mErrorDisplayer = errorDisplayer;
        }

        public void run() {
            mErrorDisplayer.displayErrorAndStack(mException);
        }
    }

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

    public static CacheListDelegate create(ListActivity parent) {
        final ErrorDisplayer errorDisplayer = new ErrorDisplayer(parent);
        final Database database = Database.create(parent);
        final ResourceProvider resourceProvider = new ResourceProvider(parent);
        final Pattern[] destinationPatterns = Destination.getDestinationPatterns(resourceProvider);
        final DestinationFactory destinationFactory = new DestinationFactory(destinationPatterns);
        final LocationBookmarksSql locationBookmarks = LocationBookmarksSql.create(parent,
                database, destinationFactory, errorDisplayer);
        final SimpleAdapterFactory simpleAdapterFactory = new SimpleAdapterFactory();
        final Intent intent = new Intent(parent, GeoBeagle.class);
        final CacheListData cacheListData = CacheListData.create(destinationFactory, parent);
        final LocationControl locationControl = LocationControl.create(((LocationManager)parent
                .getSystemService(Context.LOCATION_SERVICE)));

        return new CacheListDelegate(parent, locationBookmarks, locationControl,
                simpleAdapterFactory, cacheListData, intent, errorDisplayer, database);
    }

    private final CacheListData mCacheListData;
    private final Database mDatabaseFactory;
    private final ErrorDisplayer mErrorDisplayer;
    private final Intent mIntent;
    private final LocationBookmarksSql mLocationBookmarks;
    private final LocationControl mLocationControl;

    private final ListActivity mParent;

    private final SimpleAdapterFactory mSimpleAdapterFactory;

    private ProgressDialog progressDialog;

    public CacheListDelegate(ListActivity parent, LocationBookmarksSql locationBookmarks,
            LocationControl locationControl, SimpleAdapterFactory simpleAdapterFactory,
            CacheListData cacheListData, Intent intent, ErrorDisplayer errorDisplayer,
            Database database) {
        mParent = parent;
        mLocationBookmarks = locationBookmarks;
        mLocationControl = locationControl;
        mSimpleAdapterFactory = simpleAdapterFactory;
        mCacheListData = cacheListData;
        mIntent = intent;
        mErrorDisplayer = errorDisplayer;
        mDatabaseFactory = database;
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_list);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.menu_import_gpx);
        return true;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        mIntent.putExtra("location", mCacheListData.getLocation(position)).setAction(SELECT_CACHE);
        mParent.startActivity(mIntent);
    }

    public static class CacheProgressUpdater {
        private final ProgressDialog mProgressDialog;
        private final Activity mActivity;
        private String mStatus;

        public CacheProgressUpdater(Activity activity, ProgressDialog progressDialog) {
            mActivity = activity;
            mProgressDialog = progressDialog;
        }

        public void update(String status) {
            mStatus = status;
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    mProgressDialog.setMessage(mStatus);
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            final GpxLoader gpxLoader = GpxLoader.create(mParent, mErrorDisplayer, mDatabaseFactory);
            if (gpxLoader != null) {
                progressDialog = ProgressDialog.show(this.mParent, "Importing Caches",
                        "Please wait...");
                final Thread thread = new Thread() {
                    public void run() {
                        try {
                            gpxLoader.load(new CacheProgressUpdater(mParent, progressDialog));
                            progressDialog.dismiss();
                            mParent.runOnUiThread(new Runnable() {
                                public void run() {
                                    CacheListDelegate.this.onResume();
                                }
                            });
                        } catch (Exception e) {
                            mParent.runOnUiThread(new DisplayErrorRunnable(e, mErrorDisplayer));
                        }
                    }
                };
                thread.start();
            }
        } catch (final FileNotFoundException e) {
            mErrorDisplayer.displayError("Unable to open file '" + e.getMessage()
                    + "'.  Please ensure that the cache import file exists "
                    + "and that your sdcard is unmounted.");
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
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
