
package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.CacheWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;

@RunWith(PowerMockRunner.class)
public class CacheTagWriterTest {
    private final CacheWriter mCacheWriter = PowerMock.createMock(CacheWriter.class);

    @Test
    public void testClear() {
        mCacheWriter.insertAndUpdateCache(null, null, 0, 0, Source.GPX, null);

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.clear();
        cacheTagWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testEnd() {
        mCacheWriter.clearEarlierLoads();

        PowerMock.replayAll();
        new CacheTagWriter(mCacheWriter).end();
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTimeDontLoad() {
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.gpxName("foo.gpx");
        assertFalse(cacheTagWriter.gpxTime("2008-04-15T16:10:30"));
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTimeLoad() {
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(false);
        mCacheWriter.clearCaches("foo.gpx");

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.gpxName("foo.gpx");
        assertTrue(cacheTagWriter.gpxTime("2008-04-15T16:10:30"));
        PowerMock.verifyAll();
    }

    @Test
    public void testIsoTimeToSql() throws ParseException {
        assertEquals("2008-04-15 16:10:30", new CacheTagWriter(null)
                .isoTimeToSql("2008-04-15T16:10:30.7369220-08:00"));
    }

    @Test
    public void testStartWriting() {
        mCacheWriter.startWriting();

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.startWriting();
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWritingFailure() {
        mCacheWriter.stopWriting();

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.stopWriting(false);
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWritingSuccess() {
        mCacheWriter.stopWriting();
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);
        mCacheWriter.writeGpx("foo.gpx", "2008-04-15 16:10:30");

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.gpxName("foo.gpx");
        cacheTagWriter.gpxTime("2008-04-15T16:10:30.7369220-08:00");
        cacheTagWriter.stopWriting(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testWrite() {
        mCacheWriter.insertAndUpdateCache("GC123", "my cache", 122, 37, Source.GPX, "foo.gpx");

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.id("GC123");
        cacheTagWriter.cacheName("my cache");
        cacheTagWriter.latitudeLongitude("122", "37");
        cacheTagWriter.gpxName("foo.gpx");
        cacheTagWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testWriteFound() {
        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter);
        cacheTagWriter.symbol("Geocache Found");
        cacheTagWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

}
