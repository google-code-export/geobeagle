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

import static org.junit.Assert.*;

import com.google.code.geobeagle.Geocache;

import org.junit.Test;
import org.powermock.api.easymock.PowerMock;


public class DistanceAndBearingTest {
    @Test
    public void testDistanceAndBearing() {
        Geocache geocache= PowerMock.createMock(Geocache.class);
        
        DistanceAndBearing distanceAndBearing = new DistanceAndBearing(geocache, 12, 180);
        assertEquals(geocache,distanceAndBearing.getGeocache());
        assertEquals(180, distanceAndBearing.getBearing(), .1);
        assertEquals(12, distanceAndBearing.getDistance(), .1);
    }
}
