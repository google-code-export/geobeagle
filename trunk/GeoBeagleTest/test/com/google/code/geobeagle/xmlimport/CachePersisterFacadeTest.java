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
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.FileFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.PowerManager.WakeLock;

import java.io.File;
import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class CachePersisterFacadeTest {

    private final CacheTagSqlWriter mCacheTagWriter = PowerMock.createMock(CacheTagSqlWriter.class);
    private final MessageHandlerInterface mMessageHandler = PowerMock
            .createMock(MessageHandler.class);

    @Test
    public void testAttributes() {
        mCacheTagWriter.symbol("Geocache Found");
        mCacheTagWriter.container("big");
        mCacheTagWriter.difficulty("difficult");
        mCacheTagWriter.terrain("rocky");
        mCacheTagWriter.cacheType("traditional");

        PowerMock.replayAll();
        final ImportCacheActions importCacheActions = new ImportCacheActions(mCacheTagWriter,
                null, null, null, null);
        importCacheActions.symbol("Geocache Found");
        importCacheActions.container("big");
        importCacheActions.difficulty("difficult");
        importCacheActions.terrain("rocky");
        importCacheActions.cacheType("traditional");
        PowerMock.verifyAll();
    }

    @Test
    public void testCloseTrue() {
        mCacheTagWriter.stopWriting(true);

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, mMessageHandler, null,
                null).close(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testEnd() {
        mCacheTagWriter.end();

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, null, null, null).end();
        PowerMock.verifyAll();
    }

    @Test
    public void testEndTag() throws IOException {
        mCacheTagWriter.write(Source.GPX);
        mMessageHandler.updateName("");

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, mMessageHandler, null,
                null).endCache(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testEndTagName() throws IOException {
        mCacheTagWriter.write(Source.GPX);
        mCacheTagWriter.cacheName("my cache");
        mMessageHandler.updateName("my cache");

        PowerMock.replayAll();
        final ImportCacheActions importCacheActions = new ImportCacheActions(mCacheTagWriter,
                null, mMessageHandler, null, null);
        importCacheActions.wptDesc("my cache");
        importCacheActions.endCache(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTime() {
        expect(mCacheTagWriter.gpxTime("today")).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new ImportCacheActions(mCacheTagWriter, null, null, null, null)
                .gpxTime("today"));
        PowerMock.verifyAll();
    }

    @Test
    public void testGroundspeakName() {
        mCacheTagWriter.cacheName("GC123");

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, null, null, null)
                .groundspeakName("GC123");
        PowerMock.verifyAll();
    }

    @Test
    public void testHint() throws IOException {
        PowerMock.replayAll();
        new ImportCacheActions(null, null, null, null, null).hint("a hint");
        PowerMock.verifyAll();
    }

    @Test
    public void testLine() throws IOException {
        PowerMock.replayAll();
        new ImportCacheActions(null, null, null, null, null)
                .line("some data");
        PowerMock.verifyAll();
    }

    @Test
    public void testLogDate() throws IOException {
        PowerMock.replayAll();
        new ImportCacheActions(null, null, null, null, null)
                .logDate("04/30/99");
        PowerMock.verifyAll();
    }

    @Test
    public void testNewCache() {
        mCacheTagWriter.clear();

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, null, null, null).startCache();
        PowerMock.verifyAll();
    }

    @Test
    public void testOpen() {
        mMessageHandler.updateSource("foo.gpx");
        mCacheTagWriter.startWriting();
        mCacheTagWriter.gpxName("foo.gpx");

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, mMessageHandler, null,
                null).open("foo.gpx");
        PowerMock.verifyAll();
    }

    @Test
    public void testStart() {
        FileFactory fileFactory = PowerMock.createMock(FileFactory.class);
        File file = PowerMock.createMock(File.class);

        expect(fileFactory.createFile(null)).andReturn(file);
        expect(file.mkdirs()).andReturn(true);

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, fileFactory, null, null, null).start();
        PowerMock.verifyAll();
    }

    @Test
    public void testStripIllegalFileChars() {
        assertEquals("boring", CacheDetailsWriter.replaceIllegalFileChars("boring"));
        assertEquals("n_a__s_______t_y__", CacheDetailsWriter
                .replaceIllegalFileChars("n<a\\/s:*?\"<>|t:y\t/"));
    }

    @Test
    public void testSymbol() {
        mCacheTagWriter.symbol("Geocache Found");

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, null, null, null)
                .symbol("Geocache Found");
        PowerMock.verifyAll();
    }

    @Test
    public void testWpt() {
        mCacheTagWriter.latitudeLongitude("37", "122");

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, null, null, null)
                .wpt("37", "122");
        PowerMock.verifyAll();
    }

    @Test
    public void testWptDesc() {
        mCacheTagWriter.cacheName("GC123 by so and so");

        PowerMock.replayAll();
        new ImportCacheActions(mCacheTagWriter, null, null, null, null)
                .wptDesc("GC123 by so and so");
        PowerMock.verifyAll();
    }

    @Test
    public void testWptName() throws IOException {
        WakeLock wakeLock = createMock(WakeLock.class);

        mCacheTagWriter.id("GC123");
        mMessageHandler.updateWaypointId("GC123");
        wakeLock.acquire(GpxLoader.WAKELOCK_DURATION);

        PowerMock.replayAll();
        ImportCacheActions importCacheActions = new ImportCacheActions(mCacheTagWriter, null,
                mMessageHandler, wakeLock, null);
        importCacheActions.wptName("GC123");
        PowerMock.verifyAll();
    }
}
