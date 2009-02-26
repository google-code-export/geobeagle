
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.GpxLoader.Cache;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

import junit.framework.TestCase;

public class GpxEventHandlerTest extends TestCase {
    public void testEndTagNotCache() throws IOException {
        GpxEventHandler gpxEventHandler = new GpxEventHandler(null, null, null);
        gpxEventHandler.endTag("/gpx/wptNOT!");
    }

    public void testEndTagCache() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);

        cacheDetailsWriter.writeFooter();
        cacheDetailsWriter.close();

        replay(cacheDetailsWriter);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(null, null, cacheDetailsWriter);
        gpxEventHandler.endTag("/gpx/wpt");
        verify(cacheDetailsWriter);
    }

    public void testStartTagNotCache() throws IOException {
        Cache cache = new Cache();
        GpxEventHandler gpxEventHandler = new GpxEventHandler(null, cache, null);
        gpxEventHandler.startTag("/gpx/wptNot", null);
    }

    public void testStartTagCache() throws IOException {
        XmlPullParser xmlPullParser = createMock(XmlPullParser.class);
        expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37");
        expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("122");

        replay(xmlPullParser);
        Cache cache = new Cache();
        GpxEventHandler gpxEventHandler = new GpxEventHandler(null, cache, null);
        gpxEventHandler.startTag("/gpx/wpt", xmlPullParser);
        assertEquals(37.0, cache.mLatitude);
        assertEquals(122.0, cache.mLongitude);
        verify(xmlPullParser);
    }
}
