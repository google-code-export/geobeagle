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

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.di.GeocacheFactory;

import java.util.regex.Pattern;

import junit.framework.TestCase;

public class GeocacheTest extends TestCase {

    private static final Pattern mGeocachePatterns[] = {
            Pattern.compile("(?:GC)(\\w*)"), Pattern.compile("(?:LB)(\\w*)")
    };

    public void testGetCoordinatesIdAndName() {
        Geocache destinationImpl = Geocache.create("s37 03.0, 122 00.0 (Description)",
                mGeocachePatterns);
        assertEquals(37.05, destinationImpl.getLatitude());
        assertEquals(122.0, destinationImpl.getLongitude());
        assertEquals("37.05, 122.0 (Description)", destinationImpl.getCoordinatesIdAndName());
    }

    public void testLatLong() {
        Geocache geocache = Geocache.create("37 00.0, 122 00.0", mGeocachePatterns);
        assertEquals(37.0, geocache.getLatitude());
        assertEquals(122.0, geocache.getLongitude());
        assertEquals("", geocache.getName());

        Geocache ll2 = Geocache.create("37 00.0, 122 00.0", mGeocachePatterns);
        assertEquals(37.0, ll2.getLatitude());
        assertEquals(122.0, ll2.getLongitude());

        Geocache ll3 = Geocache.create("37 03.0, 122 00.0", mGeocachePatterns);
        assertEquals(37.05, ll3.getLatitude());
        assertEquals(122.0, ll3.getLongitude());

        Geocache ll4 = Geocache.create(" \t 37 03.0, 122 00.0  ", mGeocachePatterns);
        assertEquals(37.05, ll4.getLatitude());
        assertEquals(122.0, ll4.getLongitude());
        assertEquals("", ll4.getIdAndName());
    }

    public void testDescriptionGetIdAndName() {
        Geocache destinationImpl = Geocache.create(" \t 37 03.0, 122 00.0 (Description)",
                mGeocachePatterns);
        assertEquals(37.05, destinationImpl.getLatitude());
        assertEquals(122.0, destinationImpl.getLongitude());
        assertEquals("Description", destinationImpl.getIdAndName());
    }

    public void testNoName() {
        Geocache destinationImpl = Geocache.create(" \t 37 03.0, 122 00.0 (GC12345)",
                mGeocachePatterns);
        assertEquals(37.05, destinationImpl.getLatitude());
        assertEquals(122.0, destinationImpl.getLongitude());
        assertEquals("GC12345", destinationImpl.getId());
        assertEquals("", destinationImpl.getName());
        assertEquals("GC12345", destinationImpl.getIdAndName());
    }

    public void testBadCoordinatesGoodDescription() {
        Geocache destinationImpl = Geocache.create("  FOO (Description)", mGeocachePatterns);
        assertEquals(0.0, destinationImpl.getLatitude());
        assertEquals(0.0, destinationImpl.getLongitude());
        assertEquals("Description", destinationImpl.getIdAndName());
    }

    public void testGetId() {
        Geocache geocache = Geocache.create("34.313,122.43 (LB89882: The Nut Case)",
                mGeocachePatterns);
        assertEquals("89882", geocache.getShortId());
        assertEquals("LB89882", geocache.getId());
        assertEquals(1, geocache.getContentIndex());
        assertEquals("The Nut Case", geocache.getName());

        geocache = Geocache.create("34.313,122.43 (GCFOOBAR: GS cache)", mGeocachePatterns);
        assertEquals("FOOBAR", geocache.getShortId());
        assertEquals(0, geocache.getContentIndex());
    }

    public void testEmptyDestination() {
        Geocache geocache = Geocache.create("", mGeocachePatterns);
        assertEquals(0.0, geocache.getLatitude());
        assertEquals(0.0, geocache.getLongitude());
        assertEquals("", geocache.getIdAndName());
        assertEquals("", geocache.getShortId());
    }

    public void testExtractDescription() {
        assertEquals("GC123", GeocacheFactory.extractDescription("123 (GC123)"));
    }
}
