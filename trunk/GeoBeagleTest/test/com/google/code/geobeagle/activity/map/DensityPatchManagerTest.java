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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.Geocache;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        DensityPatchManager.class, MapView.class
})
public class DensityPatchManagerTest {

    @Test
    public void testDensityPatchManager() throws Exception {
        QueryManager queryManager = PowerMock.createMock(QueryManager.class);
        MapView mapView = PowerMock.createMock(MapView.class);
        ArrayList<DensityMatrix.DensityPatch> patches = new ArrayList<DensityMatrix.DensityPatch>();
        Projection projection = PowerMock.createMock(Projection.class);
        GeoPoint newTopLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint newBottomRight = PowerMock.createMock(GeoPoint.class);
        ArrayList<Geocache> list = new ArrayList<Geocache>();
        DensityMatrix densityMatrix = PowerMock.createMock(DensityMatrix.class);

        EasyMock.expect(mapView.getProjection()).andReturn(projection);
        EasyMock.expect(projection.fromPixels(0, 0)).andReturn(newTopLeft);
        EasyMock.expect(mapView.getRight()).andReturn(100);
        EasyMock.expect(mapView.getBottom()).andReturn(200);
        EasyMock.expect(projection.fromPixels(100, 200)).andReturn(newBottomRight);
        EasyMock.expect(queryManager.needsLoading(newTopLeft, newBottomRight)).andReturn(true);

        EasyMock.expect(queryManager.load(newTopLeft, newBottomRight, null)).andReturn(list);
        PowerMock.expectNew(DensityMatrix.class, DensityPatchManager.RESOLUTION_LATITUDE,
                DensityPatchManager.RESOLUTION_LONGITUDE).andReturn(densityMatrix);
        densityMatrix.addCaches(list);
        EasyMock.expect(densityMatrix.getDensityPatches()).andReturn(patches);

        PowerMock.replayAll();
        assertEquals(patches,
                new DensityPatchManager(queryManager, null)
                .getDensityPatches(mapView));
        PowerMock.verifyAll();
    }

    @Test
    public void testDensityPatchManagerCached() throws Exception {
        QueryManager queryManager = PowerMock.createMock(QueryManager.class);
        MapView mapView = PowerMock.createMock(MapView.class);
        ArrayList<DensityMatrix.DensityPatch> patches = new ArrayList<DensityMatrix.DensityPatch>();
        Projection projection = PowerMock.createMock(Projection.class);
        GeoPoint newTopLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint newBottomRight = PowerMock.createMock(GeoPoint.class);

        EasyMock.expect(mapView.getProjection()).andReturn(projection);
        EasyMock.expect(projection.fromPixels(0, 0)).andReturn(newTopLeft);
        EasyMock.expect(mapView.getRight()).andReturn(100);
        EasyMock.expect(mapView.getBottom()).andReturn(200);
        EasyMock.expect(projection.fromPixels(100, 200)).andReturn(newBottomRight);
        EasyMock.expect(queryManager.needsLoading(newTopLeft, newBottomRight)).andReturn(false);

        PowerMock.replayAll();
        assertEquals(patches,
                new DensityPatchManager(queryManager, null)
                .getDensityPatches(mapView));
        PowerMock.verifyAll();
    }

}
