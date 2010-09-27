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
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory;
import com.google.code.geobeagle.xmlimport.EventHelperDI.EventHelperFactory;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.ImportThreadDelegate.ImportThreadHelper;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxFileIterAndZipFileIterFactory;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipInputFileTester;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import roboguice.inject.ContextScoped;
import roboguice.util.RoboThread;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

import java.io.FilenameFilter;
import java.util.Arrays;

public class GpxImporterDI {
    // Can't test this due to final methods in base.
    public static class ImportThread extends RoboThread {
        static ImportThread create(MessageHandlerInterface messageHandler, GpxLoader gpxLoader,
                EventHandler eventHandler, XmlPullParserWrapper xmlPullParserWrapper,
                ErrorDisplayer errorDisplayer, Aborter aborter, Injector injector) {
            final GpxFilenameFilter gpxFilenameFilter = new GpxFilenameFilter();
            final FilenameFilter filenameFilter = new GpxAndZipFilenameFilter(gpxFilenameFilter);
            final ZipInputFileTester zipInputFileTester = new ZipInputFileTester(gpxFilenameFilter);
            final GeoBeagleEnvironment geoBeagleEnvironment = injector
                    .getInstance(GeoBeagleEnvironment.class);
            final GpxFileIterAndZipFileIterFactory gpxFileIterAndZipFileIterFactory = new GpxFileIterAndZipFileIterFactory(
                    zipInputFileTester, aborter, geoBeagleEnvironment);
            final SharedPreferences sharedPreferences = injector
                    .getInstance(SharedPreferences.class);
            final GpxAndZipFiles gpxAndZipFiles = new GpxAndZipFiles(filenameFilter,
                    gpxFileIterAndZipFileIterFactory, geoBeagleEnvironment, sharedPreferences);
            final EventHelperFactory eventHelperFactory = new EventHelperFactory(
                    xmlPullParserWrapper);
            final OldCacheFilesCleaner oldCacheFilesCleaner = new OldCacheFilesCleaner(
                    injector.getInstance(GeoBeagleEnvironment.class), messageHandler);

            final ImportThreadHelper importThreadHelper = new ImportThreadHelper(gpxLoader,
                    messageHandler, eventHelperFactory, eventHandler, oldCacheFilesCleaner,
                    sharedPreferences, geoBeagleEnvironment);
            final FileDataVersionWriter fileDataVersionWriter = injector
                    .getInstance(FileDataVersionWriter.class);
            final FileDataVersionChecker fileDataVersionChecker = injector
                    .getInstance(FileDataVersionChecker.class);
            final BCachingStartTime bcachingStartTime = injector.getInstance(BCachingStartTime.class);
            final UpdateFlag updateFlag = injector.getInstance(UpdateFlag.class);
            return new ImportThread(gpxAndZipFiles, importThreadHelper, errorDisplayer,
                    fileDataVersionWriter, injector.getInstance(DbFrontend.class),
                    fileDataVersionChecker, bcachingStartTime, updateFlag);
        }

        private final ImportThreadDelegate mImportThreadDelegate;

        public ImportThread(GpxAndZipFiles gpxAndZipFiles, ImportThreadHelper importThreadHelper,
                ErrorDisplayer errorDisplayer, FileDataVersionWriter fileDataVersionWriter,
                DbFrontend dbFrontend, FileDataVersionChecker fileDataVersionChecker,
                BCachingStartTime bcachingStartTime, UpdateFlag updateFlag) {
            mImportThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper,
                    errorDisplayer, fileDataVersionWriter, fileDataVersionChecker, dbFrontend,
                    bcachingStartTime, updateFlag);
        }

        @Override
        public void run() {
            mImportThreadDelegate.run();
        }

        public boolean isAliveHack() {
            return mImportThreadDelegate.isAlive();
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
                return mImportThread.isAliveHack();
            return false;
        }

        public void join() {
            if (mImportThread != null)
                try {
                    while (!isAlive()) {
                        Log.d("GeoBeagle", "Sleeping while gpx import completes");
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    // Ignore; we are aborting anyway.
                }
        }

        public void open(CacheListRefresh cacheListRefresh, GpxLoader gpxLoader,
                EventHandler eventHandler, ErrorDisplayer mErrorDisplayer, Injector injector) {
            mMessageHandler.start(cacheListRefresh);
            mImportThread = ImportThread.create(mMessageHandler, gpxLoader, eventHandler,
                    mXmlPullParserWrapper, mErrorDisplayer, mAborter, injector);
        }

        public void start() {
            if (mImportThread != null)
                mImportThread.start();
        }
    }

    // Too hard to test this class due to final methods in base.
    @ContextScoped
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
        private final SharedPreferences mSharedPreferences;

        @Inject
        public MessageHandler(ProgressDialogWrapper progressDialogWrapper,
                Provider<ImportBCachingWorker> importBCachingWorkerProvider,
                SharedPreferences sharedPreferences) {
            mProgressDialogWrapper = progressDialogWrapper;
            mImportBCachingWorkerProvider = importBCachingWorkerProvider;
            mSharedPreferences = sharedPreferences;
        }

