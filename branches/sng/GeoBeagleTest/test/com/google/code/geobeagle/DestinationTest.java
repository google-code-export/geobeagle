
package com.google.code.geobeagle;

import junit.framework.TestCase;

public class DestinationTest extends TestCase {
    public void testLatLong() {
        Destination ll = new Destination("37 00.0 122 00.0");
        assertEquals(37.0, ll.getLatitude());
        assertEquals(122.0, ll.getLongitude());

        Destination ll2 = new Destination("37 00.0\t122 00.0");
        assertEquals(37.0, ll2.getLatitude());
        assertEquals(122.0, ll2.getLongitude());

        Destination ll3 = new Destination("37 03.0 122 00.0");
        assertEquals(37.05, ll3.getLatitude());
        assertEquals(122.0, ll3.getLongitude());

        Destination ll4 = new Destination(" \t 37 03.0 122 00.0  ");
        assertEquals(37.05, ll4.getLatitude());
        assertEquals(122.0, ll4.getLongitude());
        assertEquals("", ll4.getDescription());
    }

    public void testDescription() {
        Destination destination = new Destination(" \t 37 03.0 122 00.0 # Description ");
        assertEquals(37.05, destination.getLatitude());
        assertEquals(122.0, destination.getLongitude());
        assertEquals("Description", destination.getDescription());
    }

    public void testBadCoordinatesGoodDescription() {
        Destination destination = new Destination(" 37.0 122.0 # Description ");
        assertEquals(0.0, destination.getLatitude());
        assertEquals(0.0, destination.getLongitude());
        assertEquals("Description", destination.getDescription());

    }

    public void testEmptyDestination() {
        Destination destination = new Destination("");
        assertEquals(0.0, destination.getLatitude());
        assertEquals(0.0, destination.getLongitude());
        assertEquals("", destination.getDescription());

    }
}
