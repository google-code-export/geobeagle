
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
        Destination destinationImpl = new Destination(" \t 37 03.0 122 00.0 # Description ");
        assertEquals(37.05, destinationImpl.getLatitude());
        assertEquals(122.0, destinationImpl.getLongitude());
        assertEquals("Description", destinationImpl.getDescription());
    }

    public void testBadCoordinatesGoodDescription() {
        Destination destinationImpl = new Destination(" 37.0 122.0 # Description ");
        assertEquals(0.0, destinationImpl.getLatitude());
        assertEquals(0.0, destinationImpl.getLongitude());
        assertEquals("Description", destinationImpl.getDescription());

    }

    public void testEmptyDestination() {
        Destination destinationImpl = new Destination("");
        assertEquals(0.0, destinationImpl.getLatitude());
        assertEquals(0.0, destinationImpl.getLongitude());
        assertEquals("", destinationImpl.getDescription());
    }
    
    public void testExtractDescription() {
        assertEquals("GC123", Destination.extractDescription("123 # GC123"));
    }

    public void testExtractDescriptionNoCoords() {
        assertEquals("GC123", Destination.extractDescription("GC123"));
    }
}
