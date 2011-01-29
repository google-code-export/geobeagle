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
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilesAndZipFilesIter;
import com.google.inject.Injector;

import roboguice.util.RoboThread;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportThread extends RoboThread {

    private final ErrorDisplayer mErrorDisplayer;
    private final GpxAndZipFiles mGpxAndZipFiles;
    private final ImportThreadHelper mImportThreadHelper;
    private final FileDataVersionWriter mFileDataVersionWriter;
    private final FileDataVersionChecker mFileDataVersionChecker;
    private final DbFrontend mDbFrontend;
    private final BCachingStartTime mBCachingStartTime;
    private boolean mIsAlive;
    private final UpdateFlag mUpdateFlag;

    static class ImportThreadFactory {
        public ImportThread create(Injector injector) {

            final MessageHandler messageHandlerInterface = injector
                    .getInstance(MessageHandler.class);
            final ErrorDisplayer errorDisplayer = injector.getInstance(ErrorDisplayer.class);
            final CacheListRefresh cacheListRefresh = injector.getInstance(CacheListRefresh.class);
            messageHandlerInterface.start(cacheListRefresh);

            final GeoBeagleEnvironment geoBeagleEnvironment = injector
                    .getInstance(GeoBeagleEnvironment.class);
            final SharedPreferences sharedPreferences = injector
                    .getInstance(SharedPreferences.class);
            final GpxAndZipFiles gpxAndZipFiles = injector.getInstance(GpxAndZipFiles.class);
            final OldCacheFilesCleaner oldCacheFilesCleaner = new OldCacheFilesCleaner(
                    injector.getInstance(GeoBeagleEnvironment.class), messageHandlerInterface);

            final MessageHandlerInterface messageHandler = injector
                    .getInstance(MessageHandler.class);
            final GpxToCache gpxToCache = injector.getInstance(GpxToCacheFactory.class).create(
                    messageHandler);
            final ImportThreadHelper importThreadHelper = new ImportThreadHelper(gpxToCache,
                    messageHandlerInterface, oldCacheFilesCleaner, sharedPreferences,
                    geoBeagleEnvironment);
            final FileDataVersionWriter fileDataVersionWriter = injector
                    .getInstance(FileDataVersionWriter.class);
            final FileDataVersionChecker fileDataVersionChecker = injector
                    .getInstance(FileDataVersionChecker.class);
            final BCachingStartTime bcachingStartTime = injector
                    .getInstance(BCachingStartTime.class);
            final UpdateFlag updateFlag = injector.getInstance(UpdateFlag.class);
            return new ImportThread(gpxAndZipFiles, importThreadHelper, errorDisplayer,
                    fileDataVersionWriter, injector.getInstance(DbFrontend.class),
                    fileDataVersionChecker, bcachingStartTime, updateFlag);

        }
    }

    public ImportThread(GpxAndZipFiles gpxAndZipFiles,
            ImportThreadHelper importThreadHelper,
            ErrorDisplayer errorDisplayer,
            FileDataVersionWriter fileDataVersionWriter,
            DbFrontend dbFrontend,
            FileDataVersionChecker fileDataVersionChecker,
            BCachingStartTime bcachingStartTime,
            UpdateFlag updateFlag) {
        mGpxAndZipFiles = gpxAndZipFiles;
        mImportThreadHelper = importThreadHelper;
        mErrorDisplayer = errorDisplayer;
        mFileDataVersionWriter = fileDataVersionWriter;
        mFileDataVersionChecker = fileDataVersionChecker;
        mBCachingStartTime = bcachingStartTime;
        mDbFrontend = dbFrontend;
        mUpdateFlag = updateFlag;
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
            mImportThreadHelper.cleanup();
            mIsAlive = false;
        }
        Log.d("GeoBeagle", "STARTING BCACHING IMPORT");
        mImportThreadHelper.startBCachingImport();
    }

    void tryRun() throws IOException, ImportException, CancelException {
        if (mFileDataVersionChecker.needsUpdating()) {
            mDbFrontend.forceUpdate();
            mBCachingStartTime.clearStartTime();
        }
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = mGpxAndZipFiles.iterator();
        gpxFilesAndZipFilesIter.getCount();
        mImportThreadHelper.start();
        while (gpxFilesAndZipFilesIter.hasNext()) {
            mImportThreadHelper.processFile(gpxFilesAndZipFilesIter.next());
        }
        mFileDataVersionWriter.writeVersion();
        mImportThreadHelper.end();
    }

    public boolean isAliveHack() {
        return mIsAlive;
    }
}
