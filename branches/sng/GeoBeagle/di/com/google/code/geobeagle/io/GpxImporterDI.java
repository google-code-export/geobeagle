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

import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporter.GpxFilenameFactory;
import com.google.code.geobeagle.io.GpxImporter.GpxFiles;
import com.google.code.geobeagle.io.ImportThreadDelegate.ImportThreadHelper;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class GpxImporterDI {
    // Can't test this due to final methods in base.
    public static class ImportThread extends Thread {
        static ImportThread create(MessageHandler messageHandler, GpxLoader gpxLoader,
                ErrorDisplayer errorDisplayer) {
            final GpxFilenameFactory gpxFilenameFactory = new GpxFilenameFactory(
                    GpxImporter.filenameFilter);
            return new ImportThread(messageHandler, gpxLoader, errorDisplayer, gpxFilenameFactory);
        }

        private final ImportThreadDelegate mImportThreadDelegate;

        public ImportThread(MessageHandler messageHandler, GpxLoader gpxLoader,
                ErrorDisplayer errorDisplayer, GpxFilenameFactory gpxFilenameFactory) {
            final GpxFiles gpxFiles = new GpxFiles(GpxImporter.filenameFilter);
            final ImportThreadHelper importThreadHelper = new ImportThreadHelper(gpxLoader,
                    messageHandler, errorDisplayer);
            mImportThreadDelegate = new ImportThreadDelegate(gpxFiles, importThreadHelper);
        }

        @Override
        public void run() {
            mImportThreadDelegate.run();
        }
    }

    // Wrapper so that containers can follow the "constructors do no work" rule.
    public static class ImportThreadWrapper {
        private ImportThread mImportThread;
        private final MessageHandler mMessageHandler;

        public ImportThreadWrapper(MessageHandler messageHandler) {
            mMessageHandler = messageHandler;
        }

        public boolean isAlive() {
            if (mImportThread != null)
                return mImportThread.isAlive();
            return false;
        }

        public void join() throws InterruptedException {
            if (mImportThread != null)
                mImportThread.join();
        }

        public void open(CacheListDelegate cacheListDelegate, GpxLoader gpxLoader,
                ErrorDisplayer mErrorDisplayer) {
            mMessageHandler.start(cacheListDelegate);
            mImportThread = ImportThread.create(mMessageHandler, gpxLoader, mErrorDisplayer);
        }

        public void start() {
            if (mImportThread != null)
                mImportThread.start();
        }
    }

    // Too hard to test this class due to final methods in base.
    public static class MessageHandler extends Handler {
        static final int MSG_DONE = 1;
        static final int MSG_PROGRESS = 0;

        public static MessageHandler create(ListActivity listActivity) {
            final ProgressDialogWrapper progressDialogWrapper = new ProgressDialogWrapper(
                    listActivity);
            return new MessageHandler(progressDialogWrapper);
        }

        private int mCacheCount;
        private CacheListDelegate mCacheListDelegate;
        private boolean mLoadAborted;

        private final ProgressDialogWrapper mProgressDialogWrapper;
        private String mSource;
        private String mStatus;
        private String mWaypointId;

        public MessageHandler(ProgressDialogWrapper progressDialogWrapper) {
            mProgressDialogWrapper = progressDialogWrapper;
        }

        public void abortLoad() {
            mLoadAborted = true;
            mProgressDialogWrapper.dismiss();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageHandler.MSG_PROGRESS:
                    mProgressDialogWrapper.setMessage(mStatus);
                    break;
                case MessageHandler.MSG_DONE:
                    if (!mLoadAborted) {
                        mProgressDialogWrapper.dismiss();
                        mCacheListDelegate.onResume();
                    }
                    break;
                default:
                    break;
            }
        }

        public void loadComplete() {
            sendEmptyMessage(MessageHandler.MSG_DONE);
        }

        public void start(CacheListDelegate cacheListDelegate) {
            mCacheCount = 0;
            mLoadAborted = false;
            mCacheListDelegate = cacheListDelegate;
            mProgressDialogWrapper.show("Importing caches", "Please wait...");
        }

        public void updateName(String name) {
            mStatus = mCacheCount++ + ": " + mSource + " - " + mWaypointId + " - " + name;
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        public void updateSource(String text) {
            mSource = text;
        }

        public void updateWaypointId(String wpt) {
            mWaypointId = wpt;
        }
    }

    // Wrapper so that containers can follow the "constructors do no work" rule.
    public static class ProgressDialogWrapper {
        private final Context mContext;
        private ProgressDialog mProgressDialog;

        public ProgressDialogWrapper(Context context) {
            mContext = context;
        }

        public void dismiss() {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }

        public void setMessage(CharSequence message) {
            mProgressDialog.setMessage(message);
        }

        public void show(String title, String msg) {
            mProgressDialog = ProgressDialog.show(mContext, title, msg);
        }
    }

    public static class ToastFactory {
        public void showToast(Context context, int resId, int duration) {
            Toast.makeText(context, resId, duration).show();
        }
    }

    public static GpxImporter create(Database database, SQLiteWrapper sqliteWrapper,
            ErrorDisplayer errorDisplayer, ListActivity listActivity) {
        final MessageHandler messageHandler = MessageHandler.create(listActivity);
        final GpxLoader gpxLoader = GpxLoaderDI.create(listActivity, database, sqliteWrapper,
                messageHandler, errorDisplayer);
        final ToastFactory toastFactory = new ToastFactory();
        final ImportThreadWrapper importThreadWrapper = new ImportThreadWrapper(messageHandler);

        return new GpxImporter(gpxLoader, database, sqliteWrapper, listActivity,
                importThreadWrapper, messageHandler, errorDisplayer, toastFactory);
    }

}
