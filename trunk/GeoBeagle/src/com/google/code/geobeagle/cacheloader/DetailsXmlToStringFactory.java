package com.google.code.geobeagle.cacheloader;

import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;

class DetailsXmlToStringFactory {
    private final StringWriterWrapper stringWriterWrapper;
    private final Provider<XmlPullParser> xmlPullParserProvider;

    @Inject
    DetailsXmlToStringFactory(StringWriterWrapper stringWriterWrapper,
            Provider<XmlPullParser> xmlPullParserProvider) {
        this.stringWriterWrapper = stringWriterWrapper;
        this.xmlPullParserProvider = xmlPullParserProvider;
    }

    DetailsXmlToString create(EventHelper eventHelper) {
        return new DetailsXmlToString(eventHelper, stringWriterWrapper, xmlPullParserProvider);
    }
}