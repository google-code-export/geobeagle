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

package com.google.code.geobeagle.xmlimport.gpx.zip;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.gpx.zip.ZipInputStreamFactory;
import com.google.code.geobeagle.xmlimport.AbortState;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipFileIter;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipInputFileTester;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        ZipFileIter.class, ZipFileOpener.class
})
public class ZipFileOpenerTest {
    private AbortState aborter;
    private Provider<AbortState> aborterProvider;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        aborter = PowerMock.createMock(AbortState.class);
        aborterProvider = PowerMock.createMock(Provider.class);
    }

    @Test
    public void testZipFileIter_HasNextAborted() throws Exception {
        expect(aborterProvider.get()).andReturn(aborter);
        expect(aborter.isAborted()).andReturn(true);

        PowerMock.replayAll();
        assertFalse(new ZipFileIter(null, aborterProvider, null).hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testZipFileIter_HasNextNone() throws Exception {
        GpxZipInputStream zipInputStream = PowerMock.createMock(GpxZipInputStream.class);
        expect(aborterProvider.get()).andReturn(aborter);

        expect(aborter.isAborted()).andReturn(false);
        expect(zipInputStream.getNextEntry()).andReturn(null);

        PowerMock.replayAll();
        assertFalse(new ZipFileIter(zipInputStream, aborterProvider, null).hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testZipFileIter_HasNextTrue() throws Exception {
        GpxZipInputStream zipInputStream = PowerMock.createMock(GpxZipInputStream.class);
        ZipEntry zipEntry = PowerMock.createMock(ZipEntry.class);
        ZipInputFileTester zipInputFileTester = PowerMock.createMock(ZipInputFileTester.class);

        expect(aborterProvider.get()).andReturn(aborter);
        expect(aborter.isAborted()).andReturn(false);
        expect(zipInputStream.getNextEntry()).andReturn(zipEntry);
        expect(zipInputFileTester.isValid(zipEntry)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new ZipFileIter(zipInputStream, aborterProvider, zipInputFileTester, null)
                .hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testZipFileIter_Next() throws Exception {
        GpxReader gpxReader = PowerMock.createMock(GpxReader.class);
        GpxZipInputStream zipInputStream = PowerMock.createMock(GpxZipInputStream.class);
        ZipEntry zipEntry = PowerMock.createMock(ZipEntry.class);
        InputStreamReader inputStreamReader = PowerMock.createMock(InputStreamReader.class);
        InputStream inputStream = PowerMock.createMock(InputStream.class);
        ZipInputFileTester zipInputFileTester = PowerMock.createMock(ZipInputFileTester.class);

        expect(zipEntry.getName()).andReturn("foo.gpx");
        expect(zipInputStream.getStream()).andReturn(inputStream);
        PowerMock.expectNew(InputStreamReader.class, inputStream).andReturn(inputStreamReader);
        PowerMock.expectNew(GpxReader.class, "foo.gpx", inputStreamReader).andReturn(gpxReader);

        PowerMock.replayAll();
        ZipFileIter iter = new ZipFileIter(zipInputStream, aborterProvider, zipInputFileTester,
                zipEntry);
        assertEquals(gpxReader, iter.next());
        PowerMock.verifyAll();
    }

    @Test
    public void testZipFileOpener_Iterator() throws Exception {
        ZipInputStreamFactory zipInputStreamFactory = PowerMock
                .createMock(ZipInputStreamFactory.class);
        GpxZipInputStream zipInputStream = PowerMock.createMock(GpxZipInputStream.class);
        ZipFileIter zipFileIter = PowerMock.createMock(ZipFileIter.class);

        expect(zipInputStreamFactory.create("foo.zip")).andReturn(zipInputStream);
        PowerMock.expectNew(ZipFileIter.class, zipInputStream, null, null).andReturn(zipFileIter);

        PowerMock.replayAll();
        assertEquals(zipFileIter, new ZipFileOpener("foo.zip", zipInputStreamFactory, null, null)
                .iterator());
        PowerMock.verifyAll();
    }

    @Test
    public void testZipInputFileTester_Directory() throws Exception {
        ZipEntry zipEntryDir = PowerMock.createMock(ZipEntry.class);
        GpxFilenameFilter gpxFilenameFilter = PowerMock.createMock(GpxFilenameFilter.class);

        expect(zipEntryDir.isDirectory()).andReturn(true);

        PowerMock.replayAll();
        assertFalse(new ZipInputFileTester(gpxFilenameFilter).isValid(zipEntryDir));
        PowerMock.verifyAll();
    }

    @Test
    public void testZipInputFileTester_Gpx() throws Exception {
        ZipEntry zipEntryGpx = PowerMock.createMock(ZipEntry.class);
        GpxFilenameFilter gpxFilenameFilter = PowerMock.createMock(GpxFilenameFilter.class);

        expect(zipEntryGpx.isDirectory()).andReturn(false);
        expect(zipEntryGpx.getName()).andReturn("foo.gpx");
        expect(gpxFilenameFilter.accept("foo.gpx")).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new ZipInputFileTester(gpxFilenameFilter).isValid(zipEntryGpx));
        PowerMock.verifyAll();
    }

    @Test
    public void testZipInputFileTester_NonGpx() throws Exception {
        ZipEntry zipEntryBad = PowerMock.createMock(ZipEntry.class);
        GpxFilenameFilter gpxFilenameFilter = PowerMock.createMock(GpxFilenameFilter.class);

        expect(zipEntryBad.isDirectory()).andReturn(false);
        expect(zipEntryBad.getName()).andReturn("foo.txt");
        expect(gpxFilenameFilter.accept("foo.txt")).andReturn(false);

        PowerMock.replayAll();
        assertFalse(new ZipInputFileTester(gpxFilenameFilter).isValid(zipEntryBad));
        PowerMock.verifyAll();
    }

}
