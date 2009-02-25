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
import com.google.code.geobeagle.io.LoadGpx.Cache;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class GpxToCache {
    private static final String GPX_PATH = "/sdcard/caches.gpx";

    public static class EventHelper {
        private final GpxEventHandler mGpxEventHandler;
        private final XmlPathBuilder mXmlPathBuilder;
        private final XmlPullParser mXmlPullParser;

        public EventHelper(XmlPathBuilder xmlPathBuilder, GpxEventHandler gpxEventHandler,
                XmlPullParser xmlPullParser) {
            mXmlPathBuilder = xmlPathBuilder;
            mGpxEventHandler = gpxEventHandler;
            mXmlPullParser = xmlPullParser;
        }

        public Cache handleEvent(int eventType) throws IOException {
            Cache cache = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    mXmlPathBuilder.startTag(mXmlPullParser.getName());
                    mGpxEventHandler.startTag(mXmlPathBuilder.getPath(), mXmlPullParser);
                    break;
                case XmlPullParser.END_TAG:
                    cache = mGpxEventHandler.endTag(mXmlPathBuilder.getPath());
                    mXmlPathBuilder.endTag(mXmlPullParser.getName());
                    break;
                case XmlPullParser.TEXT:
                    mGpxEventHandler.text(mXmlPathBuilder.getPath(), mXmlPullParser.getText());
                    break;
            }
            return cache;
        }
    }

    public static XmlPullParser createPullParser() throws FileNotFoundException,
            XmlPullParserException {
        final FileReader fileReader = new FileReader(GPX_PATH);
        final XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
        newPullParser.setInput(fileReader);
        return newPullParser;
    }

    public static class GpxCaches implements Iterable<Cache> {
        public class CacheIterator implements Iterator<Cache> {
            // TODO: hasNext has a side effect, and next does not, which is
            // backwards.
            public boolean hasNext() {
                try {
                    mCache = mGpxToCache.load();
                    return mCache != null;
                } catch (XmlPullParserException e) {
                    mErrorDisplayer.displayErrorAndStack(e);
                    return false;
                } catch (IOException e) {
                    mErrorDisplayer.displayErrorAndStack(e);
                    return false;
                }
            }

            public Cache next() {
                return mCache;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        private Cache mCache;
        private final ErrorDisplayer mErrorDisplayer;
        private final GpxToCache mGpxToCache;

        public GpxCaches(GpxToCache gpxToCache, ErrorDisplayer errorDisplayer)
                throws XmlPullParserException, IOException {
            mGpxToCache = gpxToCache;
            mErrorDisplayer = errorDisplayer;
        }

        public Iterator<Cache> iterator() {
            return new CacheIterator();
        }

    }

    public static class XmlPathBuilder {
        private String mPath = "";

        public void endTag(String currentTag) {
            mPath = mPath.substring(0, mPath.length() - (currentTag.length() + 1));
        }

        public String getPath() {
            return mPath;
        }

        public void startTag(String mCurrentTag) {
            mPath += "/" + mCurrentTag;
        }
    }

    public static final String GEOBEAGLE_DIR = "/sdcard/GeoBeagle";

    private final EventHelper mEventHelper;
    private final XmlPullParser mXmlPullParser;

    public static GpxToCache create() throws FileNotFoundException, XmlPullParserException {
        final XmlPullParser xmlPullParser = createPullParser();
        final CacheDetailsWriterFactory cacheDetailsWriterFactory = new CacheDetailsWriterFactory();
        final Cache cache = new Cache();
        final XmlPathBuilder xmlPathBuilder = new XmlPathBuilder();
        final GpxEventHandler gpxEventHandler = new GpxEventHandler(cacheDetailsWriterFactory,
                cache);
        final EventHelper eventHelper = new EventHelper(xmlPathBuilder, gpxEventHandler,
                xmlPullParser);

        return new GpxToCache(xmlPullParser, eventHelper);
    }

    public GpxToCache(XmlPullParser xmlPullParser, EventHelper eventHelper) {
        mXmlPullParser = xmlPullParser;
        mEventHelper = eventHelper;
    }

    public Cache load() throws XmlPullParserException, IOException {
        Cache cache = null;
        for (int eventType = mXmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT
                && cache == null; eventType = mXmlPullParser.next()) {
            cache = mEventHelper.handleEvent(eventType);
        }
        return cache;
    }

    public static GpxCaches createGpxCaches(ErrorDisplayer errorDisplayer)
            throws XmlPullParserException, IOException {
        final GpxToCache gpxToCache = GpxToCache.create();
        return new GpxCaches(gpxToCache, errorDisplayer);
    }
}
