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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilesAndZipFilesIter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;
import com.google.inject.Inject;

import roboguice.util.RoboThread;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportThread extends RoboThread {

    static class ImportThreadFactory {

        private final BCachingStartTime bcachingStartTime;
        private final CacheListRefresh cacheListRefresh;
        private final DbFrontend dbFrontend;
        private final ErrorDisplayer errorDisplayer;
        private final FileDataVersionChecker fileDataVersionChecker;
        private final FileDataVersionWriter fileDataVersionWriter;
        private final GeoBeagleEnvironment geoBeagleEnvironment;
        private final GpxAndZipFiles gpxAndZipFiles;
        private final GpxToCacheFactory gpxToCacheFactory;
        private final MessageHandler messageHandlerInterface;
        private final OldCacheFilesCleaner oldCacheFilesCleaner;
        private final SharedPreferences sharedPreferences;
        private final UpdateFlag updateFlag;

        @Inject
        public ImportThreadFactory(MessageHandler messageHandler,
                ErrorDisplayer errorDisplayer,
                CacheListRefresh cacheListRefresh,
                GeoBeagleEnvironment geoBeagleEnvironment,
                SharedPreferences sharedPreferences,
                GpxAndZipFiles gpxAndZipFiles,
                GpxToCacheFactory gpxToCacheFactory,
                FileDataVersionWriter fileDataVersionWriter,
                FileDataVersionChecker fileDataVersionChecker,
                BCachingStartTime bcachingStartTime,
                UpdateFlag updateFlag,
                DbFrontend dbFrontend,
                OldCacheFilesCleaner oldCacheFilesCleaner) {
            this.messageHandlerInterface = messageHandler;
            this.errorDisplayer = errorDisplayer;
            this.cacheListRefresh = cacheListRefresh;
            this.geoBeagleEnvironment = geoBeagleEnvironment;
            this.sharedPreferences = sharedPreferences;
            this.gpxAndZipFiles = gpxAndZipFiles;
            this.gpxToCacheFactory = gpxToCacheFactory;
            this.fileDataVersionWriter = fileDataVersionWriter;
            this.fileDataVersionChecker = fileDataVersionChecker;
            this.bcachingStartTime = bcachingStartTime;
            this.updateFlag = updateFlag;
            this.dbFrontend = dbFrontend;
            this.oldCacheFilesCleaner = oldCacheFilesCleaner;
        }

        public ImportThread create() {
            messageHandlerInterface.start(cacheListRefresh);

            final GpxToCache gpxToCache = gpxToCacheFactory.create(messageHandlerInterface);
            return new ImportThread(gpxAndZipFiles, errorDisplayer, fileDataVersionWriter,
                    dbFrontend, fileDataVersionChecker, bcachingStartTime, updateFlag,
                    messageHandlerInterface, oldCacheFilesCleaner, sharedPreferences, gpxToCache,
                    geoBeagleEnvironment);
        }
    }
    private final BCachingStartTime mBCachingStartTime;
    private final DbFrontend mDbFrontend;
    private final ErrorDisplayer mErrorDisplayer;
    private final FileDataVersionChecker mFileDataVersionChecker;
    private final FileDataVersionWriter mFileDataVersionWriter;
    private final GeoBeagleEnvironment mGeoBeagleEnvironment;
    private final GpxAndZipFiles mGpxAndZipFiles;
    private final GpxToCache mGpxToCache;
    private boolean mHasFiles;
    private boolean mIsAlive;
    private final MessageHandler mMessageHandler;
    private final OldCacheFilesCleaner mOldCacheFilesCleaner;
    private final SharedPreferences mSharedPreferences;

    private final UpdateFlag mUpdateFlag;

    public ImportThread(GpxAndZipFiles gpxAndZipFiles,
            ErrorDisplayer errorDisplayer,
            FileDataVersionWriter fileDataVersionWriter,
            DbFrontend dbFrontend,
            FileDataVersionChecker fileDataVersionChecker,
            BCachingStartTime bcachingStartTime,
            UpdateFlag updateFlag,
            MessageHandler messageHandlerInterface,
            OldCacheFilesCleaner oldCacheFilesCleaner,
            SharedPreferences sharedPreferences,
            GpxToCache gpxToCache,
            GeoBeagleEnvironment geoBeagleEnvironment) {
        mGpxAndZipFiles = gpxAndZipFiles;
        mErrorDisplayer = errorDisplayer;
        mFileDataVersionWriter = fileDataVersionWriter;
        mFileDataVersionChecker = fileDataVersionChecker;
        mBCachingStartTime = bcachingStartTime;
        mDbFrontend = dbFrontend;
        mUpdateFlag = updateFlag;
        mMessageHandler = messageHandlerInterface;
        mHasFiles = false;
        mOldCacheFilesCleaner = oldCacheFilesCleaner;
        mSharedPreferences = sharedPreferences;
        mGpxToCache = gpxToCache;
        mGeoBeagleEnvironment = geoBeagleEnvironment;
    }

    public boolean isAliveHack() {
        return mIsAlive;
    }

    @Override
    public void run() {
        mIsAlive = true;
        try {
            mUpdateFlag.setUpdatesEnabled(false);
            tryRun();
        } catch (final FileNotFoundException e) {
            mErrorDisplayer.displayError(R.string.error_opening_file, e.getMessage());
            return;
        } catch (IOException e) {
            mErrorDisplayer.displayError(R.string.error_reading_file, e.getMessage());
            return;
        } catch (ImportException e) {
            mErrorDisplayer.displayError(e.getError(), e.getPath());
            return;
        } catch (com.google.code.geobeagle.xmlimport.GpxToCache.CancelException e) {
            return;
        } finally {
            mUpdateFlag.setUpdatesEnabled(true);
            mMessageHandler.loadComplete();
            mIsAlive = false;
        }
        Log.d("GeoBeagle", "STARTING BCACHING IMPORT");
        mMessageHandler.startBCachingImport();
    }

    void endImport() throws ImportException {
        mGpxToCache.end();
        if (!mHasFiles
                && mSharedPreferences.getString(BCachingModule.BCACHING_USERNAME, "").length() == 0)
            throw new ImportException(R.string.error_no_gpx_files,
                    mGeoBeagleEnvironment.getImportFolder());
    }

    void processFile(GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter) throws IOException,
            CancelException {
        IGpxReader gpxReader = gpxFilesAndZipFilesIter.next();
        String filename = gpxReader.getFilename();

        mHasFiles = true;
        mGpxToCache.load(filename, gpxReader.open());
    }

    void startImport() {
        mOldCacheFilesCleaner.clean(mMessageHandler);
        mGpxToCache.start();
    }

    void tryRun() throws IOException, ImportException, CancelException {
        if (mFileDataVersionChecker.needsUpdating()) {
            mDbFrontend.forceUpdate();
            mBCachingStartTime.clearStartTime();
        }
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = mGpxAndZipFiles.iterator();
        gpxFilesAndZipFilesIter.getCount();
        startImport();
        while (gpxFilesAndZipFilesIter.hasNext()) {
            processFile(gpxFilesAndZipFilesIter);
        }
        mFileDataVersionWriter.writeVersion();
        endImport();
    }
}
