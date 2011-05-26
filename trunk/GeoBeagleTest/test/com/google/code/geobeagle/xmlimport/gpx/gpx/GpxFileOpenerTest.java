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
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.xmlimport.AbortState;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    GpxFileOpener.class
})
public class GpxFileOpenerTest {

    private Provider<AbortState> aborterProvider;
    private AbortState abortState;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        aborterProvider = createMock(Provider.class);
        abortState = createMock(AbortState.class);
    }

    @Test
    public void testIter_HasNext() throws Exception {
        EasyMock.expect(aborterProvider.get()).andReturn(abortState);
        EasyMock.expect(abortState.isAborted()).andReturn(false);

        replayAll();
        assertTrue(new GpxFileOpener("foo.gpx", aborterProvider).iterator().hasNext());
        verifyAll();
    }

    @Test
    public void testIter_HasNextFalse() throws Exception {
        EasyMock.expect(aborterProvider.get()).andReturn(abortState);
        EasyMock.expect(abortState.isAborted()).andReturn(false);

        replayAll();
        assertFalse(new GpxFileOpener(null, aborterProvider).iterator().hasNext());
        verifyAll();
    }

    @Test
    public void testIter_HasNextAborted() throws Exception {
        EasyMock.expect(aborterProvider.get()).andReturn(abortState);

        EasyMock.expect(abortState.isAborted()).andReturn(true);

        replayAll();
        assertFalse(new GpxFileOpener("foo.gpx", aborterProvider).iterator().hasNext());
        verifyAll();
    }

    @Test
    public void testIter_Next() throws Exception {
        GpxReader gpxReader = createMock(GpxReader.class);

        expectNew(GpxReader.class, "foo.gpx").andReturn(gpxReader);

        replayAll();
        assertEquals(gpxReader, new GpxFileOpener("foo.gpx", null).iterator().next());
        verifyAll();
    }

    @Test
    public void testIterHasNext_Aborted() throws Exception {
        EasyMock.expect(aborterProvider.get()).andReturn(abortState);
        EasyMock.expect(abortState.isAborted()).andReturn(true);

        replayAll();
        GpxFileOpener gpxFileOpener = new GpxFileOpener("foo.gpx", aborterProvider);
        assertFalse(gpxFileOpener.iterator().hasNext());
        verifyAll();
    }
}
