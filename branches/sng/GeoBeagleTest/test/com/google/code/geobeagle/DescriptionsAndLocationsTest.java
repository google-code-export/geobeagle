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

package com.google.code.geobeagle;

import junit.framework.TestCase;

public class DescriptionsAndLocationsTest extends TestCase {

    public final void testDescriptionsAndLocations() {
        Locations locations = new Locations();

        locations = new Locations();
        locations.add("37 122 etc");
        locations.add("37 122 foo");
        assertEquals("37 122 etc", locations.getPreviousLocations().get(0));
    }

    public final void testAdd() {
        Locations locations = new Locations();
        locations.add("new location");
        assertEquals("new location", locations.getPreviousLocations().get(0));
    }

    public final void testClear() {
        Locations locations = new Locations();
        locations.add("37 122 etc");
        locations.add("37 122 foo");
        locations.clear();

        assertEquals(0, locations.getPreviousLocations().size());
    }
}
