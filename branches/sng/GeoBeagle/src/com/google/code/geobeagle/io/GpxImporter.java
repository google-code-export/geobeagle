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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class GpxImporter {

    public static class CacheProgressUpdater {
        private final Handler mHandler;
        String mStatus;

        public CacheProgressUpdater(Handler handler) {
            mHandler = handler;
        }

        public void update(String status) {
            mStatus = status;
            mHandler.sendEmptyMessage(MSG_PROGRESS);
        }
    }

    public static class ImportThread extends Thread {
        private final GpxImporter.CacheProgressUpdater mCacheProgressUpdater;
        private ErrorDisplayer mErrorDisplayer;
        private GpxLoader mGpxLoader;
        private Handler mHandler;
        private SQLiteDatabase mSqliteDatabase;

        public ImportThread(GpxImporter.CacheProgressUpdater cacheProgressUpdater, Handler handler,
                GpxLoader gpxLoader, ErrorDisplayer errorDisplayer, SQLiteDatabase sqliteDatabase) {
            mCacheProgressUpdater = cacheProgressUpdater;
            mHandler = handler;
            mGpxLoader = gpxLoader;
            mErrorDisplayer = errorDisplayer;
            mSqliteDatabase = sqliteDatabase;
        }

        public void run() {
            try {
                mGpxLoader.load(mCacheProgressUpdater);
                mHandler.sendEmptyMessage(MSG_DONE);
                mGpxLoader = null;
            } catch (Exception e) {
                mErrorDisplayer.displayErrorAndStack(e);
            } finally {
                mSqliteDatabase.close();
            }
        }
    }

    public static class MessageHandler extends Handler {
        private CacheListDelegate mCacheListDelegate;
        private GpxImporter mGpxImporter;

        public MessageHandler(GpxImporter gpxImporter, CacheListDelegate cacheListDelegate) {
            mGpxImporter = gpxImporter;
            mCacheListDelegate = cacheListDelegate;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS:
                    mGpxImporter.mProgressDialog
                            .setMessage(mGpxImporter.mCacheProgressUpdater.mStatus);
                    break;
                case MSG_DONE:
                    mGpxImporter.mProgressDialog.dismiss();
                    mCacheListDelegate.onResume();
                    break;
                default:
                    break;
            }
        }
    }
    static final int MSG_DONE = 1;
    static final int MSG_PROGRESS = 0;
    private CacheProgressUpdater mCacheProgressUpdater;
    private ErrorDisplayer mErrorDisplayer;
    private GpxLoader mGpxLoader;
    private Thread mImportThread;
    private ListActivity mListActivity;
    private ProgressDialog mProgressDialog;

    public void abortLoad(CacheListDelegate cacheListDelegate) {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mGpxLoader != null) {
                mGpxLoader.abortLoad();
                if (mImportThread != null) {
                    mImportThread.join();
                    Toast mToast = Toast.makeText(mListActivity, R.string.import_canceled,
                            Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        } catch (InterruptedException e) {
            mErrorDisplayer.displayErrorAndStack(e);
        } catch (Exception e) {
            // None of these errors get displayed because the activity is
            // terminating.
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    public boolean load(CacheListDelegate cacheListDelegate, SQLiteDatabase sqliteDatabase,
            Database database, ErrorDisplayer errorDisplayer, ListActivity listActivity) {
        try {
            mErrorDisplayer = errorDisplayer;
            mListActivity = listActivity;
            mGpxLoader = GpxLoader.create(listActivity, errorDisplayer, database, sqliteDatabase);
            MessageHandler messageHandler = new MessageHandler(this, cacheListDelegate);
            mCacheProgressUpdater = new GpxImporter.CacheProgressUpdater(messageHandler);
            if (mGpxLoader != null) {
                mProgressDialog = ProgressDialog.show(listActivity, "Importing Caches",
                        "Please wait...");

                mImportThread = new ImportThread(mCacheProgressUpdater, messageHandler, mGpxLoader,
                        errorDisplayer, sqliteDatabase);
                mImportThread.start();
            }
        } catch (final FileNotFoundException e) {
            errorDisplayer.displayError("Unable to open file '" + e.getMessage()
                    + "'.  Please ensure that the cache import file exists "
                    + "and that your sdcard is unmounted.");
        } catch (final Exception e) {
            errorDisplayer.displayErrorAndStack(e);
        }
        return true;
    }

}
