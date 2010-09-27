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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;

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
        Log.class, GeoMapView.class
})
public class OverlayManagerTest extends GeoBeagleTest {
    private GeoMapActivity geoMapActivity;
    private GeoMapView geoMapView;
    private MyLocationOverlay myLocationOverlay;
    private List<Overlay> mapOverlays;
    private List<Overlay> overlays;

    @Before
    public void setUp() {
        geoMapActivity = PowerMock.createMock(GeoMapActivity.class);
        geoMapView = PowerMock.createMock(GeoMapView.class);
        myLocationOverlay = PowerMock.createMock(MyLocationOverlay.class);
        mapOverlays = new ArrayList<Overlay>();
        overlays = new ArrayList<Overlay>();
    }

    @Test
    public void zoomOutDensityMapToDensityMap() {
        EasyMock.expect(geoMapView.getOverlays()).andReturn(mapOverlays).anyTimes();
        EasyMock.expect(geoMapActivity.findViewById(R.id.mapview)).andReturn(geoMapView).anyTimes();
        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD - 1);
        EasyMock.expect(geoMapActivity.getMyLocationOverlay()).andReturn(myLocationOverlay);

        PowerMock.replayAll();
        final OverlayManager overlayManager = new OverlayManager(geoMapActivity, null, null, null);
        overlayManager.selectOverlay();
        assertTrue(overlayManager.usesDensityMap());
        PowerMock.verifyAll();
    }

    @Test
    public void zoomOutCachePinsToDensityMap() {
        DensityOverlay densityOverlay = PowerMock.createMock(DensityOverlay.class);
        overlays.add(densityOverlay);

        EasyMock.expect(geoMapView.getOverlays()).andReturn(mapOverlays).anyTimes();
        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD - 1);
        EasyMock.expect(geoMapActivity.findViewById(R.id.mapview)).andReturn(geoMapView).anyTimes();
        EasyMock.expect(geoMapActivity.getMyLocationOverlay()).andReturn(myLocationOverlay);

        PowerMock.replayAll();
        final OverlayManager overlayManager = new OverlayManager(geoMapActivity, densityOverlay,
                null, null);
        overlayManager.selectOverlay();
        assertTrue(overlayManager.usesDensityMap());
        assertEquals(densityOverlay, overlays.get(0));
        PowerMock.verifyAll();
    }

    @Test
    public void zoomInDensityMapToCachePins() {
        CachePinsOverlayFactory cachePinsOverlayFactory = PowerMock
                .createMock(CachePinsOverlayFactory.class);
        CachePinsOverlay cachePinsOverlay = PowerMock.createMock(CachePinsOverlay.class);

        overlays.add(cachePinsOverlay);
        EasyMock.expect(geoMapView.getOverlays()).andReturn(mapOverlays).anyTimes();
        EasyMock.expect(geoMapActivity.findViewById(R.id.mapview)).andReturn(geoMapView).anyTimes();
        EasyMock.expect(geoMapView.getZoomLevel()).andReturn(
                OverlayManager.DENSITY_MAP_ZOOM_THRESHOLD);
        EasyMock.expect(cachePinsOverlayFactory.getCachePinsOverlay()).andReturn(cachePinsOverlay);
        EasyMock.expect(geoMapActivity.getMyLocationOverlay()).andReturn(myLocationOverlay);

        PowerMock.replayAll();
        final OverlayManager overlayManager = new OverlayManager(geoMapActivity, null,
                cachePinsOverlayFactory, null);
        overlayManager.selectOverlay();
        assertFalse(overlayManager.usesDensityMap());
        assertEquals(cachePinsOverlay, overlays.get(0));
        PowerMock.verifyAll();
    }
}
