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

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.gpx.zip.GpxReader;

import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.Reader;

public class GpxReaderTest {

    @Test
    public void testGetFilename() throws Exception {
        Reader reader = PowerMock.createMock(Reader.class);

        assertEquals("foo.gpx", new GpxReader("foo.gpx", reader).getFilename());
    }

    @Test
    public void GpxFileOpenedOpen() throws Exception {
        Reader reader = PowerMock.createMock(Reader.class);
        assertEquals(reader, new GpxReader("foo.gpx", reader).open());
    }
}
