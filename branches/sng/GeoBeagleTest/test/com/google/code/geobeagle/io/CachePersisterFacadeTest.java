
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.GpxLoader.Cache;
import com.google.code.geobeagle.io.HtmlWriter.HtmlWriterFactory;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

import junit.framework.TestCase;

public class CachePersisterFacadeTest extends TestCase {

    public void testEndTag() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        cacheDetailsWriter.writeEndTag();

        replay(cacheDetailsWriter);
        new CachePersisterFacade(cacheDetailsWriter, null, null, null).endTag();
        verify(cacheDetailsWriter);
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
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        cacheDetailsWriter.writeLine("some data");
        replay(cacheDetailsWriter);
        new CachePersisterFacade(cacheDetailsWriter, null, null, null).line("some data");
        verify(cacheDetailsWriter);
    }

    public void testLogDate() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        Cache cache = new Cache();
        cacheDetailsWriter.writeLogDate("04/30/99");

        replay(cacheDetailsWriter);
        new CachePersisterFacade(cacheDetailsWriter, cache, null, null).logDate("04/30/99");
        verify(cacheDetailsWriter);
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
        HtmlWriterFactory htmlWriterFactory = createMock(HtmlWriterFactory.class);
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        CacheDetailsWriter.CacheDetailsWriterFactory cacheDetailsWriterFactory = createMock(CacheDetailsWriter.CacheDetailsWriterFactory.class);
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);

        Cache cache = new Cache();
        cache.mLatitude = 122;
        cache.mLongitude = 37;

        expect(htmlWriterFactory.create(GpxLoader.GEOBEAGLE_DIR + "/GC123.html"))
                .andReturn(htmlWriter);
        expect(cacheDetailsWriterFactory.create(htmlWriter)).andReturn(cacheDetailsWriter);
        cacheDetailsWriter.writeWptName("GC123", 122, 37);

        replay(htmlWriterFactory);
        replay(htmlWriter);
        replay(cacheDetailsWriterFactory);
        replay(cacheDetailsWriter);
        new CachePersisterFacade(cacheDetailsWriter, cache, cacheDetailsWriterFactory, htmlWriterFactory)
                .wptName("GC123");
        verify(htmlWriterFactory);
        verify(htmlWriter);
        verify(cacheDetailsWriterFactory);
        verify(cacheDetailsWriter);
    }
}
