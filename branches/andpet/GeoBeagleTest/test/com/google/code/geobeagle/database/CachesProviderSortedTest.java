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

package com.google.code.geobeagle.database;

import static org.junit.Assert.assertEquals;
import static com.google.code.geobeagle.Common.mockGeocache;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheListPrecomputed;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class CachesProviderSortedTest {

    CachesProviderStub mProvider;
    ArrayList<Geocache> mSorted;

    @Before
    public void setUp() {
        mSorted = new ArrayList<Geocache>();
        Geocache cache1 = mockGeocache(1, 1);
        Geocache cache2 = mockGeocache(1, 2);
        EasyMock.expect(cache1.getDistanceTo(0.0, 0.0)).andReturn(1.4f);
        EasyMock.expect(cache2.getDistanceTo(0.0, 0.0)).andReturn(2.2f);
        mSorted.add(cache1);
        mSorted.add(cache2);
        
        mProvider = new CachesProviderStub();
        mProvider.addCache(cache2);
        mProvider.addCache(cache1);
    }
    
    @Test
    public void testUnsortedAtStart() {
        PowerMock.replayAll();

        CachesProviderSorted sorted = new CachesProviderSorted(mProvider);
        assertEquals(mProvider.getCaches(), sorted.getCaches());
    }

    @Test
    public void testSortedAfterSetCenter() {
        PowerMock.replayAll();

        CachesProviderSorted sorted = new CachesProviderSorted(mProvider);
        sorted.setCenter(0, 0);
        assertEquals(new GeocacheListPrecomputed(mSorted), sorted.getCaches());
    }
}
