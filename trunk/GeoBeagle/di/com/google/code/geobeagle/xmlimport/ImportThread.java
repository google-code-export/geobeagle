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
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxFileIterAndZipFileIterFactory;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipInputFileTester;
import com.google.inject.Injector;
import com.google.inject.Provider;

import roboguice.util.RoboThread;

import android.content.SharedPreferences;

public class ImportThread extends RoboThread {
    static ImportThread create(MessageHandlerInterface messageHandlerInterface,
            ErrorDisplayer errorDisplayer,
            Injector injector) {
        final GpxAndZipFilenameFilter filenameFilter = injector
                .getInstance(GpxAndZipFilenameFilter.class);
        final ZipInputFileTester zipInputFileTester = injector
                .getInstance(ZipInputFileTester.class);
        final GeoBeagleEnvironment geoBeagleEnvironment = injector
                .getInstance(GeoBeagleEnvironment.class);
        Provider<Aborter> aborterProvider = injector.getProvider(Aborter.class);
        final GpxFileIterAndZipFileIterFactory gpxFileIterAndZipFileIterFactory = new GpxFileIterAndZipFileIterFactory(
                zipInputFileTester, aborterProvider, geoBeagleEnvironment);
        final SharedPreferences sharedPreferences = injector
                .getInstance(SharedPreferences.class);
        final GpxAndZipFiles gpxAndZipFiles = new GpxAndZipFiles(filenameFilter,
                gpxFileIterAndZipFileIterFactory, geoBeagleEnvironment, sharedPreferences);
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