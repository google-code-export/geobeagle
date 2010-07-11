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

package com.google.code.geobeagle.bcaching;

import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImporterStateless;
import com.google.code.geobeagle.xmlimport.EventHandler;
import com.google.code.geobeagle.xmlimport.EventHandlerComposite;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.GpxLoader;
import com.google.code.geobeagle.xmlimport.XmlWriter;
import com.google.inject.Inject;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Hashtable;

public class DetailsReaderImport {
    private static Hashtable<String, String> params;
    static {
        params = new Hashtable<String, String>();
        params.put("a", "detail");
        BCachingListImporterStateless.commonParams(params);
        params.put("desc", "html");
        params.put("tbs", "0");
        params.put("wpts", "1");
        params.put("logs", "1");
        params.put("fmt", "gpx");
    }

    private final BufferedReaderFactoryImpl bufferedReaderFactory;
    private final GpxLoader gpxLoader;
    private final EventHelper eventHelper;
    private final EventHandler eventHandler;

    @Inject
    DetailsReaderImport(BufferedReaderFactoryImpl bufferedReaderFactory, EventHelper eventHelper,
            GpxLoader gpxLoader, XmlWriter xmlWriter, EventHandlerGpx eventHandlerGpx) {
        this.bufferedReaderFactory = bufferedReaderFactory;
        this.gpxLoader = gpxLoader;
        this.eventHelper = eventHelper;
        this.eventHandler = new EventHandlerComposite(Arrays.asList(xmlWriter, eventHandlerGpx));
    }

    public boolean loadCacheDetails(String csvIds) throws BCachingException {
        params.put("ids", csvIds);

        try {
            BufferedReader bufferedReader = bufferedReaderFactory.create(params);
            gpxLoader.open("BCaching.com", bufferedReader);
        } catch (XmlPullParserException e) {
            throw new BCachingException("Error parsing data from baching.com: "
                    + e.getLocalizedMessage());
        }
        return gpxLoader.load(eventHelper, eventHandler);
    }

    public String getLastModified() {
        return gpxLoader.getLastModified();
    }
}
