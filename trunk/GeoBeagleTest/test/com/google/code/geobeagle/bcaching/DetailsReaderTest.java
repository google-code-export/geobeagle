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

package com.google.code.geobeagle.bcaching;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.bcaching.DetailsReader.WriterWrapperFactory;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImportHelper.BufferedReaderFactory;
import com.google.code.geobeagle.cachedetails.WriterWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

@RunWith(PowerMockRunner.class)
public class DetailsReaderTest extends GeoBeagleTest {

    @Test
    public void testGetCacheDetailsNoLines() throws BCachingException, IOException {
        Hashtable<String, String> params = new Hashtable<String, String>();
        BufferedReaderFactory bufferedReaderFactory = createMock(BufferedReaderFactory.class);
        BufferedReader bufferedReader = createMock(BufferedReader.class);
        WriterWrapperFactory writerWrapperFactory = createMock(WriterWrapperFactory.class);
        WriterWrapper writerWrapper = createMock(WriterWrapper.class);

        expect(bufferedReaderFactory.create(params)).andReturn(bufferedReader);
        expect(writerWrapperFactory.create("/sdcard/download/bcaching12.gpx")).andReturn(
                writerWrapper);
        expect(bufferedReader.readLine()).andReturn(null);
        writerWrapper.close();

        replayAll();
        new DetailsReader(params, bufferedReaderFactory, writerWrapperFactory).getCacheDetails(
                "GC123", 12);
        verifyAll();
    }

    @Test
    public void testGetCacheDetailsTwoLines() throws BCachingException, IOException {
        Hashtable<String, String> params = new Hashtable<String, String>();
        BufferedReaderFactory bufferedReaderFactory = createMock(BufferedReaderFactory.class);
        BufferedReader bufferedReader = createMock(BufferedReader.class);
        WriterWrapperFactory writerWrapperFactory = createMock(WriterWrapperFactory.class);
        WriterWrapper writerWrapper = createMock(WriterWrapper.class);

        expect(bufferedReaderFactory.create(params)).andReturn(bufferedReader);
        expect(writerWrapperFactory.create("/sdcard/download/bcaching12.gpx")).andReturn(
                writerWrapper);
        expect(bufferedReader.readLine()).andReturn("hi");
        writerWrapper.write("hi");
        expect(bufferedReader.readLine()).andReturn(null);
        writerWrapper.close();

        replayAll();
        new DetailsReader(params, bufferedReaderFactory, writerWrapperFactory).getCacheDetails(
                "GC123", 12);
        verifyAll();
    }

    @Test(expected = BCachingException.class)
    public void testGetCacheDetailsIOException() throws BCachingException, IOException {
        Hashtable<String, String> params = new Hashtable<String, String>();
        BufferedReaderFactory bufferedReaderFactory = createMock(BufferedReaderFactory.class);
        BufferedReader bufferedReader = createMock(BufferedReader.class);
        WriterWrapperFactory writerWrapperFactory = createMock(WriterWrapperFactory.class);
        expect(bufferedReaderFactory.create(params)).andReturn(bufferedReader);
        expect(writerWrapperFactory.create("/sdcard/download/bcaching12.gpx")).andThrow(
                new IOException());
        replayAll();
        new DetailsReader(params, bufferedReaderFactory, writerWrapperFactory).getCacheDetails(
                "GC123", 12);
        verifyAll();
    }
}
