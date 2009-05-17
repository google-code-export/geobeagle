
package com.google.code.geobeagle.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class DistanceFormatterTest {
    @Test
    public void testKilometers() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        Locale.setDefault(Locale.ENGLISH);
        assertEquals("1.23km", distanceFormatter.formatDistance(1234.5f));
    }

    @Test
    public void testMeters() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        assertEquals("3m", distanceFormatter.formatDistance(3.5f));
    }

    @Test
    public void testNoGps() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        assertEquals("", distanceFormatter.formatDistance(-1f));
    }

    @Test
    public void testFormatBearing() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        assertEquals("^", distanceFormatter.formatBearing(-720));
        assertEquals("^", distanceFormatter.formatBearing(44));
        assertEquals(">", distanceFormatter.formatBearing(134));
        assertEquals("v", distanceFormatter.formatBearing(220));
        assertEquals("<", distanceFormatter.formatBearing(314));
        assertEquals("^", distanceFormatter.formatBearing(315));
    }
}
