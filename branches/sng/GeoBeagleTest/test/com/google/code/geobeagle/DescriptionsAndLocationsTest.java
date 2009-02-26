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
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();

        descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("SFO", "37 122 etc");
        descriptionsAndLocations.add("OAK", "37 122 foo");
        assertEquals("OAK", descriptionsAndLocations.getPreviousDescriptions().get(1));
        assertEquals("37 122 etc", descriptionsAndLocations.getPreviousLocations().get(0));
    }

    public final void testAdd() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("new description", "new location");
        assertEquals("new description", descriptionsAndLocations.getPreviousDescriptions().get(0));
    }

    public final void testAddDupe() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("description1", "new location");
        descriptionsAndLocations.add("description2", "new location");
        descriptionsAndLocations.add("description2", "new location");

        assertEquals(2, descriptionsAndLocations.getPreviousDescriptions().size());
        assertEquals("description1", descriptionsAndLocations.getPreviousDescriptions().get(0));
        assertEquals("description2", descriptionsAndLocations.getPreviousDescriptions().get(1));
    }

    public final void testClear() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("SFO", "37 122 etc");
        descriptionsAndLocations.add("OAK", "37 122 foo");
        descriptionsAndLocations.clear();

        assertEquals(0, descriptionsAndLocations.getPreviousDescriptions().size());
        assertEquals(0, descriptionsAndLocations.getPreviousLocations().size());
    }
}
