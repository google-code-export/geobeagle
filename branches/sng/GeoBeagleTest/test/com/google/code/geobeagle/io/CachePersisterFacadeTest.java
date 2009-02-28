
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.CacheDetailsWriter.CacheDetailsWriterFactory;
import com.google.code.geobeagle.io.GpxLoader.Cache;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

import junit.framework.TestCase;

public class CachePersisterFacadeTest extends TestCase {

    public void testEndTag() throws IOException {
        GpxWriter gpxWriter = createMock(GpxWriter.class);
        gpxWriter.writeEndTag();

        replay(gpxWriter);
        new CachePersisterFacade(gpxWriter, null, null, null).endTag();
        verify(gpxWriter);
    }

    public void testEndTagNotCache() throws IOException {
        GpxEventHandler gpxEventHandler = new GpxEventHandler(null);
        assertNull(gpxEventHandler.endTag("/gpx/wptNOT!"));
    }

    public void testGroundspeakName() throws IOException {
        Cache cache = createMock(Cache.class);
        new CachePersisterFacade(null, cache, null, null).groundspeakName("GC12");
        assertEquals("GC12", cache.mName);
    }

    public void testLine() throws IOException {
        GpxWriter gpxWriter = createMock(GpxWriter.class);
        gpxWriter.writeLine("some data");
        replay(gpxWriter);
        new CachePersisterFacade(gpxWriter, null, null, null).line("some data");
        verify(gpxWriter);
    }

    public void testLogDate() throws IOException {
        GpxWriter gpxWriter = createMock(GpxWriter.class);
        Cache cache = new Cache();
        gpxWriter.writeLogDate("04/30/99");

        replay(gpxWriter);
        new CachePersisterFacade(gpxWriter, cache, null, null).logDate("04/30/99");
        verify(gpxWriter);
    }

    public void testWpt() throws IOException {
        XmlPullParser xmlPullParser = createMock(XmlPullParser.class);
        Cache cache = new Cache();
        expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37");
        expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("122");

        replay(xmlPullParser);
        new CachePersisterFacade(null, cache, null, null).wpt(xmlPullParser);
        verify(xmlPullParser);
    }

    public void testWptName() throws IOException {
        CacheDetailsWriterFactory cacheDetailsWriterFactory = createMock(CacheDetailsWriterFactory.class);
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        GpxWriter.GpxWriterFactory gpxWriterFactory = createMock(GpxWriter.GpxWriterFactory.class);
        GpxWriter gpxWriter = createMock(GpxWriter.class);

        Cache cache = new Cache();
        cache.mLatitude = 122;
        cache.mLongitude = 37;

        expect(cacheDetailsWriterFactory.create(GpxToCache.GEOBEAGLE_DIR + "/GC123.html"))
                .andReturn(cacheDetailsWriter);
        expect(gpxWriterFactory.create(cacheDetailsWriter)).andReturn(gpxWriter);
        gpxWriter.writeWptName("GC123", 122, 37);

        replay(cacheDetailsWriterFactory);
        replay(cacheDetailsWriter);
        replay(gpxWriterFactory);
        replay(gpxWriter);
        new CachePersisterFacade(gpxWriter, cache, gpxWriterFactory, cacheDetailsWriterFactory)
                .wptName("GC123");
        verify(cacheDetailsWriterFactory);
        verify(cacheDetailsWriter);
        verify(gpxWriterFactory);
        verify(gpxWriter);
    }
}
