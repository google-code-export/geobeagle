
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;
import com.google.code.geobeagle.io.LoadGpx.Cache;
import com.google.code.geobeagle.io.LoadGpx.CacheFilter;
import com.google.code.geobeagle.io.LoadGpx.GpxToCache;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import junit.framework.TestCase;

public class LoadGpxTest extends TestCase {

    private XmlPullParser xmlPullParser = createMock(XmlPullParser.class);

    private void endTagAndDocument(XmlPullParser xmlPullParser) throws XmlPullParserException,
            IOException {
        expectTag(XmlPullParser.END_TAG, "wpt");
        expect(xmlPullParser.next()).andReturn(XmlPullParser.END_TAG);
    }

    private void expectTag(int eventType, String name) throws XmlPullParserException, IOException {
        expect(xmlPullParser.next()).andReturn(eventType);
        expect(xmlPullParser.getName()).andReturn(name);
    }

    private void expectText(String text) throws XmlPullParserException, IOException {
        expect(xmlPullParser.next()).andReturn(XmlPullParser.TEXT);
        expect(xmlPullParser.getText()).andReturn(text);
    }

    private void startTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_TAG);
        expect(xmlPullParser.getName()).andReturn("gpx");
        expectTag(XmlPullParser.START_TAG, "wpt");
        expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("123");
        expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("37");
    }

    public void testId() throws XmlPullParserException, IOException {
        startTag(xmlPullParser);

        expectTag(XmlPullParser.START_TAG, "name");
        expectText("GC123");
        expectTag(XmlPullParser.END_TAG, "name");

        endTagAndDocument(xmlPullParser);

        replay(xmlPullParser);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser);
        gpxToCache.startLoad();
        Cache cache = gpxToCache.load();
        assertEquals("GC123", cache.mId);
        verify(xmlPullParser);
    }

    public void testLoad() throws XmlPullParserException, IOException {
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        CacheWriter cacheWriter = createMock(CacheWriter.class);
        CacheFilter cacheFilter = createMock(CacheFilter.class);

        cacheWriter.clear();
        gpxToCache.startLoad();
        Cache cache = new Cache();
        cache.mId = "gc1234";
        cache.mName = "my cache";
        cache.mLatitude = 122;
        cache.mLongitude = 37;
        expect(gpxToCache.load()).andReturn(cache);
        expect(cacheFilter.filter(cache)).andReturn(true);
        expect(cacheWriter.write("gc1234", "my cache", 122, 37)).andReturn(true);
        expect(gpxToCache.load()).andReturn(null);

        replay(gpxToCache);
        replay(cacheFilter);
        replay(cacheWriter);
        LoadGpx loadGpx = new LoadGpx(cacheWriter, gpxToCache);
        loadGpx.load(cacheFilter);
        verify(cacheWriter);
        verify(cacheFilter);
        verify(gpxToCache);
    }

    public void testLoadHasError() throws XmlPullParserException, IOException {
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        CacheWriter cacheWriter = createMock(CacheWriter.class);
        CacheFilter cacheFilter = createMock(CacheFilter.class);

        cacheWriter.clear();
        gpxToCache.startLoad();
        Cache cache = new Cache();
        cache.mId = "gc1234";
        cache.mName = "my cache";
        cache.mLatitude = 122;
        cache.mLongitude = 37;
        expect(gpxToCache.load()).andReturn(cache);
        expect(cacheFilter.filter(cache)).andReturn(true);
        expect(cacheWriter.write("gc1234", "my cache", 122, 37)).andReturn(false);

        replay(gpxToCache);
        replay(cacheFilter);
        replay(cacheWriter);
        LoadGpx loadGpx = new LoadGpx(cacheWriter, gpxToCache);
        loadGpx.load(cacheFilter);
        verify(cacheWriter);
        verify(cacheFilter);
        verify(gpxToCache);
    }
    public void testName() throws XmlPullParserException, IOException {
        startTag(xmlPullParser);

        expectTag(XmlPullParser.START_TAG, "groundspeak:cache");
        expectTag(XmlPullParser.START_TAG, "groundspeak:name");
        expectText("a fun little cache");
        expectTag(XmlPullParser.END_TAG, "groundspeak:name");
        expectTag(XmlPullParser.END_TAG, "groundspeak:cache");

        endTagAndDocument(xmlPullParser);

        replay(xmlPullParser);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser);
        gpxToCache.startLoad();
        Cache cache = gpxToCache.load();
        assertEquals("a fun little cache", cache.mName);
        verify(xmlPullParser);
    }

    public void testNameNotTB() throws XmlPullParserException, IOException {
        startTag(xmlPullParser);

        expectTag(XmlPullParser.START_TAG, "groundspeak:cache");
        expectTag(XmlPullParser.START_TAG, "groundspeak:name");
        expectText("a fun little cache");
        expectTag(XmlPullParser.END_TAG, "groundspeak:name");
        expectTag(XmlPullParser.END_TAG, "groundspeak:cache");

        expectTag(XmlPullParser.START_TAG, "groundspeak:travelbug");
        expectTag(XmlPullParser.START_TAG, "groundspeak:name");
        // expect(xmlPullParser.next()).andReturn(XmlPullParser.TEXT);
        expectTag(XmlPullParser.END_TAG, "groundspeak:name");
        expectTag(XmlPullParser.END_TAG, "groundspeak:travelbug");

        endTagAndDocument(xmlPullParser);

        replay(xmlPullParser);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser);
        gpxToCache.startLoad();
        Cache cache = gpxToCache.load();
        assertEquals("a fun little cache", cache.mName);
        verify(xmlPullParser);
    }

    public void testWpt() throws XmlPullParserException, IOException {
        startTag(xmlPullParser);
        endTagAndDocument(xmlPullParser);

        replay(xmlPullParser);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser);
        gpxToCache.startLoad();
        Cache cache = gpxToCache.load();
        assertEquals(123.0, cache.mLatitude);
        assertEquals(37.0, cache.mLongitude);
        verify(xmlPullParser);
    }
}
