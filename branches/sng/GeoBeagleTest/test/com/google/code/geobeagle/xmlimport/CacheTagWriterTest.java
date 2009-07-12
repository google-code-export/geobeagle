
package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.xmlimport.CacheTagWriter.CacheTagParser;

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
        mCacheWriter.insertAndUpdateCache(null, null, 0, 0, Source.GPX, null, CacheType.NULL, 0, 0,
                0);

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter, null);
        cacheTagWriter.clear();
        cacheTagWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testEnd() {
        mCacheWriter.clearEarlierLoads();

        PowerMock.replayAll();
        new CacheTagWriter(mCacheWriter, null).end();
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTimeDontLoad() {
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter, null);
        cacheTagWriter.gpxName("foo.gpx");
        assertFalse(cacheTagWriter.gpxTime("2008-04-15T16:10:30"));
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTimeLoad() {
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(false);
        mCacheWriter.clearCaches("foo.gpx");

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter, null);
        cacheTagWriter.gpxName("foo.gpx");
        assertTrue(cacheTagWriter.gpxTime("2008-04-15T16:10:30"));
        PowerMock.verifyAll();
    }

    @Test
    public void testIsoTimeToSql() throws ParseException {
        assertEquals("2008-04-15 16:10:30", new CacheTagWriter(null, null)
                .isoTimeToSql("2008-04-15T16:10:30.7369220-08:00"));
    }

    @Test
    public void testStartWriting() {
        mCacheWriter.startWriting();

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter, null);
        cacheTagWriter.startWriting();
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWritingFailure() {
        mCacheWriter.stopWriting();

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter, null);
        cacheTagWriter.stopWriting(false);
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWritingSuccess() {
        mCacheWriter.stopWriting();
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);
        mCacheWriter.writeGpx("foo.gpx", "2008-04-15 16:10:30");

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter, null);
        cacheTagWriter.gpxName("foo.gpx");
        cacheTagWriter.gpxTime("2008-04-15T16:10:30.7369220-08:00");
        cacheTagWriter.stopWriting(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testWrite() {
        mCacheWriter.insertAndUpdateCache("GC123", "my cache", 122, 37, Source.GPX, "foo.gpx",
                CacheType.TRADITIONAL, 6, 5, 1);
        CacheTagParser cacheTagParser = new CacheTagParser();

        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter, cacheTagParser);
        cacheTagWriter.id("GC123");
        cacheTagWriter.cacheName("my cache");
        cacheTagWriter.latitudeLongitude("122", "37");
        cacheTagWriter.container("Micro");
        cacheTagWriter.terrain("2.5");
        cacheTagWriter.difficulty("3");
        
        cacheTagWriter.gpxName("foo.gpx");
        cacheTagWriter.cacheType("Traditional Cache");
        cacheTagWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testContainer() {
        CacheTagParser cacheTagParser = new CacheTagParser();
        assertEquals(0, cacheTagParser.container("bad string"));
        assertEquals(1, cacheTagParser.container("Micro"));
        assertEquals(2, cacheTagParser.container("Small"));
        assertEquals(3, cacheTagParser.container("Regular"));
        assertEquals(4, cacheTagParser.container("Large"));
    }
    

    @Test
    public void testCacheType() {
        CacheTagParser cacheTagParser = new CacheTagParser();
        assertEquals(CacheType.NULL, cacheTagParser.cacheType("bad string"));
        assertEquals(CacheType.TRADITIONAL, cacheTagParser.cacheType("Traditional Cache"));
        assertEquals(CacheType.MULTI, cacheTagParser.cacheType("Multi-cache"));
        assertEquals(CacheType.UNKNOWN, cacheTagParser.cacheType("Unknown Cache"));
    }

    @Test
    public void testStars() {
        CacheTagParser cacheTagParser = new CacheTagParser();
        assertEquals(0, cacheTagParser.stars("0"));
        assertEquals(1, cacheTagParser.stars("0.5"));
        assertEquals(2, cacheTagParser.stars("1"));
        assertEquals(0, cacheTagParser.stars("foo"));
    }

    @Test
    public void testWriteFound() {
        PowerMock.replayAll();
        CacheTagWriter cacheTagWriter = new CacheTagWriter(mCacheWriter, null);
        cacheTagWriter.symbol("Geocache Found");
        cacheTagWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

}
