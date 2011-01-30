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
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.inject.Inject;

import android.content.SharedPreferences;

class ImportThreadFactory {

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
        return new ImportThread(gpxAndZipFiles, errorDisplayer, fileDataVersionWriter, dbFrontend,
                fileDataVersionChecker, bcachingStartTime, updateFlag, messageHandlerInterface,
                oldCacheFilesCleaner, sharedPreferences, gpxToCache, geoBeagleEnvironment);
    }
}
