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

import com.google.code.geobeagle.CacheListActions;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.data.Destination.DestinationFactory;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.GpxLoader;
import com.google.code.geobeagle.io.LocationBookmarksSql;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class CacheListDelegate {
    
    public class MessageHandler extends Handler {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS:
                    mProgressDialog.setMessage(mCacheProgressUpdater.mStatus);
                    break;
                case MSG_DONE:
                    mProgressDialog.dismiss();
                    onResume();
                    break;
                default:
                    break;
            }
        }
    }

    private class ImportThread extends Thread {
        private final CacheProgressUpdater mCacheProgressUpdater;
        private Handler mHandler;

        public ImportThread(CacheProgressUpdater cacheProgressUpdater, Handler handler) {
            mCacheProgressUpdater = cacheProgressUpdater;
            mHandler = handler;
        }

        public void run() {
            try {
                mGpxLoader.load(mCacheProgressUpdater);
                mHandler.sendEmptyMessage(MSG_DONE);
                mGpxLoader = null;
            } catch (Exception e) {
                mParent.runOnUiThread(new DisplayErrorRunnable(e, mErrorDisplayer));
            } finally {
                mSqliteDatabase.close();
            }
        }
    }

    static class CacheListOnCreateContextMenuListener implements OnCreateContextMenuListener {
        public static class Factory {
            public OnCreateContextMenuListener create(CacheListData cacheListData) {
                return new CacheListOnCreateContextMenuListener(cacheListData);
            }
        }

        CacheListData mCacheListData;

        CacheListOnCreateContextMenuListener(CacheListData cacheListData) {
            mCacheListData = cacheListData;
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mCacheListData.getId(acmi.position));
            menu.add(0, MENU_VIEW, 0, "View");
            if (acmi.position > 0)
                menu.add(0, MENU_DELETE, 1, "Delete");
        }
    }

    public static class CacheProgressUpdater {
        private String mStatus;
        private final Handler mHandler;

        public CacheProgressUpdater(Handler handler) {
            mHandler = handler;
        }

        public void update(String status) {
            mStatus = status;
            mHandler.sendEmptyMessage(MSG_PROGRESS);
        }
    }

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
        public SimpleAdapter create(Context context, ArrayList<Map<String, Object>> arrayList,
                int view_layout, String[] from, int[] to) {
            return new SimpleAdapter(context, arrayList, view_layout, from, to);
        }
    }

    public static final String[] ADAPTER_FROM = {
            "cache", "distance"
    };

    public static final int[] ADAPTER_TO = {
            R.id.txt_cache, R.id.distance
    };
    public static final int MENU_DELETE = 0;
    public static final int MENU_VIEW = 1;

    public static final String SELECT_CACHE = "SELECT_CACHE";

    private static final int MSG_PROGRESS = 0;
    private static final int MSG_DONE = 1;

    public static CacheListDelegate create(ListActivity parent) {
        final ErrorDisplayer errorDisplayer = new ErrorDisplayer(parent);
        final Database database = Database.create(parent);
        final ResourceProvider resourceProvider = new ResourceProvider(parent);
        // TODO: add a create function that takes a resourceid
        final Pattern[] destinationPatterns = Destination.getDestinationPatterns(resourceProvider);
        final DestinationFactory destinationFactory = new DestinationFactory(destinationPatterns);
        final LocationBookmarksSql locationBookmarks = LocationBookmarksSql.create(parent,
                database, destinationFactory, errorDisplayer);
        final SimpleAdapterFactory simpleAdapterFactory = new SimpleAdapterFactory();
        final CacheListData cacheListData = CacheListData.create(destinationFactory, parent);
        final LocationControl locationControl = LocationControl.create(((LocationManager)parent
                .getSystemService(Context.LOCATION_SERVICE)));

        final CacheListActions.Action actions[] = CacheListActions.create(parent, database,
                cacheListData, errorDisplayer);

        CacheListOnCreateContextMenuListener.Factory factory = new CacheListOnCreateContextMenuListener.Factory();
        final CacheListDelegate cacheListDelegate = new CacheListDelegate(parent, locationBookmarks, locationControl,
                simpleAdapterFactory, cacheListData, errorDisplayer, database, actions, factory);
        cacheListDelegate.setHandler(cacheListDelegate.new MessageHandler());
        return cacheListDelegate;
    }

    private void setHandler(MessageHandler messageHandler) {
        mMessageHandler = messageHandler;
    }

    private Thread importThread;
    private final CacheListActions.Action mActions[];
    private final CacheListData mCacheListData;
    private final CacheListOnCreateContextMenuListener.Factory mCreateContextMenuFactory;
    private final Database mDatabase;
    private final ErrorDisplayer mErrorDisplayer;
    private GpxLoader mGpxLoader;
    private final LocationBookmarksSql mLocationBookmarks;
    private final LocationControl mLocationControl;
    private final ListActivity mParent;
    private SimpleAdapter mSimpleAdapter;
    private final SimpleAdapterFactory mSimpleAdapterFactory;
    private SQLiteDatabase mSqliteDatabase;
    private ProgressDialog mProgressDialog;
    private MessageHandler mMessageHandler;
    private CacheProgressUpdater mCacheProgressUpdater;

    public CacheListDelegate(ListActivity parent, LocationBookmarksSql locationBookmarks,
            LocationControl locationControl, SimpleAdapterFactory simpleAdapterFactory,
            CacheListData cacheListData, ErrorDisplayer errorDisplayer, Database database,
            CacheListActions.Action[] actions, CacheListOnCreateContextMenuListener.Factory factory) {
        mParent = parent;
        mLocationBookmarks = locationBookmarks;
        mLocationControl = locationControl;
        mSimpleAdapterFactory = simpleAdapterFactory;
        mCacheListData = cacheListData;
        mErrorDisplayer = errorDisplayer;
        mDatabase = database;
        mActions = actions;
        mCreateContextMenuFactory = factory;
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        try {
            AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo)menuItem
                    .getMenuInfo();
            mActions[menuItem.getItemId()].act(adapterContextMenuInfo.position, mSimpleAdapter);
            return true;
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
        return false;
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_list);
        mParent.getListView().setOnCreateContextMenuListener(
                mCreateContextMenuFactory.create(mCacheListData));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.menu_import_gpx);
        return true;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        try {
            mActions[MENU_VIEW].act(position, mSimpleAdapter);
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            mSqliteDatabase = mDatabase.openOrCreateCacheDatabase();
            mGpxLoader = GpxLoader.create(mParent, mErrorDisplayer, mDatabase, mSqliteDatabase);
            mCacheProgressUpdater = new CacheProgressUpdater(this.mMessageHandler);
            if (mGpxLoader != null) {
                mProgressDialog = ProgressDialog.show(this.mParent, "Importing Caches",
                        "Please wait...");

                importThread = new ImportThread(mCacheProgressUpdater, this.mMessageHandler);
                importThread.start();
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

    public void onPause() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mGpxLoader != null) {
                mGpxLoader.abortLoad();
                if (importThread != null) {
                    importThread.join();
                    Toast mToast = Toast.makeText(mParent, R.string.import_canceled,
                            Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        } catch (InterruptedException e) {
            // None of these errors are displayed because the activity is terminating.
            mErrorDisplayer.displayErrorAndStack(e);
        } catch (Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    public void onResume() {
        try {
            mLocationBookmarks.onResume(null);
            mCacheListData.add(mLocationBookmarks.getLocations(), mLocationControl.getLocation());
            mSimpleAdapter = mSimpleAdapterFactory.create(mParent, mCacheListData.getAdapterData(),
                    R.layout.cache_row, ADAPTER_FROM, ADAPTER_TO);
            mParent.setListAdapter(mSimpleAdapter);
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}
