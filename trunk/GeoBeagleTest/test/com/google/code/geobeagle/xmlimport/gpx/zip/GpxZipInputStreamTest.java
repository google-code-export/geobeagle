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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RunWith(PowerMockRunner.class)
public class GpxZipInputStreamTest {

    @Test
    public void testGetStream() throws Exception {
        ZipInputStream zipInputStream = PowerMock.createMock(ZipInputStream.class);

        PowerMock.replayAll();
        assertEquals(zipInputStream, new GpxZipInputStream(zipInputStream).getStream());
        PowerMock.verifyAll();
    }

    @Test
    public void testNone() throws Exception {
        ZipInputStream zipInputStream = PowerMock.createMock(ZipInputStream.class);

        expect(zipInputStream.getNextEntry()).andReturn(null);

        PowerMock.replayAll();
        GpxZipInputStream zis = new GpxZipInputStream(zipInputStream);
        assertEquals(null, zis.getNextEntry());
        PowerMock.verifyAll();
    }

    @Test
    public void testOne() throws Exception {
        ZipInputStream zipInputStream = PowerMock.createMock(ZipInputStream.class);
        ZipEntry zipEntry = PowerMock.createMock(ZipEntry.class);

        expect(zipInputStream.getNextEntry()).andReturn(zipEntry);

        PowerMock.replayAll();
        GpxZipInputStream zis = new GpxZipInputStream(zipInputStream);
        assertEquals(zipEntry, zis.getNextEntry());
        PowerMock.verifyAll();
    }
}
