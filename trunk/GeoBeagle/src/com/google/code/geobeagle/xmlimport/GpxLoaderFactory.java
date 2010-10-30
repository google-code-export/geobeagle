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
import com.google.inject.Provider;

public class GpxLoaderFactory {

    private final ImportCacheActionsFromFile importCacheActionsFromFile;
    private final ErrorDisplayer errorDisplayer;
    private final GpxToCache gpxToCache;
    private final Provider<ImportWakeLock> importWakeLockProvider;
    private final ImportCacheActionsFromBCaching importCacheActionsFromBCaching;

    GpxLoaderFactory(GpxToCache gpxToCache,
            Provider<ImportWakeLock> importWakeLockProvider,
            ErrorDisplayer errorDisplayer,
            ImportCacheActionsFromFile importCacheActionsFromFile,
            ImportCacheActionsFromBCaching importCacheActionsFromBCaching) {
        this.importWakeLockProvider = importWakeLockProvider;
        this.errorDisplayer = errorDisplayer;
        this.gpxToCache = gpxToCache;
        this.importCacheActionsFromFile = importCacheActionsFromFile;
        this.importCacheActionsFromBCaching = importCacheActionsFromBCaching;
    }

    public GpxLoader createFileLoader() {
        return new GpxLoader(importCacheActionsFromFile, errorDisplayer, gpxToCache,
                importWakeLockProvider);
    }

    public GpxLoader createBCachingLoader() {
        return new GpxLoader(importCacheActionsFromBCaching, errorDisplayer, gpxToCache,
                importWakeLockProvider);
    }
}
