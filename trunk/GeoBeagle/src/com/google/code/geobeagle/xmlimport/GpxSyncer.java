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
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilesAndZipFilesIter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

public class GpxSyncer {

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

    public GpxSyncer(GpxAndZipFiles gpxAndZipFiles,
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

    public void sync() {
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
        } catch (CancelException e) {
            return;
        } finally {
            mUpdateFlag.setUpdatesEnabled(true);
            mMessageHandler.loadComplete();
            mIsAlive = false;
        }
        Log.d("GeoBeagle", "STARTING BCACHING IMPORT");
        mMessageHandler.startBCachingImport();
    }

    private void endImport() throws ImportException, IOException {
        mFileDataVersionWriter.writeVersion();
        mGpxToCache.end();
        if (!mHasFiles
                && mSharedPreferences.getString(BCachingModule.BCACHING_USERNAME, "").length() == 0)
            throw new ImportException(R.string.error_no_gpx_files,
                    mGeoBeagleEnvironment.getImportFolder());
    }

    private void processFile(GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter) throws IOException,
            CancelException {
        IGpxReader gpxReader = gpxFilesAndZipFilesIter.next();
        String filename = gpxReader.getFilename();

        mHasFiles = true;
        mGpxToCache.load(filename, gpxReader.open());
    }

    private GpxFilesAndZipFilesIter startImport() throws ImportException {
        mOldCacheFilesCleaner.clean(mMessageHandler);
        mGpxToCache.start();
        if (mFileDataVersionChecker.needsUpdating()) {
            mDbFrontend.forceUpdate();
            mBCachingStartTime.clearStartTime();
        }
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = mGpxAndZipFiles.iterator();
        return gpxFilesAndZipFilesIter;
    }

    private void tryRun() throws IOException, ImportException, CancelException {
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = startImport();
        while (gpxFilesAndZipFilesIter.hasNext()) {
            processFile(gpxFilesAndZipFilesIter);
        }
        endImport();
    }
}
