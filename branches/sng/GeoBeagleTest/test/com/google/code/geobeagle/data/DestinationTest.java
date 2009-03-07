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
import com.google.code.geobeagle.data.di.DestinationFactory;

import java.util.regex.Pattern;

import junit.framework.TestCase;

public class DestinationTest extends TestCase {

    private static final Pattern mDestinationPatterns[] = {
            Pattern.compile("(?:GC)(\\w*)"), Pattern.compile("(?:LB)(\\w*)")
    };

    public void testLatLong() {
        Destination destination = new Destination("37 00.0, 122 00.0", mDestinationPatterns);
        assertEquals(37.0, destination.getLatitude());
        assertEquals(122.0, destination.getLongitude());
        assertEquals("", destination.getName());

        Destination ll2 = new Destination("37 00.0, 122 00.0", mDestinationPatterns);
        assertEquals(37.0, ll2.getLatitude());
        assertEquals(122.0, ll2.getLongitude());

        Destination ll3 = new Destination("37 03.0, 122 00.0", mDestinationPatterns);
        assertEquals(37.05, ll3.getLatitude());
        assertEquals(122.0, ll3.getLongitude());

        Destination ll4 = new Destination(" \t 37 03.0, 122 00.0  ", mDestinationPatterns);
        assertEquals(37.05, ll4.getLatitude());
        assertEquals(122.0, ll4.getLongitude());
        assertEquals("", ll4.getDescription());
    }

    public void testDescription() {
        Destination destinationImpl = new Destination(" \t 37 03.0, 122 00.0 (Description)",
                mDestinationPatterns);
        assertEquals(37.05, destinationImpl.getLatitude());
        assertEquals(122.0, destinationImpl.getLongitude());
        assertEquals("Description", destinationImpl.getDescription());
    }

    public void testNoName() {
        Destination destinationImpl = new Destination(" \t 37 03.0, 122 00.0 (GC12345)",
                mDestinationPatterns);
        assertEquals(37.05, destinationImpl.getLatitude());
        assertEquals(122.0, destinationImpl.getLongitude());
        assertEquals("GC12345", destinationImpl.getFullId());
        assertEquals("", destinationImpl.getName());
        assertEquals("GC12345", destinationImpl.getDescription());
    }

    public void testBadCoordinatesGoodDescription() {
        Destination destinationImpl = new Destination("  FOO (Description)", mDestinationPatterns);
        assertEquals(0.0, destinationImpl.getLatitude());
        assertEquals(0.0, destinationImpl.getLongitude());
        assertEquals("Description", destinationImpl.getDescription());
    }

    public void testGetId() {
        Destination destination = new Destination("34.313,122.43 (LB89882: The Nut Case)",
                mDestinationPatterns);
        assertEquals("89882", destination.getId());
        assertEquals("LB89882", destination.getFullId());
        assertEquals(1, destination.getContentIndex());
        assertEquals("The Nut Case", destination.getName());

        destination = new Destination("34.313,122.43 (GCFOOBAR: GS cache)", mDestinationPatterns);
        assertEquals("FOOBAR", destination.getId());
        assertEquals(0, destination.getContentIndex());
    }

    public void testEmptyDestination() {
        Destination destination = new Destination("", mDestinationPatterns);
        assertEquals(0.0, destination.getLatitude());
        assertEquals(0.0, destination.getLongitude());
        assertEquals("", destination.getDescription());
    }

    public void testExtractDescription() {
        assertEquals("GC123", DestinationFactory.extractDescription("123 (GC123)"));
    }
}
