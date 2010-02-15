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

import static org.junit.Assert.*;

import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;
import com.google.code.geobeagle.Toaster.OneTimeToaster;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class PeggedCacheProviderTest {
    @Test
    public void testPegCachesNotPegged() {
        GeocacheList caches = PowerMock.createMock(GeocacheList.class);
        OneTimeToaster oneTimeToaster = PowerMock
                .createMock(OneTimeToaster.class);

        EasyMock.expect(caches.size()).andReturn(50);
        oneTimeToaster.showToast(false);

        PowerMock.replayAll();
        PeggedCacheProvider peggedCacheProvider = new PeggedCacheProvider(
                oneTimeToaster);
        assertEquals(caches, peggedCacheProvider.pegCaches(100, caches));
        peggedCacheProvider.showToastIfTooManyCaches();
        assertFalse(peggedCacheProvider.isTooManyCaches());
        PowerMock.verifyAll();
    }

    @Test
    public void testPegCachesPegged() {
        GeocacheList caches = PowerMock.createMock(GeocacheList.class);
        OneTimeToaster oneTimeToaster = PowerMock
                .createMock(OneTimeToaster.class);

        EasyMock.expect(caches.size()).andReturn(200);
        oneTimeToaster.showToast(true);

        PowerMock.replayAll();
        PeggedCacheProvider peggedCacheProvider = new PeggedCacheProvider(
                oneTimeToaster);
        assertEquals(GeocacheListPrecomputed.EMPTY, peggedCacheProvider
                .pegCaches(100, caches));
        peggedCacheProvider.showToastIfTooManyCaches();
        assertTrue(peggedCacheProvider.isTooManyCaches());
        PowerMock.verifyAll();
    }

}
