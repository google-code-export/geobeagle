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
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.cachedetails.CacheDetailsHtmlWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

@PrepareForTest({
    CacheXmlTagsToSql.class
})
@RunWith(PowerMockRunner.class)
public class CacheXmlTagsToSqlTest {

    private CacheTagSqlWriter mCacheTagWriter;
    private MessageHandlerInterface mMessageHandler;
    private GeoBeagleEnvironment geoBeagleEnvironment;

    @Before
    public void setUp() {
        mCacheTagWriter = PowerMock.createMock(CacheTagSqlWriter.class);
        mMessageHandler = PowerMock.createMock(MessageHandler.class);
        geoBeagleEnvironment = PowerMock.createMock(GeoBeagleEnvironment.class);
    }

    @Test
    public void testAttributes() {
        mCacheTagWriter.symbol("Geocache Found");
        mCacheTagWriter.container("big");
        mCacheTagWriter.difficulty("difficult");
        mCacheTagWriter.terrain("rocky");
        mCacheTagWriter.cacheType("traditional");

        PowerMock.replayAll();
        final CacheXmlTagsToSql cacheXmlTagsToSql = new CacheXmlTagsToSql(mCacheTagWriter, null,
                null, null, null, null);
        cacheXmlTagsToSql.symbol("Geocache Found");
        cacheXmlTagsToSql.container("big");
        cacheXmlTagsToSql.difficulty("difficult");
        cacheXmlTagsToSql.terrain("rocky");
        cacheXmlTagsToSql.cacheType("traditional");
        PowerMock.verifyAll();
    }

    @Test
    public void testCloseTrue() {
        mCacheTagWriter.stopWriting(true);

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, mMessageHandler, null, null, null, null).close(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testEnd() {
        mCacheTagWriter.end(null);

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, null, null, null, null, null).end();
        PowerMock.verifyAll();
    }

    @Test
    public void testEndTag() throws IOException {
        mCacheTagWriter.write(Source.GPX);
        mMessageHandler.updateName("");

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, mMessageHandler, null, null, null, null)
                .endCache(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testEndTagName() throws IOException {
        mCacheTagWriter.write(Source.GPX);
        mCacheTagWriter.cacheName("my cache");
        mMessageHandler.updateName("my cache");

        PowerMock.replayAll();
        final CacheXmlTagsToSql cacheXmlTagsToSql = new CacheXmlTagsToSql(mCacheTagWriter,
                mMessageHandler, null, null, null, null);
        cacheXmlTagsToSql.wptDesc("my cache");
        cacheXmlTagsToSql.endCache(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTime() {
        expect(mCacheTagWriter.gpxTime(null, null, "today")).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new CacheXmlTagsToSql(mCacheTagWriter, null, null, null, null, null)
                .gpxTime("today"));
        PowerMock.verifyAll();
    }

    @Test
    public void testGroundspeakName() {
        mCacheTagWriter.cacheName("GC123");

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, null, null, null, null, null)
                .groundspeakName("GC123");
        PowerMock.verifyAll();
    }

    @Test
    public void testHint() throws IOException {
        PowerMock.replayAll();
        new CacheXmlTagsToSql(null, null, null, null, null, null).hint("a hint");
        PowerMock.verifyAll();
    }

    @Test
    public void testLine() throws IOException {
        PowerMock.replayAll();
        new CacheXmlTagsToSql(null, null, null, null, null, null).line("some data");
        PowerMock.verifyAll();
    }

    @Test
    public void testLogDate() throws IOException {
        PowerMock.replayAll();
        new CacheXmlTagsToSql(null, null, null, null, null, null).logDate("04/30/99");
        PowerMock.verifyAll();
    }

    @Test
    public void testNewCache() {
        mCacheTagWriter.clear();

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, null, null, null, null, null).startCache();
        PowerMock.verifyAll();
    }

    @Test
    public void testOpen() {
        mMessageHandler.updateSource("foo.gpx");
        mCacheTagWriter.startWriting();
        mCacheTagWriter.gpxName("foo.gpx");

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, mMessageHandler, null, geoBeagleEnvironment, null,
                null).open("foo.gpx");
        PowerMock.verifyAll();
    }

    @Test
    public void testStart() throws Exception {
        File file = PowerMock.createMock(File.class);

        expect(geoBeagleEnvironment.getDetailsDirectory()).andReturn("/details/dir");
        PowerMock.expectNew(File.class, "/details/dir").andReturn(file);
        expect(file.mkdirs()).andReturn(true);

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, null, null, geoBeagleEnvironment, null, null)
                .start();
        PowerMock.verifyAll();
    }

    @Test
    public void testStripIllegalFileChars() {
        assertEquals("boring", CacheDetailsHtmlWriter.replaceIllegalFileChars("boring"));
        assertEquals("n_a__s_______t_y__",
                CacheDetailsHtmlWriter.replaceIllegalFileChars("n<a\\/s:*?\"<>|t:y\t/"));
    }

    @Test
    public void testSymbol() {
        mCacheTagWriter.symbol("Geocache Found");

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, null, null, null, null, null)
                .symbol("Geocache Found");
        PowerMock.verifyAll();
    }

    @Test
    public void testWpt() {
        mCacheTagWriter.latitudeLongitude("37", "122");

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, null, null, null, null, null).wpt("37", "122");
        PowerMock.verifyAll();
    }

    @Test
    public void testWptDesc() {
        mCacheTagWriter.cacheName("GC123 by so and so");

        PowerMock.replayAll();
        new CacheXmlTagsToSql(mCacheTagWriter, null, null, null, null, null)
                .wptDesc("GC123 by so and so");
        PowerMock.verifyAll();
    }

    @Test
    public void testWptName() throws IOException {
        ImportWakeLock wakeLock = createMock(ImportWakeLock.class);

        mCacheTagWriter.id("GC123");
        mMessageHandler.updateWaypointId("GC123");

        PowerMock.replayAll();
        CacheXmlTagsToSql cacheXmlTagsToSql = new CacheXmlTagsToSql(mCacheTagWriter,
                mMessageHandler, wakeLock, null, null, null);
        cacheXmlTagsToSql.wptName("GC123");
        PowerMock.verifyAll();
    }
}
