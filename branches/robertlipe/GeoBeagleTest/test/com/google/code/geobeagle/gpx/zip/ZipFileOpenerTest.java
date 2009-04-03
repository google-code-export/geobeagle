
package com.google.code.geobeagle.gpx.zip;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilesIterFactory;
import com.google.code.geobeagle.gpx.zip.ZipFileOpener.ZipFileIter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        ZipFileIter.class, ZipFileOpener.class, GpxAndZipFilesIterFactory.class
})
public class ZipFileOpenerTest {

    @Test
    public void testHasNextNone() throws Exception {
        GpxZipInputStream zipInputStream = PowerMock.createMock(GpxZipInputStream.class);

        expect(zipInputStream.getNextEntry()).andReturn(null);

        PowerMock.replayAll();
        ZipFileIter iter = new ZipFileIter(zipInputStream);
        assertFalse(iter.hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testNextNone() throws Exception {
        GpxZipInputStream zipInputStream = PowerMock.createMock(GpxZipInputStream.class);

        expect(zipInputStream.getNextEntry()).andReturn(null);

        PowerMock.replayAll();
        ZipFileIter iter = new ZipFileIter(zipInputStream);
        assertEquals(null, iter.next());
        PowerMock.verifyAll();
    }

    @Test
    public void testOne() throws Exception {
        GpxReader gpxReader = PowerMock.createMock(GpxReader.class);
        GpxZipInputStream zipInputStream = PowerMock.createMock(GpxZipInputStream.class);
        ZipEntry zipEntry = PowerMock.createMock(ZipEntry.class);
        InputStreamReader inputStreamReader = PowerMock.createMock(InputStreamReader.class);
        InputStream inputStream = PowerMock.createMock(InputStream.class);

        expect(zipInputStream.getNextEntry()).andReturn(zipEntry);
        expect(zipEntry.getName()).andReturn("foo.gpx");
        expect(zipInputStream.getStream()).andReturn(inputStream);
        PowerMock.expectNew(InputStreamReader.class, inputStream).andReturn(inputStreamReader);
        PowerMock.expectNew(GpxReader.class, "foo.gpx", inputStreamReader).andReturn(gpxReader);
        expect(zipInputStream.getNextEntry()).andReturn(null);

        PowerMock.replayAll();
        ZipFileIter iter = new ZipFileIter(zipInputStream);
        assertTrue(iter.hasNext());
        assertEquals(gpxReader, iter.next());
        assertFalse(iter.hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testIterator() throws Exception {
        ZipInputStreamFactory zipInputStreamFactory = PowerMock
                .createMock(ZipInputStreamFactory.class);
        GpxZipInputStream zipInputStream = PowerMock.createMock(GpxZipInputStream.class);
        ZipFileIter zipFileIter = PowerMock.createMock(ZipFileIter.class);

        expect(zipInputStreamFactory.create("foo.zip")).andReturn(zipInputStream);
        PowerMock.expectNew(ZipFileIter.class, zipInputStream).andReturn(zipFileIter);

        PowerMock.replayAll();
        assertEquals(zipFileIter, new ZipFileOpener("foo.zip", zipInputStreamFactory).iterator());
        PowerMock.verifyAll();
    }

    @Test
    public void testFactoryFromZipFile() throws Exception {
        ZipFileOpener zipFileOpener = PowerMock.createStrictMock(ZipFileOpener.class);
        ZipFileIter gpxFileIterator = PowerMock.createStrictMock(ZipFileIter.class);
        ZipInputStreamFactory zipInputStreamFactory = PowerMock
                .createMock(ZipInputStreamFactory.class);

        PowerMock.expectNew(ZipInputStreamFactory.class).andReturn(zipInputStreamFactory);
        PowerMock.expectNew(ZipFileOpener.class, GpxAndZipFiles.GPX_DIR + "foo.zip",
                zipInputStreamFactory).andReturn(zipFileOpener);
        expect(zipFileOpener.iterator()).andReturn(gpxFileIterator);

        PowerMock.replayAll();
        assertEquals(gpxFileIterator, new GpxAndZipFilesIterFactory().fromFile("foo.zip"));
        PowerMock.verifyAll();
    }

}
