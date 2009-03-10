
package com.google.code.geobeagle.io;

import com.google.code.geobeagle.io.Database.CacheWriter;

import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

public class CacheTagWriterTest extends TestCase {
    public void testClear() {
        CacheWriter cacheWriter = createMock(CacheWriter.class);

        cacheWriter.insertAndUpdateCache(null, null, 0, 0, null);

        replay(cacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(cacheWriter);
        cacheTagWriter.clear();
        cacheTagWriter.write();
        verify(cacheWriter);
    }

    public void testClearAllImportedCaches() {
        CacheWriter cacheWriter = createMock(CacheWriter.class);

        cacheWriter.clearAllImportedCaches();

        replay(cacheWriter);
        new CacheTagWriter(cacheWriter).clearAllImportedCaches();
        verify(cacheWriter);
    }

    public void testStartWriting() {
        CacheWriter cacheWriter = createMock(CacheWriter.class);

        cacheWriter.startWriting();

        replay(cacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(cacheWriter);
        cacheTagWriter.startWriting();
        verify(cacheWriter);
    }

    public void testStopWriting() {
        CacheWriter cacheWriter = createMock(CacheWriter.class);

        cacheWriter.stopWriting();

        replay(cacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(cacheWriter);
        cacheTagWriter.stopWriting();
        verify(cacheWriter);
    }

    public void testWrite() {
        CacheWriter cacheWriter = createMock(CacheWriter.class);

        cacheWriter.insertAndUpdateCache("GC123", "my cache", 122, 37, "foo.gpx");

        replay(cacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(cacheWriter);
        cacheTagWriter.id("GC123");
        cacheTagWriter.name("my cache");
        cacheTagWriter.latitudeLongitude("122", "37");
        cacheTagWriter.source("foo.gpx");
        cacheTagWriter.write();
        verify(cacheWriter);
    }


    public void testWriteFound() {
        CacheWriter cacheWriter = createMock(CacheWriter.class);

        replay(cacheWriter);
        CacheTagWriter cacheTagWriter = new CacheTagWriter(cacheWriter);
        cacheTagWriter.symbol("Geocache Found");
        cacheTagWriter.write();
        verify(cacheWriter);
    }

}
