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

package com.google.code.geobeagle.activity.map;

import static org.junit.Assert.*;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection;
import com.google.code.geobeagle.activity.map.OverlayManager.OverlaySelector;
import com.google.code.geobeagle.database.CachesProviderDb;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Log.class, OverlayManager.class, MapView.class, GeoMapView.class
})
public class OverlayManagerTest {

    @Before
    public void mockLogging() {
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(
                Log.d((String)EasyMock.anyObject(), (String)EasyMock
                        .anyObject())).andReturn(0).anyTimes();
    }

    @Test
    public void testForceRefresh() {
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);
        FilterTypeCollection filterTypeCollection = PowerMock
                .createMock(FilterTypeCollection.class);
        CacheFilter cacheFilter = PowerMock.createMock(CacheFilter.class);
        CachesProviderDb cachesProviderDb = PowerMock
                .createMock(CachesProviderDb.class);
        OverlaySelector overlaySelector = PowerMock
                .createMock(OverlaySelector.class);

        final OverlayManager overlayManager = new OverlayManager(geoMapView,
                null, null, null, false, cachesProviderDb,
                filterTypeCollection, overlaySelector);
        EasyMock.expect(filterTypeCollection.getActiveFilter()).andReturn(
                cacheFilter);
        cachesProviderDb.setFilter(cacheFilter);
        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(5);
        EasyMock.expect(
                overlaySelector.selectOverlay(overlayManager, 5, false, null,
                        null, null)).andReturn(true);
        geoMapView.invalidate();

        PowerMock.replayAll();
        overlayManager.forceRefresh();
        assertTrue(overlayManager.usesDensityMap());
        PowerMock.verifyAll();
    }

    @Test
    public void testSelectOverlay() {
        OverlaySelector overlaySelector = PowerMock
                .createMock(OverlaySelector.class);
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);
        
        OverlayManager overlayManager = new OverlayManager(geoMapView, null,
                null, null, false, null, null, overlaySelector);

        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(12).atLeastOnce();
        EasyMock.expect(
                overlaySelector.selectOverlay(overlayManager, 12, false, null,
                        null, null)).andReturn(true);
        EasyMock.expect(
                overlaySelector.selectOverlay(overlayManager, 12, true, null,
                        null, null)).andReturn(false);

        PowerMock.replayAll();
        assertFalse(overlayManager.usesDensityMap());
        overlayManager.selectOverlay();
        assertTrue(overlayManager.usesDensityMap());
        overlayManager.selectOverlay();
        assertFalse(overlayManager.usesDensityMap());
        PowerMock.verifyAll();
    }

    @Test
    public void testZoomInDensityMapToCachePins() {
        List<Overlay> overlays = new ArrayList<Overlay>();
        overlays.add(null);
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);
        CachePinsOverlayFactory cachePinsOverlayFactory = PowerMock
                .createMock(CachePinsOverlayFactory.class);
        CachePinsOverlay cachePinsOverlay = PowerMock
                .createMock(CachePinsOverlay.class);

        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD);
        EasyMock.expect(cachePinsOverlayFactory.getCachePinsOverlay())
                .andReturn(cachePinsOverlay);

        PowerMock.replayAll();
        OverlaySelector overlaySelector = new OverlaySelector();
        final OverlayManager overlayManager = new OverlayManager(geoMapView,
                overlays, null, cachePinsOverlayFactory, true, null, null,
                overlaySelector);
        overlayManager.selectOverlay();
        assertFalse(overlayManager.usesDensityMap());
        assertEquals(cachePinsOverlay, overlays.get(0));
        PowerMock.verifyAll();
    }

    @Test
    public void testZoomOutCachePinsToDensityMap() {
        List<Overlay> overlays = new ArrayList<Overlay>();
        overlays.add(null);
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);
        DensityOverlay densityOverlay = PowerMock
                .createMock(DensityOverlay.class);

        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD - 1);

        PowerMock.replayAll();
        OverlaySelector overlaySelector = new OverlaySelector();
        final OverlayManager overlayManager = new OverlayManager(geoMapView,
                overlays, densityOverlay, null, false, null, null,
                overlaySelector);
        overlayManager.selectOverlay();
        assertTrue(overlayManager.usesDensityMap());
        assertEquals(densityOverlay, overlays.get(0));
        PowerMock.verifyAll();
    }

    @Test
    public void testZoomOutDensityMapToDensityMap() {
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);

        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD - 1);

        PowerMock.replayAll();
        OverlaySelector overlaySelector = new OverlaySelector();
        final OverlayManager overlayManager = new OverlayManager(geoMapView,
                null, null, null, true, null, null, overlaySelector);
        overlayManager.refresh();
        assertTrue(overlayManager.usesDensityMap());
        PowerMock.verifyAll();
    }
}
