
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;
import com.google.code.geobeagle.io.LoadGpx.Cache;
import com.google.code.geobeagle.io.LoadGpx.FileFactory;
import com.google.code.geobeagle.ui.CacheListDelegate.CacheProgressUpdater;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class LoadGpxTest extends TestCase {

    public void testLoad() throws XmlPullParserException, IOException {
        CacheWriter cacheWriter = createMock(CacheWriter.class);
        GpxToCache.GpxCaches gpxCaches = createMock(GpxToCache.GpxCaches.class);
        FileFactory fileFactory = createMock(FileFactory.class);
        File file = createMock(File.class);
        CacheProgressUpdater cacheProgressUpdater = createMock(CacheProgressUpdater.class);


        expect(fileFactory.createFile(GpxToCache.GEOBEAGLE_DIR)).andReturn(file);
        expect(file.mkdirs()).andReturn(true);
        cacheWriter.clear();
        Cache cache = new Cache("gc1234", "my cache", 122, 37);
        ArrayList<Cache> caches = new ArrayList<Cache>(1);
        caches.add(cache);
        expect(gpxCaches.iterator()).andReturn(caches.iterator());
        cacheProgressUpdater.update("my cache");
        cacheWriter.startWriting();
        expect(cacheWriter.write("gc1234", "my cache", 122, 37)).andReturn(true);
        cacheWriter.stopWriting();

        replay(fileFactory);
        replay(cacheProgressUpdater);
        replay(file);
        replay(gpxCaches);
        replay(cacheWriter);
        LoadGpx loadGpx = new LoadGpx(cacheWriter, gpxCaches, fileFactory);
        loadGpx.load(cacheProgressUpdater);
        verify(cacheWriter);
        verify(gpxCaches);
        verify(cacheProgressUpdater);
    }

    public void testLoadHasError() throws XmlPullParserException, IOException {
        GpxToCache.GpxCaches gpxCaches = createMock(GpxToCache.GpxCaches.class);
        CacheWriter cacheWriter = createMock(CacheWriter.class);
        FileFactory fileFactory = createMock(FileFactory.class);
        CacheProgressUpdater cacheProgressUpdater = createMock(CacheProgressUpdater.class);
        File file = createMock(File.class);

        expect(fileFactory.createFile(GpxToCache.GEOBEAGLE_DIR)).andReturn(file);
        expect(file.mkdirs()).andReturn(true);

        cacheWriter.clear();
        Cache cache = new Cache("gc1234", "my cache", 122, 37);
        ArrayList<Cache> caches = new ArrayList<Cache>(1);
        caches.add(cache);
        expect(gpxCaches.iterator()).andReturn(caches.iterator());
        cacheProgressUpdater.update("my cache");
        cacheWriter.startWriting();
        expect(cacheWriter.write("gc1234", "my cache", 122, 37)).andReturn(false);
        cacheWriter.stopWriting();

        replay(cacheProgressUpdater);
        replay(fileFactory);
        replay(file);
        replay(gpxCaches);
        replay(cacheWriter);
        LoadGpx loadGpx = new LoadGpx(cacheWriter, gpxCaches, fileFactory);
        loadGpx.load(cacheProgressUpdater);
        verify(cacheProgressUpdater);
        verify(cacheWriter);
        verify(gpxCaches);
        verify(fileFactory);
        verify(file);
    }
}
