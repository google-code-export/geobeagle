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
import com.google.code.geobeagle.bcaching.MessageHandlerAdapter;
import com.google.code.geobeagle.xmlimport.CacheXmlTagsToSql.CacheXmlTagsToSqlFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class GpxLoaderFactory {

    private final ErrorDisplayer errorDisplayer;
    private final Provider<ImportWakeLock> importWakeLockProvider;
    private final GpxToCacheFactory gpxToCacheFactory;
    private final CacheXmlTagsToSqlFactory cacheXmlTagsToSqlFactory;
    private final MessageHandlerAdapter messageHandlerAdapter;
    private final MessageHandler messageHandler;

    @Inject
    GpxLoaderFactory(Provider<ImportWakeLock> importWakeLockProvider,
            ErrorDisplayer errorDisplayer,
            CacheXmlTagsToSqlFactory cacheXmlTagsToSqlFactory,
            GpxToCacheFactory gpxToCacheFactory,
            MessageHandlerAdapter messageHandlerAdapter,
            MessageHandler messageHandler) {
        this.importWakeLockProvider = importWakeLockProvider;
        this.errorDisplayer = errorDisplayer;
        this.cacheXmlTagsToSqlFactory = cacheXmlTagsToSqlFactory;
        this.gpxToCacheFactory = gpxToCacheFactory;
        this.messageHandlerAdapter = messageHandlerAdapter;
        this.messageHandler = messageHandler;

    }

    public GpxLoader createFileLoader() {
        return create(messageHandler);
    }

    public GpxLoader createBCachingLoader() {
        return create(messageHandlerAdapter);
    }

    private GpxLoader create(MessageHandlerInterface messageHandler) {
        CacheXmlTagsToSql cacheXmlTagsToSql = cacheXmlTagsToSqlFactory.create(messageHandler);
        GpxToCache gpxToCache = gpxToCacheFactory.create(cacheXmlTagsToSql);
        return new GpxLoader(cacheXmlTagsToSql, errorDisplayer, gpxToCache, importWakeLockProvider);
    }
}
