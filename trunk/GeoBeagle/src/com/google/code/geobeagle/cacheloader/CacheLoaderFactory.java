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
import com.google.code.geobeagle.xmlimport.CacheTagHandler;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;
import com.google.inject.Inject;

import android.content.res.Resources;

public class CacheLoaderFactory {
    private final DetailsDatabaseReader detailsDatabaseReader;
    private final DetailsXmlToString detailsXmlToString;
    private final CacheReaderFromFile cacheReaderFromFile;
    private final Resources resources;
    private final XmlPathBuilder xmlPathBuilder;

    @Inject
    public CacheLoaderFactory(DetailsDatabaseReader detailsDatabaseReader,
            DetailsXmlToString detailsXmlToString,
            CacheReaderFromFile cacheReaderFromFile,
            Resources resources,
            XmlPathBuilder xmlPathBuilder) {
        this.detailsDatabaseReader = detailsDatabaseReader;
        this.detailsXmlToString = detailsXmlToString;
        this.cacheReaderFromFile = cacheReaderFromFile;
        this.resources = resources;
        this.xmlPathBuilder = xmlPathBuilder;
    }

    public CacheLoader create(CacheTagHandler cacheTagHandler) {
        return new CacheLoader(
                new EventHelper(xmlPathBuilder, new EventHandlerGpx(cacheTagHandler)),
                detailsDatabaseReader, detailsXmlToString, cacheReaderFromFile, resources);
    }
}
