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
import com.google.code.geobeagle.bcaching.BCachingCommunication.BCachingException;
import com.google.code.geobeagle.bcaching.BCachingListImportHelper.BCachingListFactory;
import com.google.code.geobeagle.bcaching.BCachingListImportHelper.BufferedReaderFactory;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

@RunWith(PowerMockRunner.class)
public class BCachingListImportHelperTest extends GeoBeagleTest {
    @Test
    public void testImportList() throws BCachingException, JSONException, IOException {
        BufferedReaderFactory bufferedReaderFactory = createMock(BufferedReaderFactory.class);
        BCachingListFactory bcachingListFactory = createMock(BCachingListFactory.class);
        BufferedReader bufferedReader = createMock(BufferedReader.class);
        BCachingList bcachingList = createMock(BCachingList.class);
        Hashtable<String, String> params = new Hashtable<String, String>();

        expect(bufferedReaderFactory.create(params)).andReturn(bufferedReader);
        expect(bufferedReader.readLine()).andReturn("a json line");
        expect(bufferedReader.readLine()).andReturn(null);
        expect(bcachingListFactory.create("a json line\n")).andReturn(bcachingList);

        replayAll();
        new BCachingListImportHelper(bufferedReaderFactory, bcachingListFactory).importList(params);
        verifyAll();
    }
    
    @Test
    public void testRaiseIOException() throws BCachingException, IOException {
        BufferedReaderFactory bufferedReaderFactory = createMock(BufferedReaderFactory.class);
        BCachingListFactory bcachingListFactory = createMock(BCachingListFactory.class);
        BufferedReader bufferedReader = createMock(BufferedReader.class);
        BCachingList bcachingList = createMock(BCachingList.class);
        Hashtable<String, String> params = new Hashtable<String, String>();

        expect(bufferedReaderFactory.create(params)).andReturn(bufferedReader);
        expect(bufferedReader.readLine()).andReturn("a json line");
        expect(bufferedReader.readLine()).andReturn(null);
        expect(bcachingListFactory.create("a json line\n")).andReturn(bcachingList);

        replayAll();
        new BCachingListImportHelper(bufferedReaderFactory, bcachingListFactory).importList(params);
        verifyAll();
    }
}
