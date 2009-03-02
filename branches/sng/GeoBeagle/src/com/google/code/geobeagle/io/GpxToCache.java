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

import com.google.code.geobeagle.io.GpxLoader.Cache;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class GpxToCache {
    public static GpxToCache create() {
        final XmlPullParserWrapper xmlPullParserWrapper = new XmlPullParserWrapper();
        final EventHelper eventHelper = EventHelper.create(xmlPullParserWrapper);
        return new GpxToCache(xmlPullParserWrapper, eventHelper);
    }

    public static class XmlPullParserWrapper {
        private XmlPullParser mXmlPullParser;

        public void open(String path) throws XmlPullParserException, FileNotFoundException {
            final XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            final Reader reader = new BufferedReader(new FileReader(path));
            newPullParser.setInput(reader);
            mXmlPullParser = newPullParser;
        }

        public int getEventType() throws XmlPullParserException {
            return mXmlPullParser.getEventType();
        }

        public int next() throws XmlPullParserException, IOException {
            return mXmlPullParser.next();
        }

        public String getName() {
            return mXmlPullParser.getName();
        }

        public String getAttributeValue(String namespace, String name) {
            return mXmlPullParser.getAttributeValue(namespace, name);
        }

        public String getText() {
            return mXmlPullParser.getText();
        }

    }

    public static XmlPullParser createPullParser(String path) throws FileNotFoundException,
            XmlPullParserException {
        final XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
        final Reader reader = new BufferedReader(new FileReader(path));
        newPullParser.setInput(reader);
        return newPullParser;
    }

    private final EventHelper mEventHelper;
    private final XmlPullParserWrapper mXmlPullParser;

    public GpxToCache(XmlPullParserWrapper xmlPullParser, EventHelper eventHelper) {
        mXmlPullParser = xmlPullParser;
        mEventHelper = eventHelper;
    }

    public void open(String path) throws FileNotFoundException, XmlPullParserException {
        mXmlPullParser.open(path);
    }

    public Cache load() throws XmlPullParserException, IOException {
        Cache cache = null;
        for (int eventType = mXmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT
                && cache == null; eventType = mXmlPullParser.next()) {
            cache = mEventHelper.handleEvent(eventType);
        }
        return cache;
    }
}
