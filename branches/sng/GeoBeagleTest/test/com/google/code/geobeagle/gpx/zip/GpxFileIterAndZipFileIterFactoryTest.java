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

package com.google.code.geobeagle.gpx.zip;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.gpx.GpxFileIterAndZipFileIterFactory;
import com.google.code.geobeagle.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.gpx.gpx.GpxFileOpener.GpxFileIter;
import com.google.code.geobeagle.gpx.zip.ZipFileOpener.ZipFileIter;
import com.google.code.geobeagle.gpx.zip.ZipFileOpener.ZipInputFileTester;
import com.google.code.geobeagle.io.GpxToCache.Aborter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    GpxFileIterAndZipFileIterFactory.class
})
public class GpxFileIterAndZipFileIterFactoryTest {

    @Test
    public void testFactoryFromGpxFile() throws Exception {
        GpxFileOpener gpxFileOpener = PowerMock.createStrictMock(GpxFileOpener.class);
        GpxFileIter zipFileIterator = PowerMock.createStrictMock(GpxFileIter.class);
        Aborter aborter = PowerMock.createMock(Aborter.class);

        PowerMock.expectNew(GpxFileOpener.class, GpxAndZipFiles.GPX_DIR + "foo.gpx", aborter)
                .andReturn(gpxFileOpener);
        expect(gpxFileOpener.iterator()).andReturn(zipFileIterator);

        PowerMock.replayAll();
        assertEquals(zipFileIterator, new GpxFileIterAndZipFileIterFactory(null, aborter)
                .fromFile("foo.gpx"));
        PowerMock.verifyAll();
    }

    @Test
    public void testFactoryFromZipFile() throws Exception {
        ZipFileOpener zipFileOpener = PowerMock.createStrictMock(ZipFileOpener.class);
        ZipFileIter zipFileIterator = PowerMock.createStrictMock(ZipFileIter.class);
        ZipInputStreamFactory zipInputStreamFactory = PowerMock
                .createMock(ZipInputStreamFactory.class);
        ZipInputFileTester zipInputFileTester = PowerMock.createMock(ZipInputFileTester.class);
        Aborter aborter = PowerMock.createMock(Aborter.class);

        PowerMock.expectNew(ZipInputStreamFactory.class).andReturn(zipInputStreamFactory);
        PowerMock.expectNew(ZipFileOpener.class, GpxAndZipFiles.GPX_DIR + "foo.zip",
                zipInputStreamFactory, zipInputFileTester, aborter).andReturn(zipFileOpener);
        expect(zipFileOpener.iterator()).andReturn(zipFileIterator);

        PowerMock.replayAll();
        assertEquals(zipFileIterator, new GpxFileIterAndZipFileIterFactory(zipInputFileTester,
                aborter).fromFile("foo.zip"));
        PowerMock.verifyAll();
    }

    @Test
    public void testFactoryResetAborter() throws Exception {
        Aborter aborter = PowerMock.createMock(Aborter.class);

        aborter.reset();

        PowerMock.replayAll();
        new GpxFileIterAndZipFileIterFactory(null, aborter).resetAborter();
        PowerMock.verifyAll();
    }
}
