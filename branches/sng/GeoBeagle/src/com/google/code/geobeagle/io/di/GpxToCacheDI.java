
package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.io.CachePersisterFacade;
import com.google.code.geobeagle.io.EventHelper;
import com.google.code.geobeagle.io.GpxToCache;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class GpxToCacheDI {

    public static class XmlPullParserWrapper {
        private String mSource;
        private XmlPullParser mXmlPullParser;

        public String getAttributeValue(String namespace, String name) {
            return mXmlPullParser.getAttributeValue(namespace, name);
        }

        public int getEventType() throws XmlPullParserException {
            return mXmlPullParser.getEventType();
        }

        public String getName() {
            return mXmlPullParser.getName();
        }

        public String getSource() {
            return mSource;
        }

        public String getText() {
            return mXmlPullParser.getText();
        }

        public int next() throws XmlPullParserException, IOException {
            return mXmlPullParser.next();
        }

        public void open(String path) throws XmlPullParserException, FileNotFoundException {
            final XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            final Reader reader = new BufferedReader(new FileReader(path));
            newPullParser.setInput(reader);
            mSource = path;
            mXmlPullParser = newPullParser;
        }

    }

    public static GpxToCache create(Activity activity, CachePersisterFacade cachePersisterFacade) {
        final GpxToCacheDI.XmlPullParserWrapper xmlPullParserWrapper = new GpxToCacheDI.XmlPullParserWrapper();
        final EventHelper eventHelper = EventHelperDI.create(xmlPullParserWrapper,
                cachePersisterFacade);
        return new GpxToCache(xmlPullParserWrapper, eventHelper);
    }

    public static XmlPullParser createPullParser(String path) throws FileNotFoundException,
            XmlPullParserException {
        final XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
        final Reader reader = new BufferedReader(new FileReader(path));
        newPullParser.setInput(reader);
        return newPullParser;
    }

}
