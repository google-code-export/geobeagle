/**
 * 
 */

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.io.CacheDetailsWriter.CacheDetailsWriterFactory;
import com.google.code.geobeagle.io.GpxLoader.Cache;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

public class CachePersisterFacade {
    public static class GpxWriter {
        final CacheDetailsWriter mCacheDetailsWriter;

        public GpxWriter(CacheDetailsWriter cacheDetailsWriter) {
            mCacheDetailsWriter = cacheDetailsWriter;
        }

        void writeEndTag() throws IOException {
            mCacheDetailsWriter.writeFooter();
            mCacheDetailsWriter.close();
        }

        void writeLine(String text) throws IOException {
            mCacheDetailsWriter.write(text);
        }

        void writeLogDate(Cache cache, String text) throws IOException {
            mCacheDetailsWriter.writeSeparator();
            mCacheDetailsWriter.write(text);
        }

        void writeWptName(String text, double latitude, double longitude) throws IOException {
            mCacheDetailsWriter.writeHeader();
            mCacheDetailsWriter.write(text);
            mCacheDetailsWriter.write(latitude + ", " + longitude);
        }

    }

    public static class GpxWriterFactory {
        public GpxWriter create(CacheDetailsWriter cacheDetailsWriter) {
            return new GpxWriter(cacheDetailsWriter);
        }
    }

    public static CachePersisterFacade create() {
        final CacheDetailsWriterFactory cacheDetailsWriterFactory = new CacheDetailsWriterFactory();
        final Cache cache = new Cache();
        final GpxWriterFactory gpxWriterFactory = new GpxWriterFactory();
        return new CachePersisterFacade(null, cache, gpxWriterFactory, cacheDetailsWriterFactory);
    }

    private final Cache mCache;
    private final CacheDetailsWriterFactory mCacheDetailsWriterFactory;
    private CachePersisterFacade.GpxWriter mGpxWriter;

    private final CachePersisterFacade.GpxWriterFactory mGpxWriterFactory;

    public CachePersisterFacade(CachePersisterFacade.GpxWriter gpxWriter, Cache cache,
            CachePersisterFacade.GpxWriterFactory gpxWriterFactory,
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
        mGpxWriter.writeLogDate(mCache, text);
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
