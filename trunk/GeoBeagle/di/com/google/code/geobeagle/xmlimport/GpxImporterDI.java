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

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.activity.cachelist.Pausable;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.bcaching.BCachingAnnotations.BCachingUserName;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory;
import com.google.code.geobeagle.xmlimport.EventHelperDI.EventHelperFactory;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;
import com.google.code.geobeagle.xmlimport.ImportThreadDelegate.ImportThreadHelper;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.ImportFolder;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxFileIterAndZipFileIterFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipInputFileTester;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;

import roboguice.util.RoboThread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

import java.io.FilenameFilter;

public class GpxImporterDI {
    // Can't test this due to final methods in base.
    public static class ImportThread extends RoboThread {
        static ImportThread create(MessageHandlerInterface messageHandler, GpxLoader gpxLoader,
                EventHandlers eventHandlers, XmlPullParserWrapper xmlPullParserWrapper,
                ErrorDisplayer errorDisplayer, Aborter aborter, Injector injector) {
            final GpxFilenameFilter gpxFilenameFilter = new GpxFilenameFilter();
            final FilenameFilter filenameFilter = new GpxAndZipFilenameFilter(gpxFilenameFilter);
            final ZipInputFileTester zipInputFileTester = new ZipInputFileTester(gpxFilenameFilter);
            Provider<String> importFolderProvider = injector.getProvider(Key.get(String.class,
                    ImportFolder.class));
            final GpxFileIterAndZipFileIterFactory gpxFileIterAndZipFileIterFactory = new GpxFileIterAndZipFileIterFactory(
                    zipInputFileTester, aborter, importFolderProvider);
            final GpxAndZipFiles gpxAndZipFiles = new GpxAndZipFiles(filenameFilter,
                    gpxFileIterAndZipFileIterFactory, importFolderProvider);
            final EventHelperFactory eventHelperFactory = new EventHelperFactory(
                    xmlPullParserWrapper);
            OldCacheFilesCleaner oldCacheFilesCleaner = new OldCacheFilesCleaner(
                    CacheDetailsLoader.OLD_DETAILS_DIR, messageHandler);
            final ImportThreadHelper importThreadHelper = new ImportThreadHelper(gpxLoader,
                    messageHandler, eventHelperFactory, eventHandlers, oldCacheFilesCleaner,
                    injector.getProvider(Key.get(String.class, BCachingUserName.class)),
                    importFolderProvider);
            final FileDataVersionWriter fileDataVersionWriter = injector
                    .getInstance(FileDataVersionWriter.class);
            final FileDataVersionChecker fileDataVersionChecker = injector
                    .getInstance(FileDataVersionChecker.class);
            return new ImportThread(gpxAndZipFiles, importThreadHelper, errorDisplayer,
                    fileDataVersionWriter, injector.getInstance(DbFrontend.class),
                    fileDataVersionChecker);
        }

        private final ImportThreadDelegate mImportThreadDelegate;

        public ImportThread(GpxAndZipFiles gpxAndZipFiles, ImportThreadHelper importThreadHelper,
                ErrorDisplayer errorDisplayer, FileDataVersionWriter fileDataVersionWriter,
                DbFrontend dbFrontend, FileDataVersionChecker fileDataVersionChecker) {
            mImportThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper,
                    errorDisplayer, fileDataVersionWriter, fileDataVersionChecker, dbFrontend);
        }

