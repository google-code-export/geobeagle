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

package com.google.code.geobeagle.xmlimport.gpx.gpx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    GpxFileOpener.class
})
public class GpxFileOpenerTest {

    @Test
    public void testIter_HasNext() throws Exception {
        Aborter aborter = PowerMock.createMock(Aborter.class);

        EasyMock.expect(aborter.isAborted()).andReturn(false);

        PowerMock.replayAll();
        assertTrue(new GpxFileOpener("foo.gpx", aborter).iterator().hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testIter_HasNextFalse() throws Exception {
        Aborter aborter = PowerMock.createMock(Aborter.class);

        EasyMock.expect(aborter.isAborted()).andReturn(false);

        PowerMock.replayAll();
        assertFalse(new GpxFileOpener(null, aborter).iterator().hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testIter_HasNextAborted() throws Exception {
        Aborter aborter = PowerMock.createMock(Aborter.class);

        EasyMock.expect(aborter.isAborted()).andReturn(true);

        PowerMock.replayAll();
        assertFalse(new GpxFileOpener("foo.gpx", aborter).iterator().hasNext());
        PowerMock.verifyAll();
    }

    @Test
    public void testIter_Next() throws Exception {
        GpxReader gpxReader = PowerMock.createMock(GpxReader.class);

        PowerMock.expectNew(GpxReader.class, "foo.gpx").andReturn(gpxReader);

        PowerMock.replayAll();
        assertEquals(gpxReader, new GpxFileOpener("foo.gpx", null).iterator().next());
        PowerMock.verifyAll();
    }

    @Test
    public void testIterHasNext_Aborted() throws Exception {
        Aborter aborter = PowerMock.createMock(Aborter.class);

        EasyMock.expect(aborter.isAborted()).andReturn(true);

        PowerMock.replayAll();
        GpxFileOpener gpxFileOpener = new GpxFileOpener("foo.gpx", aborter);
        assertFalse(gpxFileOpener.iterator().hasNext());
        PowerMock.verifyAll();
    }
}
