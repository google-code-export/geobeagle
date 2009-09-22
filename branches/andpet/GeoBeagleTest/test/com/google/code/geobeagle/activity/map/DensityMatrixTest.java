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
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.map.DensityMatrix.DensityPatch;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        DensityMatrix.class, GeoPoint.class
})
public class DensityMatrixTest {
    @Test
    public void testDensityPatch() {
        DensityPatch densityPatch = new DensityPatch(1, 2);
        densityPatch.setCacheCount(1);
        assertEquals(42, densityPatch.getAlpha());
        assertEquals(1, densityPatch.getCacheCount());
        assertEquals(1000000, densityPatch.getLatLowE6());
        assertEquals(2000000, densityPatch.getLonLowE6());
    }

    @Test
    public void test2OnePatch() throws Exception {
        Geocache cache1 = PowerMock.createMock(Geocache.class);

        EasyMock.expect(cache1.getLatitude()).andReturn(0.05);
        EasyMock.expect(cache1.getLongitude()).andReturn(0.05);

        PowerMock.replayAll();
        ArrayList<Geocache> caches = new ArrayList<Geocache>();
        caches.add(cache1);
        // Latitude bucket: every 10000, longitude bucket: every 20000.
        DensityMatrix matrix = new DensityMatrix(0.05, 0.04);
        matrix.addCaches(caches);

        List<DensityPatch> densityPatches = matrix.getDensityPatches();
        assertEquals(1, densityPatches.get(0).getCacheCount());
        PowerMock.verifyAll();
    }

    @Test
    public void test2ResolutionBorderPatch() throws Exception {
        Geocache cache1 = PowerMock.createMock(Geocache.class);

        EasyMock.expect(cache1.getLatitude()).andReturn(500.0);
        EasyMock.expect(cache1.getLongitude()).andReturn(500.0);

        PowerMock.replayAll();
        ArrayList<Geocache> caches = new ArrayList<Geocache>();
        caches.add(cache1);

        // (500, 500) should be in slot [1, 1], not [0, 0] because there
        // is a partial patch going from (450, 450) - (500, 500).
        DensityMatrix matrix = new DensityMatrix(450.0, 450.0);
        matrix.addCaches(caches);

        // 3 slots: 450-500, 500-600, 600-601

        PowerMock.verifyAll();
    }
}