        @Override
        public void run() {
            mImportThreadDelegate.run();
        }
    }

    // Wrapper so that containers can follow the "constructors do no work" rule.
    public static class ImportThreadWrapper {
        private final Aborter mAborter;
        private ImportThread mImportThread;
        private final MessageHandlerInterface mMessageHandler;
        private final XmlPullParserWrapper mXmlPullParserWrapper;

        public ImportThreadWrapper(MessageHandlerInterface messageHandler,
                XmlPullParserWrapper xmlPullParserWrapper, Aborter aborter) {
            mMessageHandler = messageHandler;
            mXmlPullParserWrapper = xmlPullParserWrapper;
            mAborter = aborter;
        }

        public boolean isAlive() {
            if (mImportThread != null)
                return mImportThread.isAlive();
            return false;
        }

        public void join() {
            if (mImportThread != null)
                try {
                    mImportThread.join();
                } catch (InterruptedException e) {
                    // Ignore; we are aborting anyway.
                }
        }

        public void open(CacheListRefresh cacheListRefresh, GpxLoader gpxLoader,
                EventHandlers eventHandlers, ErrorDisplayer mErrorDisplayer, Injector injector) {
            mMessageHandler.start(cacheListRefresh);
            mImportThread = ImportThread.create(mMessageHandler, gpxLoader, eventHandlers,
                    mXmlPullParserWrapper, mErrorDisplayer, mAborter, injector);
        }

        public void start() {
            if (mImportThread != null)
                mImportThread.start();
        }
    }

    // Too hard to test this class due to final methods in base.
    public static class MessageHandler extends Handler implements MessageHandlerInterface {
        public static final String GEOBEAGLE = "GeoBeagle";
        static final int MSG_DONE = 1;
        static final int MSG_PROGRESS = 0;
        private static final int MSG_BCACHING_IMPORT = 2;

        private int mCacheCount;
        private boolean mLoadAborted;
        private CacheListRefresh mMenuActionRefresh;
        private final ProgressDialogWrapper mProgressDialogWrapper;
        private String mSource;
        private String mStatus;
        private String mWaypointId;
        private final Provider<ImportBCachingWorker> mImportBCachingWorkerProvider;
        private final Provider<String> mBcachingUserNameProvider;

        @Inject
        public MessageHandler(ProgressDialogWrapper progressDialogWrapper,
                Provider<ImportBCachingWorker> importBCachingWorkerProvider,
                @BCachingUserName Provider<String> bcachingUserNameProvider) {
            mProgressDialogWrapper = progressDialogWrapper;
            mImportBCachingWorkerProvider = importBCachingWorkerProvider;
            mBcachingUserNameProvider = bcachingUserNameProvider;
        }

        public void abortLoad() {
            mLoadAborted = true;
            mProgressDialogWrapper.dismiss();
        }

        @Override
        public void handleMessage(Message msg) {
            // Log.d(GEOBEAGLE, "received msg: " + msg.what);
            switch (msg.what) {
                case MessageHandler.MSG_PROGRESS:
                    mProgressDialogWrapper.setMessage(mStatus);
                    break;
                case MessageHandler.MSG_DONE:
                    if (!mLoadAborted) {
                        mProgressDialogWrapper.dismiss();
                        mMenuActionRefresh.forceRefresh();
                    }
                    break;
                case MessageHandler.MSG_BCACHING_IMPORT:
                    if (mBcachingUserNameProvider.get().length() > 0)
                        mImportBCachingWorkerProvider.get().start();
                    break;
                default:
                    break;
            }
        }

        public void loadComplete() {
            sendEmptyMessage(MessageHandler.MSG_DONE);
        }

        public void start(CacheListRefresh cacheListRefresh) {
            mCacheCount = 0;
            mLoadAborted = false;
            mMenuActionRefresh = cacheListRefresh;
            // TODO: move text into resource.
            mProgressDialogWrapper.show("Sync from sdcard", "Please wait...");
        }

        public void updateName(String name) {
            mStatus = mCacheCount++ + ": " + mSource + " - " + mWaypointId + " - " + name;
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        public void updateSource(String text) {
            mSource = text;
            mStatus = "Opening: " + mSource + "...";
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        public void updateWaypointId(String wpt) {
            mWaypointId = wpt;
        }

        public void updateStatus(String status) {
            mStatus = status;
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        public void deletingCacheFiles() {
            mStatus = "Deleting old cache files....";
            sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        @Override
        public void startBCachingImport() {
            sendEmptyMessage(MessageHandler.MSG_BCACHING_IMPORT);
        }

    }

    // Wrapper so that containers can follow the "constructors do no work" rule.
    public static class ProgressDialogWrapper {
        private final Context mContext;
        private ProgressDialog mProgressDialog;

        @Inject
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
//            mProgressDialog.setCancelable(true);
        }
    }

    public static class ToastFactory {
        public void showToast(Context context, int resId, int duration) {
            Toast.makeText(context, resId, duration).show();
        }
    }

    public static class Toaster {
        private final Context mContext;
        private final int mResId;
        private final int mDuration;

        public Toaster(Context context, int resId, int duration) {
            mContext = context;
            mResId = resId;
            mDuration = duration;
        }

        public void showToast() {
            Toast.makeText(mContext, mResId, mDuration).show();
        }
    }

    public static GpxImporter create(Context context, XmlPullParserWrapper xmlPullParserWrapper,
            ErrorDisplayer errorDisplayer, Pausable geocacheListPresenter, Aborter aborter,
            MessageHandlerInterface messageHandler, CachePersisterFacadeFactory cachePersisterFacadeFactory,
            CacheWriter cacheWriter, Injector injector) {
        final PowerManager powerManager = (PowerManager)context
                .getSystemService(Context.POWER_SERVICE);
        final WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "Importing");

        final CachePersisterFacade cachePersisterFacade = cachePersisterFacadeFactory.create(
                cacheWriter, wakeLock);

        final GpxLoader gpxLoader = GpxLoaderDI.create(cachePersisterFacade, xmlPullParserWrapper,
                aborter, errorDisplayer, wakeLock, cacheWriter);
        final ToastFactory toastFactory = new ToastFactory();
        final ImportThreadWrapper importThreadWrapper = new ImportThreadWrapper(messageHandler,
                xmlPullParserWrapper, aborter);
        final EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        final EventHandlerLoc eventHandlerLoc = new EventHandlerLoc(cachePersisterFacade);

        final EventHandlers eventHandlers = new EventHandlers();
        eventHandlers.add("gpx", eventHandlerGpx);
        eventHandlers.add("loc", eventHandlerLoc);

        return new GpxImporter(geocacheListPresenter, gpxLoader, context, importThreadWrapper,
                messageHandler, toastFactory, eventHandlers, errorDisplayer, injector);
    }

}
