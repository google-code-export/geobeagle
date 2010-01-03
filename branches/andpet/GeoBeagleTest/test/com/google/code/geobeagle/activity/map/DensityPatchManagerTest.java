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
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.Toaster.OneTimeToaster;
import com.google.code.geobeagle.database.CachesProviderLazyArea;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        DensityPatchManager.class, Log.class, MapView.class 
})
public class DensityPatchManagerTest {
    @Before
    public void allowLogging() {
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(
                Log.d((String)EasyMock.anyObject(), (String)EasyMock
                        .anyObject())).andReturn(0).anyTimes();
    }

    @Test
    public void testDensityPatchManager() throws Exception {
        CachesProviderLazyArea lazyArea = PowerMock
                .createMock(CachesProviderLazyArea.class);
        MapView mapView = PowerMock.createMock(MapView.class);
        ArrayList<DensityMatrix.DensityPatch> patches = new ArrayList<DensityMatrix.DensityPatch>();
        Projection projection = PowerMock.createMock(Projection.class);
        GeoPoint newTopLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint newBottomRight = PowerMock.createMock(GeoPoint.class);
        DensityMatrix densityMatrix = PowerMock.createMock(DensityMatrix.class);
        GeocacheList geocacheList = PowerMock.createMock(GeocacheList.class);
        OneTimeToaster oneTimeToaster = PowerMock.createMock(OneTimeToaster.class);

        EasyMock.expect(mapView.getProjection()).andReturn(projection);
        EasyMock.expect(projection.fromPixels(0, 0)).andReturn(newTopLeft);
        EasyMock.expect(mapView.getRight()).andReturn(100);
        EasyMock.expect(mapView.getBottom()).andReturn(200);
        EasyMock.expect(projection.fromPixels(100, 200)).andReturn(
                newBottomRight);

        EasyMock.expect(newTopLeft.getLatitudeE6()).andReturn(38000000);
        EasyMock.expect(newTopLeft.getLongitudeE6()).andReturn(-123000000);
        EasyMock.expect(newBottomRight.getLatitudeE6()).andReturn(37000000);
        EasyMock.expect(newBottomRight.getLongitudeE6()).andReturn(-122000000);
        lazyArea.setBounds(37.0, -123.0, 38.0, -122.0);
        EasyMock.expect(lazyArea.hasChanged()).andReturn(true);
        EasyMock.expect(lazyArea.getCaches()).andReturn(geocacheList);
        EasyMock.expect(lazyArea.tooManyCaches()).andReturn(false);
        oneTimeToaster.showToast(false);
        lazyArea.resetChanged();

        PowerMock.expectNew(DensityMatrix.class,
                DensityPatchManager.RESOLUTION_LATITUDE,
                DensityPatchManager.RESOLUTION_LONGITUDE).andReturn(
                densityMatrix);
        densityMatrix.addCaches(geocacheList);
        EasyMock.expect(densityMatrix.getDensityPatches()).andReturn(patches);

        PowerMock.replayAll();
        assertEquals(patches, new DensityPatchManager(null, lazyArea, oneTimeToaster)
                .getDensityPatches(mapView));
        PowerMock.verifyAll();
    }

    @Test
    public void testDensityPatchManagerCached() throws Exception {
        CachesProviderLazyArea lazyArea = PowerMock
                .createMock(CachesProviderLazyArea.class);
        MapView mapView = PowerMock.createMock(MapView.class);
        ArrayList<DensityMatrix.DensityPatch> patches = new ArrayList<DensityMatrix.DensityPatch>();
        Projection projection = PowerMock.createMock(Projection.class);
        GeoPoint newTopLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint newBottomRight = PowerMock.createMock(GeoPoint.class);

        EasyMock.expect(mapView.getProjection()).andReturn(projection);
        EasyMock.expect(projection.fromPixels(0, 0)).andReturn(newTopLeft);
        EasyMock.expect(mapView.getRight()).andReturn(100);
        EasyMock.expect(mapView.getBottom()).andReturn(200);
        EasyMock.expect(projection.fromPixels(100, 200)).andReturn(
                newBottomRight);

        EasyMock.expect(newTopLeft.getLatitudeE6()).andReturn(38000000);
        EasyMock.expect(newTopLeft.getLongitudeE6()).andReturn(-123000000);
        EasyMock.expect(newBottomRight.getLatitudeE6()).andReturn(37000000);
        EasyMock.expect(newBottomRight.getLongitudeE6()).andReturn(-122000000);
        lazyArea.setBounds(37.0, -123.0, 38.0, -122.0);
        EasyMock.expect(lazyArea.hasChanged()).andReturn(false);

        PowerMock.replayAll();
        assertEquals(patches, new DensityPatchManager(patches, lazyArea, null)
                .getDensityPatches(mapView));
        PowerMock.verifyAll();
    }

}
