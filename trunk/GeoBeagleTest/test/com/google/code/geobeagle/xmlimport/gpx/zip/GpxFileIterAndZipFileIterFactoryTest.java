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

import com.google.code.geobeagle.gpx.zip.ZipInputStreamFactory;
import com.google.code.geobeagle.xmlimport.AbortState;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.code.geobeagle.xmlimport.gpx.GpxFileIterAndZipFileIterFactory;
import com.google.code.geobeagle.xmlimport.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.gpx.GpxFileOpener.GpxFileIter;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipFileIter;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipInputFileTester;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    GpxFileIterAndZipFileIterFactory.class
})
public class GpxFileIterAndZipFileIterFactoryTest {

    private GeoBeagleEnvironment geoBeagleEnvironment;
    private AbortState aborter;
    private GpxFileIter gpxFileIterator;
    private Provider<AbortState> aborterProvider;

    @Before
    public void setUp() {
        geoBeagleEnvironment = PowerMock.createMock(GeoBeagleEnvironment.class);
        aborter = PowerMock.createMock(AbortState.class);
        gpxFileIterator = PowerMock.createStrictMock(GpxFileIter.class);
        aborterProvider = PowerMock.createMock(Provider.class);
    }

    @Test
    public void testFactoryFromGpxFile() throws Exception {
        GpxFileOpener gpxFileOpener = PowerMock.createStrictMock(GpxFileOpener.class);

        expect(geoBeagleEnvironment.getImportFolder()).andReturn("/sdcard/download/");

        PowerMock.expectNew(GpxFileOpener.class, "/sdcard/download/foo.gpx", aborterProvider)
                .andReturn(gpxFileOpener);
        expect(gpxFileOpener.iterator()).andReturn(gpxFileIterator);

        PowerMock.replayAll();
        assertEquals(gpxFileIterator, new GpxFileIterAndZipFileIterFactory(null, aborterProvider,
                geoBeagleEnvironment).fromFile("foo.gpx"));
        PowerMock.verifyAll();
    }

    @Test
    public void testFactoryFromZipFile() throws Exception {
        ZipFileOpener zipFileOpener = PowerMock.createStrictMock(ZipFileOpener.class);
        ZipFileIter zipFileIterator = PowerMock.createStrictMock(ZipFileIter.class);
        ZipInputStreamFactory zipInputStreamFactory = PowerMock
                .createMock(ZipInputStreamFactory.class);
        ZipInputFileTester zipInputFileTester = PowerMock.createMock(ZipInputFileTester.class);

        expect(geoBeagleEnvironment.getImportFolder()).andReturn("/sdcard/download/");
        PowerMock.expectNew(ZipInputStreamFactory.class).andReturn(zipInputStreamFactory);
        PowerMock.expectNew(ZipFileOpener.class, "/sdcard/download/foo.zip", zipInputStreamFactory,
                zipInputFileTester, aborterProvider).andReturn(zipFileOpener);
        expect(zipFileOpener.iterator()).andReturn(zipFileIterator);

        PowerMock.replayAll();
        assertEquals(zipFileIterator, new GpxFileIterAndZipFileIterFactory(zipInputFileTester,
                aborterProvider, geoBeagleEnvironment).fromFile("foo.zip"));
        PowerMock.verifyAll();
    }

    @Test
    public void testFactoryResetAborter() throws Exception {
        expect(aborterProvider.get()).andReturn(aborter);
        aborter.reset();

        PowerMock.replayAll();
        new GpxFileIterAndZipFileIterFactory(null, aborterProvider, null).resetAborter();
        PowerMock.verifyAll();
    }
}
