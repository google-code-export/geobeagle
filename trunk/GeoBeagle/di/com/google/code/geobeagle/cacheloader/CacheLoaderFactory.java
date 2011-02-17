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

package com.google.code.geobeagle.cacheloader;

import com.google.code.geobeagle.cachedetails.DetailsDatabaseReader;
import com.google.code.geobeagle.xmlimport.CacheXmlTagHandler;
import com.google.code.geobeagle.xmlimport.EventDispatcher;
import com.google.code.geobeagle.xmlimport.EventDispatcher.EventDispatcherFactory;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.inject.Inject;

import android.content.res.Resources;

public class CacheLoaderFactory {
    private final DetailsDatabaseReader detailsDatabaseReader;
    private final DetailsXmlToStringFactory detailsXmlToStringFactory;
    private final CacheReaderFromFile cacheReaderFromFile;
    private final Resources resources;
    private final EventDispatcherFactory eventDispatcherFactory;

    @Inject
    public CacheLoaderFactory(DetailsDatabaseReader detailsDatabaseReader,
            DetailsXmlToStringFactory detailsXmlToStringFactory,
            CacheReaderFromFile cacheReaderFromFile,
            Resources resources,
            EventDispatcherFactory eventDispatcherFactory) {
        this.detailsDatabaseReader = detailsDatabaseReader;
        this.detailsXmlToStringFactory = detailsXmlToStringFactory;
        this.cacheReaderFromFile = cacheReaderFromFile;
        this.resources = resources;
        this.eventDispatcherFactory = eventDispatcherFactory;
    }

    public CacheLoader create(CacheXmlTagHandler cacheXmlTagHandler) {
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cacheXmlTagHandler);
        EventDispatcher eventDispatcher = eventDispatcherFactory.create(eventHandlerGpx);
        DetailsXmlToString detailsXmlToString = detailsXmlToStringFactory.create(eventDispatcher);
        return new CacheLoader(cacheReaderFromFile, detailsDatabaseReader, detailsXmlToString,
                resources);
    }
}
