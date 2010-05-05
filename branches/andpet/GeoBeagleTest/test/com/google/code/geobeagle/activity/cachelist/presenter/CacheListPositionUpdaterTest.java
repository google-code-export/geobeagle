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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.database.CachesProviderCenterThread;
import com.google.code.geobeagle.database.ICachesProviderCenter;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CacheListPositionUpdaterTest {

    @Test
    public void testRefreshNullLocation() {
        GeoFixProvider geoFixProvider = PowerMock
                .createMock(GeoFixProvider.class);

        EasyMock.expect(geoFixProvider.getLocation()).andReturn(null);

        new CacheListPositionUpdater(geoFixProvider, null, null, null)
                .refresh();
    }

    @Test
    public void testRefresh() {
        GeoFixProvider geoFixProvider = PowerMock
                .createMock(GeoFixProvider.class);
        GeoFix geoFix = PowerMock.createMock(GeoFix.class);
        ICachesProviderCenter cachesProviderCenter = PowerMock
                .createMock(ICachesProviderCenter.class);
        CachesProviderCenterThread sortCenterThread = PowerMock
                .createMock(CachesProviderCenterThread.class);
        CacheListAdapter cacheListAdapter = PowerMock
                .createMock(CacheListAdapter.class);

        EasyMock.expect(geoFixProvider.getLocation()).andReturn(geoFix);
        EasyMock.expect(geoFix.getLatitude()).andReturn(122.0);
        EasyMock.expect(geoFix.getLongitude()).andReturn(-37.0);
        cachesProviderCenter.setCenter(122.0, -37.0);
        sortCenterThread.setCenter(122.0, -37.0, cacheListAdapter);

        PowerMock.replayAll();
        new CacheListPositionUpdater(geoFixProvider, cacheListAdapter,
                cachesProviderCenter, sortCenterThread).refresh();
        PowerMock.verifyAll();
    }

    @Test
    public void testForceRefresh() {
        GeoFixProvider geoFixProvider = PowerMock
                .createMock(GeoFixProvider.class);

        EasyMock.expect(geoFixProvider.getLocation()).andReturn(null);

        new CacheListPositionUpdater(geoFixProvider, null, null, null)
                .forceRefresh();
    }

}
