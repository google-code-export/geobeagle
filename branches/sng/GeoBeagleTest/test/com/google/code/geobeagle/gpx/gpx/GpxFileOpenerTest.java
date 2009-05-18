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

package com.google.code.geobeagle.gpx.gpx;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.gpx.GpxAndZipFilesDI;
import com.google.code.geobeagle.gpx.IGpxReaderIter;
import com.google.code.geobeagle.gpx.GpxAndZipFilesDI.GpxAndZipFilesIterFactory;
import com.google.code.geobeagle.gpx.gpx.GpxFileOpener.GpxFileIter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpxAndZipFilesDI.GpxAndZipFilesIterFactory.class, GpxFileOpener.class
})
public class GpxFileOpenerTest {

    @Test
    public void testFromFile() throws Exception {
        GpxFileOpener gpxFileOpener = PowerMock.createStrictMock(GpxFileOpener.class);
        GpxFileIter gpxFileIterator = PowerMock.createStrictMock(GpxFileIter.class);

        PowerMock.expectNew(GpxFileOpener.class, "bar.gpx").andReturn(gpxFileOpener);
        expect(gpxFileOpener.iterator()).andReturn(gpxFileIterator);

        PowerMock.replayAll();
        assertEquals(gpxFileIterator, new GpxAndZipFilesDI.GpxAndZipFilesIterFactory(null).fromFile("bar.gpx"));
        PowerMock.verifyAll();
    }

    @Test
    public void testIter() throws Exception {
        GpxReader gpxReader = PowerMock.createMock(GpxReader.class);
        PowerMock.expectNew(GpxReader.class, "foo.gpx").andReturn(gpxReader);

        PowerMock.replayAll();
        GpxFileOpener gpxFileOpener = new GpxFileOpener("foo.gpx");
        IGpxReaderIter iter = gpxFileOpener.iterator();
        assertTrue(iter.hasNext());
        assertEquals(gpxReader, iter.next());
        assertFalse(iter.hasNext());
        PowerMock.verifyAll();
    }

}
