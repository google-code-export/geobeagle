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
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
    }

    @Test
    public void zoomOutDensityMapToDensityMap() {
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);

        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD - 1);

        PowerMock.replayAll();
        final OverlayManager overlayManager = new OverlayManager(geoMapView, null, null, null, true);
        overlayManager.selectOverlay();
        assertTrue(overlayManager.usesDensityMap());
        PowerMock.verifyAll();
    }

    @Test
    public void zoomOutCachePinsToDensityMap() {
        List<Overlay> overlays = new ArrayList<Overlay>();
        overlays.add(null);
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);
        DensityOverlay densityOverlay = PowerMock.createMock(DensityOverlay.class);

        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD - 1);

        PowerMock.replayAll();
        final OverlayManager overlayManager = new OverlayManager(geoMapView, overlays,
                densityOverlay, null, false);
        overlayManager.selectOverlay();
        assertTrue(overlayManager.usesDensityMap());
        assertEquals(densityOverlay, overlays.get(0));
        PowerMock.verifyAll();
    }

    @Test
    public void zoomInDensityMapToCachePins() {
        List<Overlay> overlays = new ArrayList<Overlay>();
        overlays.add(null);
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);
        CachePinsOverlayFactory cachePinsOverlayFactory = PowerMock
                .createMock(CachePinsOverlayFactory.class);
        CachePinsOverlay cachePinsOverlay = PowerMock.createMock(CachePinsOverlay.class);

        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD);
        EasyMock.expect(cachePinsOverlayFactory.getCachePinsOverlay()).andReturn(cachePinsOverlay);

        PowerMock.replayAll();
        final OverlayManager overlayManager = new OverlayManager(geoMapView, overlays, null,
                cachePinsOverlayFactory, true);
        overlayManager.selectOverlay();
        assertFalse(overlayManager.usesDensityMap());
        assertEquals(cachePinsOverlay, overlays.get(0));
        PowerMock.verifyAll();
    }
}
