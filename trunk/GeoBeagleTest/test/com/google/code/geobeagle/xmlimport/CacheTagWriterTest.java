
package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.Tag;
import com.google.code.geobeagle.database.TagWriterImpl;
import com.google.code.geobeagle.database.TagWriterNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CacheTagWriterTest {
    private CacheWriter mCacheWriter;
    private TagWriterNull mTagWriterNull;
    private TagWriterImpl mTagWriterImpl;
    private CacheTypeFactory mCacheTypeFactory;
    private CacheTagSqlWriter mCacheTagSqlWriter;

    @Before
    public void setUp() {
        mCacheWriter = PowerMock.createMock(CacheWriter.class);
        mTagWriterNull = PowerMock.createMock(TagWriterNull.class);
        mTagWriterImpl = PowerMock.createMock(TagWriterImpl.class);
        mCacheTypeFactory = PowerMock.createMock(CacheTypeFactory.class);
        mCacheTagSqlWriter = new CacheTagSqlWriter(mCacheWriter, mCacheTypeFactory, mTagWriterImpl,
                mTagWriterNull);
    }

    @Test
    public void testClear() {
        mCacheWriter.insertAndUpdateCache(null, null, 0, 0, Source.GPX, null, CacheType.NULL, 0,
                0, 0);
        mTagWriterNull.add(null, Tag.FOUND);

        PowerMock.replayAll();
        mCacheTagSqlWriter.clear();
        mCacheTagSqlWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testEnd() {
        mCacheWriter.clearEarlierLoads();

        PowerMock.replayAll();
        mCacheTagSqlWriter.end();
        PowerMock.verifyAll();
    }

    @Test
    public void testSymbol() {
        mCacheWriter.insertAndUpdateCache(null, null, 0, 0, Source.GPX, null, null, 0, 0, 0);
        mTagWriterImpl.add(null, Tag.FOUND);
        
        PowerMock.replayAll();
        mCacheTagSqlWriter.symbol("Geocache Found");
        mCacheTagSqlWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }
    @Test
    public void testGpxTimeDontLoad() {
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);

        PowerMock.replayAll();
        mCacheTagSqlWriter.gpxName("foo.gpx");
        assertFalse(mCacheTagSqlWriter.gpxTime("2008-04-15T16:10:30"));
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTimeLoad() {
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(false);
        mCacheWriter.clearCaches("foo.gpx");

        PowerMock.replayAll();
        mCacheTagSqlWriter.gpxName("foo.gpx");
        assertTrue(mCacheTagSqlWriter.gpxTime("2008-04-15T16:10:30"));
        PowerMock.verifyAll();
    }

    @Test
    public void testIsoTimeToSql() {
        assertEquals("2008-04-15 16:10:30", mCacheTagSqlWriter
                .isoTimeToSql("2008-04-15T16:10:30.7369220-08:00"));
    }

    @Test
    public void testStartWriting() {
        mCacheWriter.startWriting();

        PowerMock.replayAll();
        mCacheTagSqlWriter.startWriting();
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWritingFailure() {
        mCacheWriter.stopWriting();

        PowerMock.replayAll();
        mCacheTagSqlWriter.stopWriting(false);
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWritingSuccess() {
        mCacheWriter.stopWriting();
        expect(mCacheWriter.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);
        mCacheWriter.writeGpx("foo.gpx");

        PowerMock.replayAll();
        mCacheTagSqlWriter.gpxName("foo.gpx");
        mCacheTagSqlWriter.gpxTime("2008-04-15T16:10:30.7369220-08:00");
        mCacheTagSqlWriter.stopWriting(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testWrite() {
        mCacheWriter.insertAndUpdateCache("GC123", "my cache", 122, 37, Source.GPX, "foo.gpx",
                CacheType.TRADITIONAL, 6, 5, 1);
        expect(mCacheTypeFactory.container("Micro")).andReturn(1);
        expect(mCacheTypeFactory.stars("2.5")).andReturn(5);
        expect(mCacheTypeFactory.stars("3")).andReturn(6);
        expect(mCacheTypeFactory.fromTag("Traditional Cache")).andReturn(CacheType.TRADITIONAL);
        mTagWriterNull.add("GC123", Tag.FOUND);

        PowerMock.replayAll();
        mCacheTagSqlWriter.id("GC123");
        mCacheTagSqlWriter.cacheName("my cache");
        mCacheTagSqlWriter.latitudeLongitude("122", "37");
        mCacheTagSqlWriter.container("Micro");
        mCacheTagSqlWriter.terrain("2.5");
        mCacheTagSqlWriter.difficulty("3");
        
        mCacheTagSqlWriter.gpxName("foo.gpx");
        mCacheTagSqlWriter.cacheType("Traditional Cache");
        mCacheTagSqlWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }
}
