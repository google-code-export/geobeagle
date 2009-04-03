
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.CachePersisterFacadeDI.FileFactory;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.GpxToCacheDI.XmlPullParserWrapper;

import android.os.PowerManager.WakeLock;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class CachePersisterFacadeTest extends TestCase {

    private final CacheDetailsWriter mCacheDetailsWriter = createMock(CacheDetailsWriter.class);
    private final CacheTagWriter mCacheTagWriter = createMock(CacheTagWriter.class);
    private final MessageHandler mMessageHandler = createMock(MessageHandler.class);

    public void testCloseTrue() {
        mCacheTagWriter.stopWriting(true);

        replay(mCacheTagWriter);
        new CachePersisterFacade(mCacheTagWriter, null, null, null, null).close(true);
        verify(mCacheTagWriter);
    }

    public void testEnd() throws IOException {
        mCacheTagWriter.end();

        replay(mCacheTagWriter);
        new CachePersisterFacade(mCacheTagWriter, null, null, null, null).end();
        verify(mCacheTagWriter);
    }

    public void testEndTag() throws IOException {
        mCacheDetailsWriter.close();
        mCacheTagWriter.write();

        replay(mCacheTagWriter);
        replay(mCacheDetailsWriter);
        new CachePersisterFacade(mCacheTagWriter, null, mCacheDetailsWriter, null, null).endTag();
        verify(mCacheDetailsWriter);
        verify(mCacheTagWriter);
    }

    public void testGpxName() {
        mCacheTagWriter.gpxName("foo.gpx");

        replay(mCacheTagWriter);
        new CachePersisterFacade(mCacheTagWriter, null, null, null, null).gpxName("foo.gpx");
        verify(mCacheTagWriter);
    }

    public void testGpxTime() {
        expect(mCacheTagWriter.gpxTime("today")).andReturn(true);

        replay(mCacheTagWriter);
        assertTrue(new CachePersisterFacade(mCacheTagWriter, null, null, null, null)
                .gpxTime("today"));
        verify(mCacheTagWriter);
    }

    public void testGroundspeakName() throws IOException {
        mMessageHandler.updateName("GC123");
        mCacheTagWriter.cacheName("GC123");

        replay(mMessageHandler);
        replay(mCacheTagWriter);
        new CachePersisterFacade(mCacheTagWriter, null, null, mMessageHandler, null)
                .groundspeakName("GC123");
        verify(mMessageHandler);
        verify(mCacheTagWriter);
    }

    public void testHint() throws IOException {
        mCacheDetailsWriter.writeHint("a hint");

        replay(mCacheDetailsWriter);
        new CachePersisterFacade(null, null, mCacheDetailsWriter, null, null).hint("a hint");
        verify(mCacheDetailsWriter);
    }

    public void testLine() throws IOException {
        mCacheDetailsWriter.writeLine("some data");

        replay(mCacheDetailsWriter);
        new CachePersisterFacade(null, null, mCacheDetailsWriter, null, null).line("some data");
        verify(mCacheDetailsWriter);
    }

    public void testLogDate() throws IOException {
        mCacheDetailsWriter.writeLogDate("04/30/99");

        replay(mCacheDetailsWriter);
        new CachePersisterFacade(null, null, mCacheDetailsWriter, null, null).logDate("04/30/99");
        verify(mCacheDetailsWriter);
    }

    public void testOpen() {
        mMessageHandler.updateSource("GC123");
        mCacheTagWriter.startWriting();
        mCacheTagWriter.gpxName("GC123");

        replay(mMessageHandler);
        replay(mCacheTagWriter);
        new CachePersisterFacade(mCacheTagWriter, null, null, mMessageHandler, null).open("GC123");
        verify(mMessageHandler);
        verify(mCacheTagWriter);
    }

    public void testStart() {
        FileFactory fileFactory = createMock(FileFactory.class);
        File file = createMock(File.class);

        expect(fileFactory.createFile(CacheDetailsWriter.GEOBEAGLE_DIR)).andReturn(file);
        expect(file.mkdirs()).andReturn(true);

        replay(fileFactory);
        replay(file);
        CachePersisterFacade cachePersisterFacade = new CachePersisterFacade(mCacheTagWriter,
                fileFactory, null, null, null);
        cachePersisterFacade.start();
        verify(fileFactory);
        verify(file);
    }

    public void testSymbol() throws IOException {
        mCacheTagWriter.symbol("Geocache Found");

        replay(mCacheTagWriter);
        new CachePersisterFacade(mCacheTagWriter, null, null, null, null).symbol("Geocache Found");
        verify(mCacheTagWriter);
    }

    public void testWpt() throws IOException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37");
        expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("122");

        mCacheTagWriter.clear();
        mCacheTagWriter.latitudeLongitude("37", "122");
        mCacheTagWriter.latitudeLongitude("37", "122");

        replay(xmlPullParser);
        new CachePersisterFacade(mCacheTagWriter, null, mCacheDetailsWriter, null, null)
                .wpt(xmlPullParser);
        verify(xmlPullParser);
    }

    public void testWptName() throws IOException {
        WakeLock wakeLock = createMock(WakeLock.class);

        // cacheWriter.clearCaches("foo.gpx");
        mCacheDetailsWriter.open("GC123");
        mCacheDetailsWriter.writeWptName("GC123");
        mCacheTagWriter.id("GC123");
        mMessageHandler.updateWaypointId("GC123");
        wakeLock.acquire(CachePersisterFacade.WAKELOCK_DURATION);

        replay(mCacheDetailsWriter);
        replay(mCacheTagWriter);
        replay(mMessageHandler);
        replay(wakeLock);
        CachePersisterFacade cachePersisterFacade = new CachePersisterFacade(mCacheTagWriter, null,
                mCacheDetailsWriter, mMessageHandler, wakeLock);
        cachePersisterFacade.wptName("GC123");
        verify(mCacheDetailsWriter);
        verify(mCacheTagWriter);
        verify(mCacheDetailsWriter);
        verify(wakeLock);
    }
}
