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

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.location.LocationControlBuffered;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class CacheListDataTest {

    @Test
    public void testAdd() {
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);

        ArrayList<Geocache> geocaches = new ArrayList<Geocache>(0);
        geocacheVectors.reset(0);
        geocacheVectors.addLocations(geocaches, locationControlBuffered);

        PowerMock.replayAll();
        new CacheListData(geocacheVectors).add(geocaches, locationControlBuffered);
        PowerMock.verifyAll();
    }

    @Test
    public void testGet() {
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        ArrayList<GeocacheVector> geocacheVectorsList = new ArrayList<GeocacheVector>();

        EasyMock.expect(geocacheVectors.getGeocacheVectorsList()).andReturn(geocacheVectorsList);

        PowerMock.replayAll();
        assertEquals(geocacheVectorsList, new CacheListData(geocacheVectors).get());
        PowerMock.verifyAll();
    }

    @Test
    public void testSize() {
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);

        EasyMock.expect(geocacheVectors.size()).andReturn(12);

        PowerMock.replayAll();
        assertEquals(12, new CacheListData(geocacheVectors).size());
        PowerMock.verifyAll();
    }
}
