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

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.ClearCachesFromSourceImpl;
import com.google.code.geobeagle.database.GpxTableWriterGpxFiles;
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.inject.Inject;

import android.content.SharedPreferences;

class GpxSyncerFactory {

    private final CacheListRefresh cacheListRefresh;
    private final FileDataVersionWriter fileDataVersionWriter;
    private final GpxAndZipFiles gpxAndZipFiles;
    private final GpxToCacheFactory gpxToCacheFactory;
    private final MessageHandler messageHandlerInterface;
    private final OldCacheFilesCleaner oldCacheFilesCleaner;
    private final UpdateFlag updateFlag;
    private final GeoBeagleEnvironment geoBeagleEnvironment;
    private final GpxTableWriterGpxFiles gpxTableWriterGpxFiles;
    private final SharedPreferences sharedPreferences;
    private final ClearCachesFromSourceImpl clearCachesFromSource;

    @Inject
    public GpxSyncerFactory(MessageHandler messageHandler,
            CacheListRefresh cacheListRefresh,
            GpxAndZipFiles gpxAndZipFiles,
            GpxToCacheFactory gpxToCacheFactory,
            FileDataVersionWriter fileDataVersionWriter,
            OldCacheFilesCleaner oldCacheFilesCleaner,
            UpdateFlag updateFlag,
            GeoBeagleEnvironment geoBeagleEnvironment,
            GpxTableWriterGpxFiles gpxTableWriterGpxFiles,
            SharedPreferences sharedPreferences,
            ClearCachesFromSourceImpl clearCachesFromSource) {
        this.messageHandlerInterface = messageHandler;
        this.cacheListRefresh = cacheListRefresh;
        this.gpxAndZipFiles = gpxAndZipFiles;
        this.gpxToCacheFactory = gpxToCacheFactory;
        this.fileDataVersionWriter = fileDataVersionWriter;
        this.oldCacheFilesCleaner = oldCacheFilesCleaner;
        this.updateFlag = updateFlag;
        this.geoBeagleEnvironment = geoBeagleEnvironment;
        this.gpxTableWriterGpxFiles = gpxTableWriterGpxFiles;
        this.sharedPreferences = sharedPreferences;
        this.clearCachesFromSource = clearCachesFromSource;
    }

    public GpxSyncer create() {
        messageHandlerInterface.start(cacheListRefresh);

        final GpxToCache gpxToCache = gpxToCacheFactory.create(messageHandlerInterface,
                gpxTableWriterGpxFiles, clearCachesFromSource);
        return new GpxSyncer(gpxAndZipFiles, fileDataVersionWriter, messageHandlerInterface,
                oldCacheFilesCleaner, gpxToCache, updateFlag, geoBeagleEnvironment,
                sharedPreferences);
    }
}
