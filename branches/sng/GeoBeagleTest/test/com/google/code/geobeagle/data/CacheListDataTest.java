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

package com.google.code.geobeagle.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class CacheListDataTest {

    @Test
    public void testAdd() {
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        Location here = PowerMock.createMock(Location.class);

        ArrayList<Geocache> geocaches = new ArrayList<Geocache>(0);
        geocacheVectors.reset(0);
        geocacheVectors.addLocations(geocaches, here);
        geocacheVectors.sort();

        PowerMock.replayAll();
        new CacheListData(geocacheVectors).add(geocaches, here);
        PowerMock.verifyAll();
    }
}
