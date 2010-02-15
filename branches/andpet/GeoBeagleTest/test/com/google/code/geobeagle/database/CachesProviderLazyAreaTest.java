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

import static com.google.code.geobeagle.Common.mockGeocache;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;
import com.google.code.geobeagle.database.CachesProviderLazyArea.CoordinateManager;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CachesProviderLazyAreaTest {
    CachesProviderStub mCachesProviderStub;

    @Before
    public void setUp() {
        mCachesProviderStub = new CachesProviderStub();
    }

    @Test
    public void testGetCountCachesChanges() {
        CoordinateManager coordinateManager = PowerMock
                .createMock(CoordinateManager.class);
        Geocache cache1 = mockGeocache(1, 1);
        Geocache cache2 = mockGeocache(2, 2);
        PeggedCacheProvider peggedCacheProvider = PowerMock
                .createMock(PeggedCacheProvider.class);

        EasyMock.expect(peggedCacheProvider.isTooManyCaches()).andReturn(false);
        EasyMock.expect(coordinateManager.atLeastOneSideIsSmaller(0, 0, 5, 5))
                .andReturn(false);
        GeocacheList caches = mCachesProviderStub.getCaches();
        EasyMock.expect(peggedCacheProvider.pegCaches(1000, caches)).andReturn(
                caches).anyTimes();
        mCachesProviderStub.addCache(cache1);

        PowerMock.replayAll();
        CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(
                mCachesProviderStub, peggedCacheProvider, coordinateManager);
        lazyArea.setBounds(0, 0, 5, 5);
        assertEquals(1, lazyArea.getCount());
        lazyArea.resetChanged();
        mCachesProviderStub.addCache(cache2);
        assertEquals(2, lazyArea.getCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testClearBounds() {
        ICachesProviderArea area = PowerMock
                .createMock(ICachesProviderArea.class);

        area.clearBounds();

        PowerMock.replayAll();
        new CachesProviderLazyArea(area, null, null).clearBounds();
        PowerMock.verifyAll();
    }

    @Test
    public void testGetCaches() {
        ICachesProviderArea area = PowerMock
                .createMock(ICachesProviderArea.class);
        GeocacheList cacheList = PowerMock.createMock(GeocacheList.class);
        PeggedCacheProvider peggedCacheProvider = PowerMock
                .createMock(PeggedCacheProvider.class);
        GeocacheList peggedCacheList = PowerMock.createMock(GeocacheList.class);

        EasyMock.expect(area.getCaches(CachesProviderLazyArea.MAX_COUNT + 1))
                .andReturn(cacheList);
        area.resetChanged();
        EasyMock.expect(
                peggedCacheProvider.pegCaches(CachesProviderLazyArea.MAX_COUNT,
                        cacheList)).andReturn(peggedCacheList);

        PowerMock.replayAll();
        CachesProviderLazyArea cachesProviderLazyArea = new CachesProviderLazyArea(
                area, peggedCacheProvider, null);
        assertEquals(peggedCacheList, cachesProviderLazyArea.getCaches());
        PowerMock.verifyAll();
    }

    @Test
    public void testEverySideIsBigger() {
        CoordinateManager coordinateManager = new CoordinateManager(0);

        assertTrue(coordinateManager.everySideIsBigger(0, 0, 0, 0));
        assertFalse(coordinateManager.everySideIsBigger(1, 0, 0, 0));
    }

    @Test
    public void testSetBoundsBigger() {
        CoordinateManager coordinateManager = PowerMock
                .createMock(CoordinateManager.class);
        ICachesProviderArea cachesProviderArea = PowerMock
                .createMock(ICachesProviderArea.class);
        PeggedCacheProvider peggedCacheProvider = PowerMock
                .createMock(PeggedCacheProvider.class);

        EasyMock.expect(peggedCacheProvider.isTooManyCaches()).andReturn(true);
        EasyMock.expect(coordinateManager.everySideIsBigger(0, 0, 5, 5))
                .andReturn(true);

        PowerMock.replayAll();
        CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(
                cachesProviderArea, peggedCacheProvider, coordinateManager);
        lazyArea.setBounds(0, 0, 5, 5);
        PowerMock.verifyAll();
    }

    @Test
    public void testGetCachesTooMany() {
        ICachesProviderArea area = PowerMock
                .createMock(ICachesProviderArea.class);
        GeocacheList cacheList = PowerMock.createMock(GeocacheList.class);
        PeggedCacheProvider peggedCacheProvider = PowerMock
                .createMock(PeggedCacheProvider.class);

        EasyMock.expect(area.getCaches(1001)).andReturn(cacheList);
        area.resetChanged();
        EasyMock.expect(peggedCacheProvider.pegCaches(1000, cacheList))
                .andReturn(GeocacheListPrecomputed.EMPTY);

        PowerMock.replayAll();
        CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(area,
                peggedCacheProvider, null);
        assertEquals(GeocacheListPrecomputed.EMPTY, lazyArea.getCaches(1000));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetTotalCount() {
        ICachesProviderArea area = PowerMock
                .createMock(ICachesProviderArea.class);

        EasyMock.expect(area.getTotalCount()).andReturn(27);

        PowerMock.replayAll();
        CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(area,
                null, null);
        assertEquals(27, lazyArea.getTotalCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testHasChanged() {
        mCachesProviderStub.setChanged(false);

        CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(
                mCachesProviderStub, null, null);
        assertTrue(lazyArea.hasChanged());
        lazyArea.resetChanged();
        assertFalse(lazyArea.hasChanged());
        mCachesProviderStub.setChanged(true);
        assertTrue(lazyArea.hasChanged());
        lazyArea.resetChanged();
        assertFalse(lazyArea.hasChanged());
    }

    @Test
    public void testSmallerArea() {
        PeggedCacheProvider peggedCacheProvider = new PeggedCacheProvider(null);
        CoordinateManager coordinateManager = new CoordinateManager(0);

        PowerMock.replayAll();
        CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(
                mCachesProviderStub, peggedCacheProvider, coordinateManager);
        lazyArea.setBounds(0, 0, 5, 5);
        lazyArea.getCaches();
        assertTrue(lazyArea.hasChanged());
        assertEquals(1, mCachesProviderStub.getSetBoundsCalls());

        lazyArea.resetChanged();
        lazyArea.setBounds(0, 1, 4, 5);
        lazyArea.getCaches();
        // No more calls to setBounds
        assertEquals(1, mCachesProviderStub.getSetBoundsCalls());
        assertFalse(lazyArea.hasChanged());
        PowerMock.verifyAll();
    }

    @Test
    public void testShowToastIfTooManyCaches() {
        PeggedCacheProvider peggedCacheProvider = PowerMock
                .createMock(PeggedCacheProvider.class);
        
        peggedCacheProvider.showToastIfTooManyCaches();

        PowerMock.replayAll();
        CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(null,
                peggedCacheProvider, null);
        lazyArea.showToastIfTooManyCaches();
        PowerMock.verifyAll();
    }

    @Test
    public void testTooManyCaches() {
        PeggedCacheProvider peggedCacheProvider = PowerMock
                .createMock(PeggedCacheProvider.class);
        
        EasyMock.expect(peggedCacheProvider.isTooManyCaches()).andReturn(true);

        PowerMock.replayAll();
        CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(null,
                peggedCacheProvider, null);
        assertTrue(lazyArea.tooManyCaches());
        PowerMock.verifyAll();
    }
    
}
