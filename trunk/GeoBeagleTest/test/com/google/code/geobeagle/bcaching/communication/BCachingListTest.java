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

package com.google.code.geobeagle.bcaching.communication;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingJSONArray;
import com.google.code.geobeagle.bcaching.communication.BCachingJSONObject;
import com.google.code.geobeagle.bcaching.communication.BCachingList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class BCachingListTest extends GeoBeagleTest {
    private BCachingJSONObject cacheList;

    @Before
    public void setUp() {
        cacheList = createMock(BCachingJSONObject.class);
    }

    @Test
    public void testGetCachesRead() throws BCachingException {
        BCachingJSONArray cacheListJSON = createMock(BCachingJSONArray.class);

        expect(cacheList.getJSONArray("data")).andReturn(cacheListJSON);
        expect(cacheListJSON.length()).andReturn(12);

        replayAll();
        assertEquals(12, new BCachingList(cacheList).getCachesRead());
        verifyAll();
    }

    @Test
    public void testGetTotalCount() throws BCachingException {
        expect(cacheList.getInt("totalCount")).andReturn(40);

        replayAll();
        assertEquals(40, new BCachingList(cacheList).getTotalCount());
        verifyAll();
    }

    @Test
    public void testGetNoCacheIds() throws BCachingException {
        BCachingJSONArray summary = createMock(BCachingJSONArray.class);

        expect(cacheList.getJSONArray("data")).andReturn(summary);
        expect(summary.length()).andReturn(0);

        replayAll();
        assertEquals("", new BCachingList(cacheList).getCacheIds());
        verifyAll();
    }

    @Test
    public void testGetOneCacheIds() throws BCachingException {
        BCachingJSONArray summary = createMock(BCachingJSONArray.class);
        BCachingJSONObject cacheObject = createMock(BCachingJSONObject.class);

        expect(cacheList.getJSONArray("data")).andReturn(summary);
        expect(summary.length()).andReturn(1);
        expect(summary.getJSONObject(0)).andReturn(cacheObject);
        expect(cacheObject.getInt("id")).andReturn(123);

        replayAll();
        assertEquals("123", new BCachingList(cacheList).getCacheIds());
        verifyAll();
    }

    @Test
    public void testGetTwoCacheIds() throws BCachingException {
        BCachingJSONArray summary = createMock(BCachingJSONArray.class);
        BCachingJSONObject cacheObject1 = createMock(BCachingJSONObject.class);
        BCachingJSONObject cacheObject2 = createMock(BCachingJSONObject.class);

        expect(cacheList.getJSONArray("data")).andReturn(summary);
        expect(summary.length()).andReturn(2);
        expect(summary.getJSONObject(0)).andReturn(cacheObject1);
        expect(cacheObject1.getInt("id")).andReturn(123);
        expect(summary.getJSONObject(1)).andReturn(cacheObject2);
        expect(cacheObject2.getInt("id")).andReturn(456);

        replayAll();
        assertEquals("123,456", new BCachingList(cacheList).getCacheIds());
        verifyAll();
    }
}
