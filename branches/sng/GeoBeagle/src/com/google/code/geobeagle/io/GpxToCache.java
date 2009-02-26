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
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class GpxToCache {
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

    public static XmlPullParser createPullParser(String path) throws FileNotFoundException,
            XmlPullParserException {
        final Reader reader = new BufferedReader(new FileReader(path));
        final XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
        newPullParser.setInput(reader);
        return newPullParser;
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

    public static GpxToCache create(String path) throws FileNotFoundException, XmlPullParserException {
        final XmlPullParser xmlPullParser = createPullParser(path);
        final CacheDetailsWriterFactory cacheDetailsWriterFactory = new CacheDetailsWriterFactory();
        final Cache cache = new Cache();
        final XmlPathBuilder xmlPathBuilder = new XmlPathBuilder();
        final GpxEventHandler gpxEventHandler = new GpxEventHandler(cacheDetailsWriterFactory,
                cache, null);
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

    public static GpxCaches createGpxCaches(ErrorDisplayer errorDisplayer, String path)
            throws XmlPullParserException, IOException, FileNotFoundException {
        final GpxToCache gpxToCache = GpxToCache.create(path);
        return new GpxCaches(gpxToCache, path, errorDisplayer);
    }
}
