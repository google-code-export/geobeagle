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
        GpxCaches gpxCaches = createMock(GpxCaches.class);
        FileFactory fileFactory = createMock(FileFactory.class);
        File file = createMock(File.class);
        CacheProgressUpdater cacheProgressUpdater = createMock(CacheProgressUpdater.class);

        expect(fileFactory.createFile(GpxToCache.GEOBEAGLE_DIR)).andReturn(file);
        expect(file.mkdirs()).andReturn(true);
        cacheWriter.clear(LoadGpx.GPX_PATH);
        Cache cache = new Cache("gc1234", "my cache", 122, 37);
        ArrayList<Cache> caches = new ArrayList<Cache>(1);
        caches.add(cache);
        expect(gpxCaches.iterator()).andReturn(caches.iterator());
        cacheProgressUpdater.update("1: my cache");
        cacheWriter.startWriting();
        expect(gpxCaches.getSource()).andReturn(LoadGpx.GPX_PATH);
        expect(cacheWriter.write("gc1234", "my cache", 122, 37, LoadGpx.GPX_PATH)).andReturn(true);
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
        GpxCaches gpxCaches = createMock(GpxCaches.class);
        CacheWriter cacheWriter = createMock(CacheWriter.class);
        FileFactory fileFactory = createMock(FileFactory.class);
        CacheProgressUpdater cacheProgressUpdater = createMock(CacheProgressUpdater.class);
        File file = createMock(File.class);

        expect(fileFactory.createFile(GpxToCache.GEOBEAGLE_DIR)).andReturn(file);
        expect(file.mkdirs()).andReturn(true);

        cacheWriter.clear(LoadGpx.GPX_PATH);
        Cache cache = new Cache("gc1234", "my cache", 122, 37);
        ArrayList<Cache> caches = new ArrayList<Cache>(1);
        caches.add(cache);
        expect(gpxCaches.iterator()).andReturn(caches.iterator());
        cacheProgressUpdater.update("1: my cache");
        cacheWriter.startWriting();
        expect(gpxCaches.getSource()).andReturn(LoadGpx.GPX_PATH);
        expect(cacheWriter.write("gc1234", "my cache", 122, 37, LoadGpx.GPX_PATH)).andReturn(false);
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
