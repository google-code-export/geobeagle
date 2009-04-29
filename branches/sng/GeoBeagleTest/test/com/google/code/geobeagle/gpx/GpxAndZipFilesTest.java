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

package com.google.code.geobeagle.gpx;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.gpx.IGpxReader;
import com.google.code.geobeagle.gpx.IGpxReaderIter;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilesIterFactory;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilesIter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FilenameFilter;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        File.class, GpxAndZipFiles.class
})
public class GpxAndZipFilesTest {

    @Test
    public void GpxFilesIterator() throws Exception {
        FilenameFilter filenameFilter = PowerMock.createMock(FilenameFilter.class);
        GpxAndZipFilesIterFactory gpxAndZipFilesIterFactory = PowerMock
                .createMock(GpxAndZipFilesIterFactory.class);
        GpxAndZipFilesIter gpxAndZipFilesIter = PowerMock.createMock(GpxAndZipFilesIter.class);
        File file = PowerMock.createMock(File.class);

        PowerMock.expectNew(File.class, GpxAndZipFiles.GPX_DIR).andReturn(file);
        String[] fileList = new String[] {
                "foo.gpx", "bar.gpx"
        };
        expect(file.list(filenameFilter)).andReturn(fileList);
        PowerMock.expectNew(GpxAndZipFilesIter.class, fileList, gpxAndZipFilesIterFactory)
                .andReturn(gpxAndZipFilesIter);

        PowerMock.replayAll();
        new GpxAndZipFiles(filenameFilter, gpxAndZipFilesIterFactory).iterator();
        PowerMock.verifyAll();
    }

    @Test
    public void GpxFilesIteratorError() throws Exception {
        FilenameFilter filenameFilter = PowerMock.createMock(FilenameFilter.class);
        GpxAndZipFilesIterFactory gpxAndZipFilesIterFactory = PowerMock
                .createMock(GpxAndZipFilesIterFactory.class);
        File file = PowerMock.createMock(File.class);

        PowerMock.expectNew(File.class, GpxAndZipFiles.GPX_DIR).andReturn(file);
        expect(file.list(filenameFilter)).andReturn(null);

        PowerMock.replayAll();
        assertEquals(null, new GpxAndZipFiles(filenameFilter, gpxAndZipFilesIterFactory).iterator());
        PowerMock.verifyAll();
    }

    @Test
    public void testCantReadDir() throws Exception {
        assertFalse(new GpxAndZipFilesIter(new String[] {}, null).hasNext());
    }

    @Test
    public void testFilenameFilter() {
        FilenameFilter filenameFilter = new GpxAndZipFilenameFilter();
        assertTrue(filenameFilter.accept(null, "foo.gpx"));
        assertTrue(filenameFilter.accept(null, "foo.zip"));
        assertTrue(filenameFilter.accept(null, "foo.ZIP"));
        assertFalse(filenameFilter.accept(null, "skip.me"));
        assertFalse(filenameFilter.accept(null, ".appledetritus010.gpx"));
    }

    @Test
    public void testHasSubFiles() throws Exception {
        GpxAndZipFilesIterFactory gpxAndZipFilesIterFactory = PowerMock
                .createStrictMock(GpxAndZipFilesIterFactory.class);
        IGpxReaderIter gpxReaderIter1 = PowerMock.createStrictMock(IGpxReaderIter.class);
        IGpxReaderIter gpxReaderIter2 = PowerMock.createStrictMock(IGpxReaderIter.class);
        IGpxReader gpxReader1 = PowerMock.createStrictMock(IGpxReader.class);
        IGpxReader gpxReader2 = PowerMock.createStrictMock(IGpxReader.class);
        IGpxReader gpxReader3 = PowerMock.createStrictMock(IGpxReader.class);

        expect(gpxAndZipFilesIterFactory.fromFile("foo.gpx")).andReturn(gpxReaderIter1);
        expect(gpxReaderIter1.next()).andReturn(gpxReader1);
        expect(gpxReaderIter1.hasNext()).andReturn(true).atLeastOnce();
        expect(gpxReaderIter1.next()).andReturn(gpxReader2);
        expect(gpxReaderIter1.hasNext()).andReturn(false).atLeastOnce();
        expect(gpxAndZipFilesIterFactory.fromFile("bar.gpx")).andReturn(gpxReaderIter2);
        expect(gpxReaderIter2.next()).andReturn(gpxReader3);
        expect(gpxReaderIter2.hasNext()).andReturn(false).atLeastOnce();

        PowerMock.replayAll();
        GpxAndZipFilesIter gpxFileIter = new GpxAndZipFilesIter(new String[] {
                "foo.gpx", "bar.gpx"
        }, gpxAndZipFilesIterFactory);
        assertTrue(gpxFileIter.hasNext());
        assertEquals(gpxReader1, gpxFileIter.next());
        assertTrue(gpxFileIter.hasNext());
        assertEquals(gpxReader2, gpxFileIter.next());
        assertTrue(gpxFileIter.hasNext());
        assertEquals(gpxReader3, gpxFileIter.next());
        assertFalse(gpxFileIter.hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testNoFiles() throws Exception {
        assertFalse(new GpxAndZipFilesIter(new String[] {}, null).hasNext());
    }

    @Test
    public void testOneFile() throws Exception {
        GpxAndZipFilesIterFactory gpxAndZipFilesIterFactory = PowerMock
                .createMock(GpxAndZipFilesIterFactory.class);
        IGpxReaderIter gpxReaderIter = PowerMock.createMock(IGpxReaderIter.class);
        IGpxReader gpxReader = PowerMock.createMock(IGpxReader.class);

        expect(gpxAndZipFilesIterFactory.fromFile("foo.gpx")).andReturn(gpxReaderIter);
        expect(gpxReaderIter.next()).andReturn(gpxReader);
        expect(gpxReaderIter.hasNext()).andReturn(false);

        PowerMock.replayAll();
        GpxAndZipFilesIter gpxFileIter = new GpxAndZipFilesIter(new String[] {
            "foo.gpx"
        }, gpxAndZipFilesIterFactory);
        assertTrue(gpxFileIter.hasNext());
        assertEquals(gpxReader, gpxFileIter.next());
        assertFalse(gpxFileIter.hasNext());
        PowerMock.verifyAll();
    }
}
