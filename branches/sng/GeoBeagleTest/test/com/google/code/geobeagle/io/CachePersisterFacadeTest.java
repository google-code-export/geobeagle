
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.io.CachePersisterFacadeDI.FileFactory;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.mainactivity.GeocacheFactory.Source;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.PowerManager.WakeLock;

import java.io.File;
import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class CachePersisterFacadeTest {

    private final CacheDetailsWriter mCacheDetailsWriter = PowerMock
            .createMock(CacheDetailsWriter.class);
    private final CacheTagWriter mCacheTagWriter = PowerMock.createMock(CacheTagWriter.class);
    private final MessageHandler mMessageHandler = PowerMock.createMock(MessageHandler.class);

    @Test
    public void testCloseTrue() {
        mCacheTagWriter.stopWriting(true);

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, null, null, null).close(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testEnd() throws IOException {
        mCacheTagWriter.end();

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, null, null, null).end();
        PowerMock.verifyAll();
    }

    @Test
    public void testEndTag() throws IOException {
        mCacheDetailsWriter.close();
        mCacheTagWriter.write(Source.GPX);

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, mCacheDetailsWriter, null, null)
                .endTag(Source.GPX);
        PowerMock.verifyAll();
    }

    @Test
    public void testGpxTime() {
        expect(mCacheTagWriter.gpxTime("today")).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new CachePersisterFacade(mCacheTagWriter, null, null, null, null)
                .gpxTime("today"));
        PowerMock.verifyAll();
    }

    @Test
    public void testGroundspeakName() throws IOException {
        mCacheTagWriter.cacheName("GC123");

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, null, mMessageHandler, null)
                .groundspeakName("GC123");
        PowerMock.verifyAll();
    }

    @Test
    public void testHint() throws IOException {
        mCacheDetailsWriter.writeHint("a hint");

        PowerMock.replayAll();
        new CachePersisterFacade(null, null, mCacheDetailsWriter, null, null).hint("a hint");
        PowerMock.verifyAll();
    }

    @Test
    public void testLine() throws IOException {
        mCacheDetailsWriter.writeLine("some data");

        PowerMock.replayAll();
        new CachePersisterFacade(null, null, mCacheDetailsWriter, null, null).line("some data");
        PowerMock.verifyAll();
    }

    @Test
    public void testLogDate() throws IOException {
        mCacheDetailsWriter.writeLogDate("04/30/99");

        PowerMock.replayAll();
        new CachePersisterFacade(null, null, mCacheDetailsWriter, null, null).logDate("04/30/99");
        PowerMock.verifyAll();
    }

    @Test
    public void testNewCache() throws IOException {
        mCacheTagWriter.clear();

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, null, null, null).newCache();
        PowerMock.verifyAll();
    }

    @Test
    public void testOpen() {
        mMessageHandler.updateSource("GC123");
        mCacheTagWriter.startWriting();
        mCacheTagWriter.gpxName("GC123");

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, null, mMessageHandler, null).open("GC123");
        PowerMock.verifyAll();
    }

    @Test
    public void testStart() {
        FileFactory fileFactory = PowerMock.createMock(FileFactory.class);
        File file = PowerMock.createMock(File.class);

        expect(fileFactory.createFile(CacheDetailsWriter.GEOBEAGLE_DIR)).andReturn(file);
        expect(file.mkdirs()).andReturn(true);

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, fileFactory, null, null, null).start();
        PowerMock.verifyAll();
    }

    @Test
    public void testSymbol() throws IOException {
        mCacheTagWriter.symbol("Geocache Found");

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, null, null, null).symbol("Geocache Found");
        PowerMock.verifyAll();
    }

    @Test
    public void testWpt() throws IOException {
        mCacheTagWriter.latitudeLongitude("37", "122");
        mCacheDetailsWriter.latitudeLongitude("37", "122");

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, mCacheDetailsWriter, null, null).wpt("37",
                "122");
        PowerMock.verifyAll();
    }

    @Test
    public void testWptDesc() throws IOException {
        mMessageHandler.updateName("GC123 by so and so");
        mCacheTagWriter.cacheName("GC123 by so and so");

        PowerMock.replayAll();
        new CachePersisterFacade(mCacheTagWriter, null, null, mMessageHandler, null)
                .wptDesc("GC123 by so and so");
        PowerMock.verifyAll();
    }

    @Test
    public void testWptName() throws IOException {
        WakeLock wakeLock = createMock(WakeLock.class);

        mCacheDetailsWriter.open("GC123");
        mCacheDetailsWriter.writeWptName("GC123");
        mCacheTagWriter.id("GC123");
        mMessageHandler.updateWaypointId("GC123");
        wakeLock.acquire(CachePersisterFacade.WAKELOCK_DURATION);

        PowerMock.replayAll();
        CachePersisterFacade cachePersisterFacade = new CachePersisterFacade(mCacheTagWriter, null,
                mCacheDetailsWriter, mMessageHandler, wakeLock);
        cachePersisterFacade.wptName("GC123");
        PowerMock.verifyAll();
    }
}
