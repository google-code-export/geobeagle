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

import com.google.code.geobeagle.data.Destination;

import java.util.regex.Pattern;

import junit.framework.TestCase;

public class DestinationTest extends TestCase {
    public void testLatLong() {
        Pattern destinationPatterns[] = {
                Pattern.compile("(?:GC)(\\w*)"), Pattern.compile("(?:LB)(\\w*)")
        };
        Destination ll = new Destination("37 00.0, 122 00.0", destinationPatterns);
        assertEquals(37.0, ll.getLatitude());
        assertEquals(122.0, ll.getLongitude());

        Destination ll2 = new Destination("37 00.0, 122 00.0", destinationPatterns);
        assertEquals(37.0, ll2.getLatitude());
        assertEquals(122.0, ll2.getLongitude());

        Destination ll3 = new Destination("37 03.0, 122 00.0", destinationPatterns);
        assertEquals(37.05, ll3.getLatitude());
        assertEquals(122.0, ll3.getLongitude());

        Destination ll4 = new Destination(" \t 37 03.0, 122 00.0  ", destinationPatterns);
        assertEquals(37.05, ll4.getLatitude());
        assertEquals(122.0, ll4.getLongitude());
        assertEquals("", ll4.getDescription());
    }

    public void testDescription() {
        Pattern destinationPatterns[] = {
                Pattern.compile("(?:GC)(\\w*)"), Pattern.compile("(?:LB)(\\w*)")
        };
        Destination destinationImpl = new Destination(" \t 37 03.0, 122 00.0 (Description)",
                destinationPatterns);
        assertEquals(37.05, destinationImpl.getLatitude());
        assertEquals(122.0, destinationImpl.getLongitude());
        assertEquals("Description", destinationImpl.getDescription());
    }

    public void testBadCoordinatesGoodDescription() {
        Destination destinationImpl = new Destination("  FOO (Description)", new Pattern[] {
                Pattern.compile("(?:GC)(\\w*)"), Pattern.compile("(?:LB)(\\w*)")
        });
        assertEquals(0.0, destinationImpl.getLatitude());
        assertEquals(0.0, destinationImpl.getLongitude());
        assertEquals("Description", destinationImpl.getDescription());

    }

    public void testGetId() {
        Pattern destinationPatterns[] = {
                Pattern.compile("(?:LB)(\\w*)"), Pattern.compile("(?:GC)(\\w*)")
        };
        Destination destination = new Destination("34.313,122.43 (LB89882--The Nut Case)",
                destinationPatterns);
        assertEquals("89882", destination.getId());
        assertEquals(0, destination.getContentIndex());

        destination = new Destination("34.313,122.43 (GCFOOBAR--GS cache)", destinationPatterns);
        assertEquals("FOOBAR", destination.getId());
        assertEquals(1, destination.getContentIndex());

    }

    public void testEmptyDestination() {
        Destination destination = new Destination("", new Pattern[] {
                Pattern.compile("(?:GC)(\\w*)"), Pattern.compile("(?:LB)(\\w*)")
        });
        assertEquals(0.0, destination.getLatitude());
        assertEquals(0.0, destination.getLongitude());
        assertEquals("", destination.getDescription());
    }

    public void testExtractDescription() {
        assertEquals("GC123", Destination.extractDescription("123 (GC123)"));
    }
    
    public void testGetCacheListDisplayMap() {
        
    }
}
