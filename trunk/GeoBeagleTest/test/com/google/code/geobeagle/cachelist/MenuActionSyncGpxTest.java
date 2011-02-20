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

package com.google.code.geobeagle.cachelist;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.xmlimport.CacheSyncer;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionSyncGpxTest extends GeoBeagleTest {
    private Provider<CacheSyncer> cacheSyncerProvider;
    private CacheSyncer cacheSyncer;
    private MenuActionSyncGpx menuActionSyncGpx;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        cacheSyncerProvider = createMock(Provider.class);
        cacheSyncer = createMock(CacheSyncer.class);
        menuActionSyncGpx = new MenuActionSyncGpx(cacheSyncerProvider);
    }

    @Test
    public void testAct() {
        expect(cacheSyncerProvider.get()).andReturn(cacheSyncer);
        cacheSyncer.syncGpxs();

        replayAll();
        menuActionSyncGpx.act();
        verifyAll();
    }

    @Test
    public void testAbort() {
        expect(cacheSyncerProvider.get()).andReturn(cacheSyncer);
        cacheSyncer.syncGpxs();
        cacheSyncer.abort();

        replayAll();
        menuActionSyncGpx.act();
        menuActionSyncGpx.abort();
        verifyAll();
    }
}
