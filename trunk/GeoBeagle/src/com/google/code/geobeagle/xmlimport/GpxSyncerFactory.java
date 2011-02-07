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
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.inject.Inject;

class GpxSyncerFactory {

    private final CacheListRefresh cacheListRefresh;
    private final FileDataVersionWriter fileDataVersionWriter;
    private final GpxAndZipFiles gpxAndZipFiles;
    private final GpxToCacheFactory gpxToCacheFactory;
    private final MessageHandler messageHandlerInterface;
    private final OldCacheFilesCleaner oldCacheFilesCleaner;
    @Inject
    public GpxSyncerFactory(MessageHandler messageHandler,
            CacheListRefresh cacheListRefresh,
            GpxAndZipFiles gpxAndZipFiles,
            GpxToCacheFactory gpxToCacheFactory,
            FileDataVersionWriter fileDataVersionWriter,
            OldCacheFilesCleaner oldCacheFilesCleaner) {
        this.messageHandlerInterface = messageHandler;
        this.cacheListRefresh = cacheListRefresh;
        this.gpxAndZipFiles = gpxAndZipFiles;
        this.gpxToCacheFactory = gpxToCacheFactory;
        this.fileDataVersionWriter = fileDataVersionWriter;
        this.oldCacheFilesCleaner = oldCacheFilesCleaner;
    }

    public GpxSyncer create() {
        messageHandlerInterface.start(cacheListRefresh);

        final GpxToCache gpxToCache = gpxToCacheFactory.create(messageHandlerInterface);
        return new GpxSyncer(gpxAndZipFiles, fileDataVersionWriter, messageHandlerInterface,
                oldCacheFilesCleaner, gpxToCache);
    }
}
