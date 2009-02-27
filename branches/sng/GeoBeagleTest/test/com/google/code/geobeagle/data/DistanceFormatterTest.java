package com.google.code.geobeagle.data;

import junit.framework.TestCase;

public class DistanceFormatterTest extends TestCase {
    public void testMeters() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        assertEquals("3m", distanceFormatter.format(3.5f));
    }

    public void testKilometers() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        assertEquals("1km", distanceFormatter.format(1234.5f));
    }

    public void testNoGps() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        assertEquals("", distanceFormatter.format(-1f));
    }
}
