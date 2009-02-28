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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.io.CacheDetailsWriter.CacheDetailsWriterFactory;
import com.google.code.geobeagle.io.GpxLoader.Cache;
import com.google.code.geobeagle.io.GpxWriter.GpxWriterFactory;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

public class CachePersisterFacade {
    public static CachePersisterFacade create() {
        final CacheDetailsWriterFactory cacheDetailsWriterFactory = new CacheDetailsWriterFactory();
        final Cache cache = new Cache();
        final GpxWriterFactory gpxWriterFactory = new GpxWriterFactory();
        return new CachePersisterFacade(null, cache, gpxWriterFactory, cacheDetailsWriterFactory);
    }

    private final Cache mCache;
    private final CacheDetailsWriterFactory mCacheDetailsWriterFactory;
    private GpxWriter mGpxWriter;

    private final GpxWriterFactory mGpxWriterFactory;

    public CachePersisterFacade(GpxWriter gpxWriter, Cache cache,
            GpxWriterFactory gpxWriterFactory,
            CacheDetailsWriterFactory cacheDetailsFactory) {
        mGpxWriterFactory = gpxWriterFactory;
        mGpxWriter = gpxWriter;
        mCache = cache;
        mCacheDetailsWriterFactory = cacheDetailsFactory;
    }

    Cache endTag() throws IOException {
        mGpxWriter.writeEndTag();
        return mCache;
    }

    void groundspeakName(String text) {
        mCache.mName = text;
    }

    void line(String text) throws IOException {
        mGpxWriter.writeLine(text);
    }

    void logDate(String text) throws IOException {
        mGpxWriter.writeLogDate(text);
    }

    void wpt(XmlPullParser mXmlPullParser) {
        mCache.mLatitude = Double.parseDouble(mXmlPullParser.getAttributeValue(null, "lat"));
        mCache.mLongitude = Double.parseDouble(mXmlPullParser.getAttributeValue(null, "lon"));
    }

    void wptName(String text) throws IOException {
        CacheDetailsWriter cacheDetailsWriter = mCacheDetailsWriterFactory
                .create(GpxToCache.GEOBEAGLE_DIR + "/" + text + ".html");
        mGpxWriter = mGpxWriterFactory.create(cacheDetailsWriter);
        mGpxWriter.writeWptName(text, mCache.mLatitude, mCache.mLongitude);
        mCache.mId = text;
    }
}
