
package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.GpxImporter;
import com.google.code.geobeagle.io.GpxLoader;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporter.ImportThreadDelegate;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;

public class GpxImporterDI {

    public static class ToastFactory {
        public void showToast(Context context, int resId, int duration) {
            Toast.makeText(context, resId, duration).show();
        }
    }

    // Can't test this due to final methods in base.
    public static class ImportThread extends Thread {
        static ImportThread create(MessageHandler messageHandler, GpxLoader gpxLoader,
                ErrorDisplayer errorDisplayer) {
            final GpxImporterDI.GpxFilenameFactory gpxFilenameFactory = new GpxImporterDI.GpxFilenameFactory();
            return new ImportThread(messageHandler, gpxLoader, errorDisplayer, gpxFilenameFactory);
        }

        private final GpxImporter.ImportThreadDelegate mImportThreadDelegate;

        public ImportThread(MessageHandler messageHandler, GpxLoader gpxLoader,
                ErrorDisplayer errorDisplayer, GpxImporterDI.GpxFilenameFactory gpxFilenameFactory) {
            mImportThreadDelegate = new ImportThreadDelegate(gpxLoader, messageHandler,
                    errorDisplayer, gpxFilenameFactory);
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

        private CacheListDelegate mCacheListDelegate;
        private boolean mLoadAborted;
        private final ProgressDialogWrapper mProgressDialogWrapper;

        private String mStatus;

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
            mLoadAborted = false;
            mCacheListDelegate = cacheListDelegate;
            mProgressDialogWrapper.show("Importing caches", "Please wait...");
        }

        public void workerSendUpdate(String status) {
            mStatus = status;
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
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

    public static class GpxFilenameFactory {
    
        public String[] getFilenames() {
            File dir = new File("/sdcard");
    
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return !name.startsWith(".") && name.endsWith(".gpx");
                }
            };
            return dir.list(filter);
        }
    
    }

    public static GpxImporter create(Database database, SQLiteWrapper sqliteWrapper,
            ErrorDisplayer errorDisplayer, ListActivity listActivity) {
        final MessageHandler messageHandler = MessageHandler.create(listActivity);
        final GpxLoader gpxLoader = GpxLoaderDI.create(database, sqliteWrapper, messageHandler,
                errorDisplayer);
        final ToastFactory toastFactory = new ToastFactory();
        final ImportThreadWrapper importThreadWrapper = new ImportThreadWrapper(messageHandler);
        return new GpxImporter(gpxLoader, database, sqliteWrapper, listActivity,
                importThreadWrapper, messageHandler, errorDisplayer, toastFactory);
    }

}
