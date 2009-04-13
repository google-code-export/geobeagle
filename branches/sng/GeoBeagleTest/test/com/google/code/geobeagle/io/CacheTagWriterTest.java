
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.data.GeocacheFactory.Source;

import java.text.ParseException;

import junit.framework.TestCase;

public class CacheTagWriterTest extends TestCase {
    private final CacheWriter mCacheWriter = createMock(CacheWriter.class);

    public void testClear() {
        mCacheWriter.insertAndUpdateCache(null, null, 0, 0, Source.GPX, null);

        replay(mCacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.clear();
        cacheTagWriter.write(Source.GPX);
        verify(mCacheWriter);
    }

    public void testEnd() {
        mCacheWriter.clearEarlierLoads();

        replay(mCacheWriter);
        new CacheTagWriter(mCacheWriter).end();
        verify(mCacheWriter);
    }

    public void testGpxTimeDontLoad() {
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);

        replay(mCacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.gpxName("foo.gpx");
        assertFalse(cacheTagWriter.gpxTime("2008-04-15T16:10:30"));
        verify(mCacheWriter);
    }

    public void testGpxTimeLoad() {
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(false);
        mCacheWriter.clearCaches("foo.gpx");

        replay(mCacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.gpxName("foo.gpx");
        assertTrue(cacheTagWriter.gpxTime("2008-04-15T16:10:30"));
        verify(mCacheWriter);
    }

    public void testIsoTimeToSql() throws ParseException {
        assertEquals("2008-04-15 16:10:30", new CacheTagWriter(null)
                .isoTimeToSql("2008-04-15T16:10:30.7369220-08:00"));
    }

    public void testStartWriting() {
        mCacheWriter.startWriting();

        replay(mCacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.startWriting();
        verify(mCacheWriter);
    }

    public void testStopWritingFailure() {
        mCacheWriter.stopWriting();

        replay(mCacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.stopWriting(false);
        verify(mCacheWriter);
    }

    public void testStopWritingSuccess() {
        mCacheWriter.stopWriting();
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);
        mCacheWriter.writeGpx("foo.gpx", "2008-04-15 16:10:30");

        replay(mCacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.gpxName("foo.gpx");
        cacheTagWriter.gpxTime("2008-04-15T16:10:30.7369220-08:00");
        cacheTagWriter.stopWriting(true);
        verify(mCacheWriter);
    }

    public void testWrite() {
        mCacheWriter.insertAndUpdateCache("GC123", "my cache", 122, 37, Source.GPX, "foo.gpx");

        replay(mCacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.id("GC123");
        cacheTagWriter.cacheName("my cache");
        cacheTagWriter.latitudeLongitude("122", "37");
        cacheTagWriter.gpxName("foo.gpx");
        cacheTagWriter.write(Source.GPX);
        verify(mCacheWriter);
    }

    public void testWriteFound() {
        replay(mCacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.symbol("Geocache Found");
        cacheTagWriter.write(Source.GPX);
        verify(mCacheWriter);
    }

}
