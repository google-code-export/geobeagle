
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.di.CachePersisterFacadeDI.FileFactory;
import com.google.code.geobeagle.io.di.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.di.GpxToCacheDI.XmlPullParserWrapper;

import android.os.PowerManager.WakeLock;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class CachePersisterFacadeTest extends TestCase {

    public void testClose() {
        CacheTagWriter cacheTagWriter = createMock(CacheTagWriter.class);

        cacheTagWriter.stopWriting();

        replay(cacheTagWriter);
        new CachePersisterFacade(cacheTagWriter, null, null, null, null).close();
        verify(cacheTagWriter);
    }

    public void testEndTag() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        CacheTagWriter cacheTagWriter = createMock(CacheTagWriter.class);

        cacheDetailsWriter.close();
        cacheTagWriter.write();

        replay(cacheTagWriter);
        replay(cacheDetailsWriter);
        new CachePersisterFacade(cacheTagWriter, null, cacheDetailsWriter, null, null).endTag();
        verify(cacheDetailsWriter);
        verify(cacheTagWriter);
    }

    public void testGroundspeakName() throws IOException {
        MessageHandler messageHandler = createMock(MessageHandler.class);
        CacheTagWriter cacheTagWriter = createMock(CacheTagWriter.class);

        messageHandler.updateName("GC123");
        cacheTagWriter.name("GC123");

        replay(messageHandler);
        replay(cacheTagWriter);
        new CachePersisterFacade(cacheTagWriter, null, null, messageHandler, null)
                .groundspeakName("GC123");
        verify(messageHandler);
        verify(cacheTagWriter);
    }

    public void testHint() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);

        cacheDetailsWriter.writeHint("a hint");

        replay(cacheDetailsWriter);
        new CachePersisterFacade(null, null, cacheDetailsWriter, null, null).hint("a hint");
        verify(cacheDetailsWriter);
    }

    public void testLine() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);

        cacheDetailsWriter.writeLine("some data");

        replay(cacheDetailsWriter);
        new CachePersisterFacade(null, null, cacheDetailsWriter, null, null).line("some data");
        verify(cacheDetailsWriter);
    }

    public void testLogDate() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);

        cacheDetailsWriter.writeLogDate("04/30/99");

        replay(cacheDetailsWriter);
        new CachePersisterFacade(null, null, cacheDetailsWriter, null, null).logDate("04/30/99");
        verify(cacheDetailsWriter);
    }

    public void testOpen() {
        MessageHandler messageHandler = createMock(MessageHandler.class);
        CacheTagWriter cacheTagWriter = createMock(CacheTagWriter.class);

        messageHandler.updateSource("GC123");
        cacheTagWriter.startWriting();
        cacheTagWriter.source("GC123");

        replay(messageHandler);
        replay(cacheTagWriter);
        new CachePersisterFacade(cacheTagWriter, null, null, messageHandler, null).open("GC123");
        verify(messageHandler);
        verify(cacheTagWriter);
    }

    public void testStart() {
        FileFactory fileFactory = createMock(FileFactory.class);
        File file = createMock(File.class);
        CacheTagWriter cacheTagWriter = createMock(CacheTagWriter.class);

        expect(fileFactory.createFile(CacheDetailsWriter.GEOBEAGLE_DIR)).andReturn(file);
        expect(file.mkdirs()).andReturn(true);
        cacheTagWriter.clearAllImportedCaches();

        replay(fileFactory);
        replay(file);
        CachePersisterFacade cachePersisterFacade = new CachePersisterFacade(cacheTagWriter,
                fileFactory, null, null, null);
        cachePersisterFacade.start();
        verify(fileFactory);
        verify(file);
    }

    public void testSymbol() throws IOException {
        CacheTagWriter cacheTagWriter = createMock(CacheTagWriter.class);

        cacheTagWriter.symbol("Geocache Found");
        
        replay(cacheTagWriter);
        new CachePersisterFacade(cacheTagWriter, null, null, null, null).symbol("Geocache Found");
        verify(cacheTagWriter);
    }

    public void testWpt() throws IOException {
        CacheTagWriter cacheTagWriter = createMock(CacheTagWriter.class);
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37");
        expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("122");

        cacheTagWriter.clear();
        cacheTagWriter.latitudeLongitude("37", "122");
        cacheTagWriter.latitudeLongitude("37", "122");

        replay(xmlPullParser);
        new CachePersisterFacade(cacheTagWriter, null, cacheDetailsWriter, null, null)
                .wpt(xmlPullParser);
        verify(xmlPullParser);
    }

    public void testWptName() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        CacheTagWriter cacheTagWriter = createMock(CacheTagWriter.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);
        WakeLock wakeLock = createMock(WakeLock.class);

        // cacheWriter.clearCaches("foo.gpx");
        cacheDetailsWriter.open("GC123");
        cacheDetailsWriter.writeWptName("GC123");
        cacheTagWriter.id("GC123");
        messageHandler.updateWaypoint("GC123");
        wakeLock.acquire(CachePersisterFacade.WAKELOCK_DURATION);

        replay(cacheDetailsWriter);
        replay(cacheTagWriter);
        replay(messageHandler);
        replay(wakeLock);
        CachePersisterFacade cachePersisterFacade = new CachePersisterFacade(cacheTagWriter, null,
                cacheDetailsWriter, messageHandler, wakeLock);
        cachePersisterFacade.wptName("GC123");
        verify(cacheDetailsWriter);
        verify(cacheTagWriter);
        verify(cacheDetailsWriter);
        verify(wakeLock);
    }
}
