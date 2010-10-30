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
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.inject.Inject;

public class CacheLoaderFactory {
    private final FilePathStrategy filePathStrategy;
    private final FileDataVersionChecker fileDataVersionChecker;
    private final EventHelper eventHelper;
    private final DetailsDatabaseReader detailsDatabaseReader;
    private final DetailsReader detailsReader;

    @Inject
    public CacheLoaderFactory(FilePathStrategy filePathStrategy,
            FileDataVersionChecker fileDataVersionChecker,
            EventHelper eventHelper,
            DetailsDatabaseReader detailsDatabaseReader,
            DetailsReader detailsReader) {
        this.filePathStrategy = filePathStrategy;
        this.fileDataVersionChecker = fileDataVersionChecker;
        this.eventHelper = eventHelper;
        this.detailsDatabaseReader = detailsDatabaseReader;
        this.detailsReader = detailsReader;

    }

    public CacheLoader create(EventHandlerGpx eventHandlerGpx) {
        return new CacheLoader(filePathStrategy, eventHandlerGpx, fileDataVersionChecker,
                eventHelper, detailsDatabaseReader, detailsReader);
    }
}