        @Override
        public void abortLoad() {
            mLoadAborted = true;
            mProgressDialogWrapper.dismiss();
        }

        @Override
        public void handleMessage(Message msg) {
//            Log.d(GEOBEAGLE, "received msg: " + msg.what);
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
                    if (mLoadAborted) {
                        return;
                    }

                    if (mSharedPreferences.getBoolean(BCachingModule.BCACHING_ENABLED, false)) {
                        ImportBCachingWorker importBCachingWorker = mImportBCachingWorkerProvider
                                .get();
                        importBCachingWorker.start();
                        while (!importBCachingWorker.inProgress())
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Log.d("GeoBeagle",
                                        "InterruptedException while waiting for bcaching thread to die: "
                                                + e);
                                e.printStackTrace();
                            }
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void loadComplete() {
            sendEmptyMessage(MessageHandler.MSG_DONE);
        }

        @Override
        public void start(CacheListRefresh cacheListRefresh) {
            mCacheCount = 0;
            mLoadAborted = false;
            mMenuActionRefresh = cacheListRefresh;
            // TODO: move text into resource.
            mProgressDialogWrapper.show("Sync from sdcard", "Please wait...");
        }

        @Override
        public void updateName(String name) {
            mStatus = mCacheCount++ + ": " + mSource + " - " + mWaypointId + " - " + name;
            if (!hasMessages(MessageHandler.MSG_PROGRESS))
                sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        @Override
        public void updateSource(String text) {
            mSource = text;
            mStatus = "Opening: " + mSource + "...";
            if (!hasMessages(MessageHandler.MSG_PROGRESS))
                sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        @Override
        public void updateWaypointId(String wpt) {
            mWaypointId = wpt;
        }

        @Override
        public void updateStatus(String status) {
            mStatus = status;
            if (!hasMessages(MessageHandler.MSG_PROGRESS))
                sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        @Override
        public void deletingCacheFiles() {
            mStatus = "Deleting old cache files....";
            if (!hasMessages(MessageHandler.MSG_PROGRESS))
                sendEmptyMessage(MessageHandler.MSG_PROGRESS);
        }

        @Override
        public void startBCachingImport() {
            sendEmptyMessage(MessageHandler.MSG_BCACHING_IMPORT);
        }

    }

    // Wrapper so that containers can follow the "constructors do no work" rule.
    public static class ProgressDialogWrapper {
        private final Provider<Context> mContextProvider;
        private ProgressDialog mProgressDialog;

        @Inject
        public ProgressDialogWrapper(Provider<Context> context) {
            mContextProvider = context;
        }

        public void dismiss() {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }

        public void setMessage(CharSequence message) {
            mProgressDialog.setMessage(message);
        }

        public void show(String title, String msg) {
            mProgressDialog = ProgressDialog.show(mContextProvider.get(), title, msg);
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

    public static GpxImporter create(
            Pausable geocacheListPresenter,
            Aborter aborter,
            MessageHandlerInterface messageHandler, CachePersisterFacadeFactory cachePersisterFacadeFactory,
            CacheWriter cacheWriter,
            GpxWriter gpxWriter,
            Injector injector) {
        final ErrorDisplayer errorDisplayer = injector.getInstance(ErrorDisplayer.class);
        final Context context = injector.getInstance(Context.class);
        final PowerManager powerManager = (PowerManager)context
                .getSystemService(Context.POWER_SERVICE);
        final WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "Importing");

        final GeoBeagleEnvironment geoBeagleEnvironment = injector
                .getInstance(GeoBeagleEnvironment.class);
        final ImportCacheActions importCacheActions = cachePersisterFacadeFactory.create(
                cacheWriter, gpxWriter, wakeLock, geoBeagleEnvironment);

        XmlPullParserWrapper xmlPullParserWrapper = injector
                .getInstance(XmlPullParserWrapper.class);
        final GpxLoader gpxLoader = GpxLoaderDI.create(importCacheActions, xmlPullParserWrapper,
                aborter, errorDisplayer, wakeLock, gpxWriter);
        final ToastFactory toastFactory = new ToastFactory();
        final ImportThreadWrapper importThreadWrapper = new ImportThreadWrapper(messageHandler,
                xmlPullParserWrapper, aborter);
        final EventHandlerGpx eventHandlerGpx = new EventHandlerGpx();
        final XmlWriter xmlWriter = injector.getInstance(XmlWriter.class);

        final EventHandlerComposite eventHandlerComposite = new EventHandlerComposite(Arrays
                .asList(xmlWriter, eventHandlerGpx));

        return new GpxImporter(geocacheListPresenter, gpxLoader,
                injector.getProvider(Context.class),
                importThreadWrapper,
                messageHandler, toastFactory, eventHandlerComposite, errorDisplayer,
                injector.getProvider(CacheListRefresh.class), injector);
    }

}
