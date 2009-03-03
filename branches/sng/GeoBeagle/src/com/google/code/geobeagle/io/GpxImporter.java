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
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxLoader.Factory;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class GpxImporter {

    // Too hard to test this class due to final methods in base.
    public static class ImportThread extends Thread {
        public static class Factory {
            private final ErrorDisplayer mErrorDisplayer;

            public Factory(ErrorDisplayer errorDisplayer) {
                mErrorDisplayer = errorDisplayer;
            }

            ImportThread create(CacheListDelegate cacheListDelegate,
                    ProgressDialogWrapper progressDialog, GpxLoader gpxLoader) {
                final MessageHandler messageHandler = new MessageHandler(cacheListDelegate,
                        progressDialog);
                return new ImportThread(messageHandler, gpxLoader, mErrorDisplayer);
            }
        }

        private final ErrorDisplayer mErrorDisplayer;
        private final GpxLoader mGpxLoader;
        private final MessageHandler mMessageHandler;

        public ImportThread(MessageHandler messageHandler, GpxLoader gpxLoader,
                ErrorDisplayer errorDisplayer) {
            mMessageHandler = messageHandler;
            mGpxLoader = gpxLoader;
            mErrorDisplayer = errorDisplayer;
        }

        public void run() {
            try {
                mGpxLoader.load(mMessageHandler);
                mMessageHandler.sendEmptyMessage(MessageHandler.MSG_DONE);
            } catch (Exception e) {
                mErrorDisplayer.displayErrorAndStack(e);
            }
        }
    }

    // Too hard to test this class due to final methods in base.
    public static class MessageHandler extends Handler {
        static final int MSG_DONE = 1;
        static final int MSG_PROGRESS = 0;
        private final CacheListDelegate mCacheListDelegate;
        private final ProgressDialogWrapper mProgressDialog;
        private String mStatus;

        public MessageHandler(CacheListDelegate cacheListDelegate,
                ProgressDialogWrapper progressDialog) {
            mCacheListDelegate = cacheListDelegate;
            mProgressDialog = progressDialog;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageHandler.MSG_PROGRESS:
                    mProgressDialog.setMessage(mStatus);
                    break;
                case MessageHandler.MSG_DONE:
                    mProgressDialog.dismiss();
                    mCacheListDelegate.onResume();
                    break;
                default:
                    break;
            }
        }

        public void update(String status) {
            mStatus = status;
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }
    }

    public static class ProgressDialogWrapper {
        ProgressDialog mProgressDialog;

        public void dismiss() {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }

        public void setMessage(CharSequence message) {
            mProgressDialog.setMessage(message);
        }

        public void show(ListActivity listActivity, String title, String msg) {
            mProgressDialog = ProgressDialog.show(listActivity, title, msg);
        }
    }

    private final ErrorDisplayer mErrorDisplayer;
    private GpxLoader mGpxLoader;
    private final Factory mGpxLoaderFactory;
    private Thread mImportThread;
    private final ImportThread.Factory mImportThreadFactory;
    private final ListActivity mListActivity;
    private final ProgressDialogWrapper mProgressDialog;
    private final SQLiteWrapper mSqliteWrapper;
    private final Database mDatabase;

    public GpxImporter(GpxLoader.Factory gpxLoaderFactory, Database database,
            ErrorDisplayer errorDisplayer, ListActivity listActivity,
            ImportThread.Factory importThreadFactory, ProgressDialogWrapper progressDialog,
            SQLiteWrapper sqliteWrapper) {
        mGpxLoaderFactory = gpxLoaderFactory;
        mDatabase = database;
        mErrorDisplayer = errorDisplayer;
        mListActivity = listActivity;
        mImportThreadFactory = importThreadFactory;
        mProgressDialog = progressDialog;
        mGpxLoader = mGpxLoaderFactory.create(sqliteWrapper);
        mSqliteWrapper = sqliteWrapper;
    }

    public void abort() {
        try {
            mProgressDialog.dismiss();
            if (mGpxLoader != null) {
                mGpxLoader.abortLoad();
                if (mImportThread != null && mImportThread.isAlive()) {
                    mImportThread.join();
                    mSqliteWrapper.close();
                    Toast mToast = Toast.makeText(mListActivity, R.string.import_canceled,
                            Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        } catch (InterruptedException e) {
            mErrorDisplayer.displayErrorAndStack(e);
        } catch (Exception e) {
            // None of these errors actually get displayed because the activity
            // is terminating.
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    public boolean importGpxs(CacheListDelegate cacheListDelegate) {
        try {
            mSqliteWrapper.openReadableDatabase(mDatabase);
            mGpxLoader.open();
            mProgressDialog.show(mListActivity, "Importing Caches", "Please wait...");
            mImportThread = mImportThreadFactory.create(cacheListDelegate, mProgressDialog,
                    mGpxLoader);
            mImportThread.start();
        } catch (final FileNotFoundException e) {
            mErrorDisplayer.displayError("Unable to open file '" + e.getMessage()
                    + "'.  Please ensure that the cache import file exists "
                    + "and that the sdcard is readable from your phone.");
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
        return true;
    }
}
