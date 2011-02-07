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

import com.google.code.geobeagle.R;
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

import java.io.IOException;

public class GpxSyncer {

    private final BCachingStartTime bcachingStartTime;
    private final DbFrontend dbFrontend;
    private final FileDataVersionChecker fileDataVersionChecker;
    private final FileDataVersionWriter fileDataVersionWriter;
    private final GeoBeagleEnvironment geoBeagleEnvironment;
    private final GpxAndZipFiles gpxAndZipFiles;
    private final GpxToCache gpxToCache;
    private boolean mHasFiles;
    private final MessageHandler messageHandler;
    private final OldCacheFilesCleaner oldCacheFilesCleaner;
    private final SharedPreferences sharedPreferences;

    public GpxSyncer(GpxAndZipFiles gpxAndZipFiles,
            FileDataVersionWriter fileDataVersionWriter,
            DbFrontend dbFrontend,
            FileDataVersionChecker fileDataVersionChecker,
            BCachingStartTime bcachingStartTime,
            MessageHandler messageHandlerInterface,
            OldCacheFilesCleaner oldCacheFilesCleaner,
            SharedPreferences sharedPreferences,
            GpxToCache gpxToCache,
            GeoBeagleEnvironment geoBeagleEnvironment) {
        this.gpxAndZipFiles = gpxAndZipFiles;
        this.fileDataVersionWriter = fileDataVersionWriter;
        this.fileDataVersionChecker = fileDataVersionChecker;
        this.bcachingStartTime = bcachingStartTime;
        this.dbFrontend = dbFrontend;
        this.messageHandler = messageHandlerInterface;
        this.mHasFiles = false;
        this.oldCacheFilesCleaner = oldCacheFilesCleaner;
        this.sharedPreferences = sharedPreferences;
        this.gpxToCache = gpxToCache;
        this.geoBeagleEnvironment = geoBeagleEnvironment;
    }

    public void sync() throws IOException, ImportException, CancelException {
        try {
            tryRun();
        } finally {
            Log.d("GeoBeagle", "<<< Syncing");
            messageHandler.loadComplete();
        }
    }

    private void endImport() throws ImportException, IOException {
        fileDataVersionWriter.writeVersion();
        gpxToCache.end();
        if (!mHasFiles
                && sharedPreferences.getString(BCachingModule.BCACHING_USERNAME, "").length() == 0)
            throw new ImportException(R.string.error_no_gpx_files,
                    geoBeagleEnvironment.getImportFolder());
    }

    private void processFile(GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter) throws IOException,
            CancelException {
        IGpxReader gpxReader = gpxFilesAndZipFilesIter.next();
        String filename = gpxReader.getFilename();

        mHasFiles = true;
        gpxToCache.load(filename, gpxReader.open());
    }

    private GpxFilesAndZipFilesIter startImport() throws ImportException {
        oldCacheFilesCleaner.clean(messageHandler);
        gpxToCache.start();
        if (fileDataVersionChecker.needsUpdating()) {
            dbFrontend.forceUpdate();
            bcachingStartTime.clearStartTime();
        }
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = gpxAndZipFiles.iterator();
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
