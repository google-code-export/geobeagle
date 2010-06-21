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

import com.google.code.geobeagle.bcaching.BCachingAnnotations.DetailsReaderAnnotation;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImportHelper.BufferedReaderFactory;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.GpxLoader;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.GpxAnnotation;
import com.google.inject.Inject;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.util.Hashtable;

public class DetailsReaderImport {
    private final Hashtable<String, String> params;
    private final BufferedReaderFactory bufferedReaderFactory;
    private final GpxLoader gpxLoader;
    private final EventHelper eventHelper;

    @Inject
    DetailsReaderImport(@DetailsReaderAnnotation Hashtable<String, String> params,
            BufferedReaderFactory bufferedReaderFactory, @GpxAnnotation EventHelper eventHelper,
            GpxLoader gpxLoader) {
        this.params = params;
        this.bufferedReaderFactory = bufferedReaderFactory;
        this.gpxLoader = gpxLoader;
        this.eventHelper = eventHelper;
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
        return gpxLoader.load(eventHelper);
    }

    public String getLastModified() {
        return gpxLoader.getLastModified();
    }
}
