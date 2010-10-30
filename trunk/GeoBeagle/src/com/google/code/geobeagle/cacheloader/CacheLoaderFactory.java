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
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelperFactory;
import com.google.inject.Inject;

import android.content.res.Resources;

public class CacheLoaderFactory {
    private final DetailsDatabaseReader detailsDatabaseReader;
    private final DetailsReader detailsReader;
    private final EventHelperFactory eventHelperFactory;
    private final CacheReaderFromFile cacheReaderFromFile;
    private final Resources resources;

    @Inject
    public CacheLoaderFactory(DetailsDatabaseReader detailsDatabaseReader,
            DetailsReader detailsReader,
            EventHelperFactory eventHelperFactory,
            CacheReaderFromFile cacheReaderFromFile,
            Resources resources) {
        this.detailsDatabaseReader = detailsDatabaseReader;
        this.detailsReader = detailsReader;
        this.eventHelperFactory = eventHelperFactory;
        this.cacheReaderFromFile = cacheReaderFromFile;
        this.resources = resources;
    }

    public CacheLoader create(EventHandlerGpx eventHandlerGpx) {
        return new CacheLoader(eventHelperFactory.create(eventHandlerGpx), detailsDatabaseReader,
                detailsReader, cacheReaderFromFile, resources);
    }
}
