/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.database.CacheSqlWriter;
import com.google.code.geobeagle.database.ClearCachesFromSource;
import com.google.code.geobeagle.database.GpxTableWriterGpxFiles;
import com.google.code.geobeagle.database.Tag;
import com.google.code.geobeagle.database.TagWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CacheTagWriterTest extends GeoBeagleTest {
    private CacheSqlWriter cacheSqlWriter;
    private GpxTableWriterGpxFiles gpxTableWriterGpxFiles;
    private TagWriter tagWriter;
    private CacheTypeFactory cacheTypeFactory;
    private CacheTagSqlWriter cacheTagSqlWriter;
    private ClearCachesFromSource clearCachesFromSource;

    @Before
    public void setUp() {
        cacheSqlWriter = PowerMock.createMock(CacheSqlWriter.class);
        gpxTableWriterGpxFiles = PowerMock.createMock(GpxTableWriterGpxFiles.class);
        tagWriter = PowerMock.createMock(TagWriter.class);
        cacheTypeFactory = PowerMock.createMock(CacheTypeFactory.class);
        clearCachesFromSource = PowerMock.createMock(ClearCachesFromSource.class);
        cacheTagSqlWriter = new CacheTagSqlWriter(cacheSqlWriter, gpxTableWriterGpxFiles,
                cacheTypeFactory, tagWriter);
    }

    @Test
    public void testClear() {
        cacheSqlWriter.insertAndUpdateCache(null, null, 0, 0, Source.GPX, null, CacheType.NULL, 0, 0,
                0, true, false, false);

        PowerMock.replayAll();
        cacheTagSqlWriter.clear();
        cacheTagSqlWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testEnd() {
        clearCachesFromSource.clearEarlierLoads();

        PowerMock.replayAll();
        cacheTagSqlWriter.end(clearCachesFromSource);
        PowerMock.verifyAll();
    }

    @Test
    public void testSymbol() {
        cacheSqlWriter.insertAndUpdateCache(null, null, 0, 0, Source.GPX, null, null, 0, 0, 0, false,
                false, true);
        tagWriter.add(null, Tag.FOUND, false);

        PowerMock.replayAll();
        cacheTagSqlWriter.symbol("Geocache Found");
        cacheTagSqlWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTimeDontLoad() {
        expect(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);

        PowerMock.replayAll();
        cacheTagSqlWriter.gpxName("foo.gpx");
        assertFalse(cacheTagSqlWriter.gpxTime(clearCachesFromSource, gpxTableWriterGpxFiles,
                "2008-04-15T16:10:30"));
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTimeLoad() {
        expect(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(false);
        clearCachesFromSource.clearCaches("foo.gpx");

        PowerMock.replayAll();
        cacheTagSqlWriter.gpxName("foo.gpx");
        assertTrue(cacheTagSqlWriter.gpxTime(clearCachesFromSource, gpxTableWriterGpxFiles,
                "2008-04-15T16:10:30"));
        PowerMock.verifyAll();
    }

    @Test
    public void testIsoTimeToSql() {
        assertEquals("2008-04-15 16:10:30",
                cacheTagSqlWriter.isoTimeToSql("2008-04-15T16:10:30.7369220-08:00"));
    }

    @Test
    public void testStartWriting() {
        cacheSqlWriter.startWriting();

        PowerMock.replayAll();
        cacheTagSqlWriter.startWriting();
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWritingFailure() {
        cacheSqlWriter.stopWriting();

        PowerMock.replayAll();
        cacheTagSqlWriter.stopWriting(false);
        PowerMock.verifyAll();
    }

    @Test
    public void testStopWritingSuccess() {
        cacheSqlWriter.stopWriting();
        expect(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("foo.gpx", "2008-04-15 16:10:30")).andReturn(true);
        gpxTableWriterGpxFiles.writeGpx("foo.gpx");

        PowerMock.replayAll();
        cacheTagSqlWriter.gpxName("foo.gpx");
        cacheTagSqlWriter.gpxTime(clearCachesFromSource, gpxTableWriterGpxFiles,
                "2008-04-15T16:10:30.7369220-08:00");
        cacheTagSqlWriter.stopWriting(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testWrite() {
        cacheSqlWriter.insertAndUpdateCache("GC123", "my cache", 122, 37, Source.GPX, "foo.gpx",
                CacheType.TRADITIONAL, 6, 5, 1, false, false, true);
        expect(cacheTypeFactory.container("Micro")).andReturn(1);
        expect(cacheTypeFactory.stars("2.5")).andReturn(5);
        expect(cacheTypeFactory.stars("3")).andReturn(6);
        expect(cacheTypeFactory.fromTag("Traditional Cache")).andReturn(CacheType.TRADITIONAL);
        tagWriter.add("GC123", Tag.FOUND, false);

        PowerMock.replayAll();
        cacheTagSqlWriter.id("GC123");
        cacheTagSqlWriter.cacheName("my cache");
        cacheTagSqlWriter.latitudeLongitude("122", "37");
        cacheTagSqlWriter.container("Micro");
        cacheTagSqlWriter.terrain("2.5");
        cacheTagSqlWriter.difficulty("3");
        cacheTagSqlWriter.symbol("Geocache Found");

        cacheTagSqlWriter.gpxName("foo.gpx");
        cacheTagSqlWriter.cacheType("Traditional Cache");
        cacheTagSqlWriter.write(Source.GPX);
        PowerMock.verifyAll();
    }
}
