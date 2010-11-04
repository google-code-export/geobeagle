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
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class GpxLoaderFactory {

    private final ImportCacheActionsFromFile importCacheActionsFromFile;
    private final ErrorDisplayer errorDisplayer;
    private final Provider<ImportWakeLock> importWakeLockProvider;
    private final ImportCacheActionsFromBCaching importCacheActionsFromBCaching;
    private final GpxToCacheFactory gpxToCacheFactory;

    @Inject
    GpxLoaderFactory(Provider<ImportWakeLock> importWakeLockProvider,
            ErrorDisplayer errorDisplayer,
            ImportCacheActionsFromFile importCacheActionsFromFile,
            ImportCacheActionsFromBCaching importCacheActionsFromBCaching,
            GpxToCacheFactory gpxToCacheFactory) {
        this.importWakeLockProvider = importWakeLockProvider;
        this.errorDisplayer = errorDisplayer;
        this.importCacheActionsFromFile = importCacheActionsFromFile;
        this.importCacheActionsFromBCaching = importCacheActionsFromBCaching;
        this.gpxToCacheFactory = gpxToCacheFactory;
    }

    public GpxLoader createFileLoader() {
        return create(importCacheActionsFromFile);
    }

    public GpxLoader createBCachingLoader() {
        return create(importCacheActionsFromBCaching);
    }

    private GpxLoader create(CacheXmlTagsToSql importCacheActions) {
        GpxToCache gpxToCache = gpxToCacheFactory.create();
        return new GpxLoader(importCacheActions, errorDisplayer, gpxToCache, importWakeLockProvider);
    }
}
