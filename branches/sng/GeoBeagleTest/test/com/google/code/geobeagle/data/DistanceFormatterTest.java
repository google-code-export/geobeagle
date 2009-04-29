
package com.google.code.geobeagle.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class DistanceFormatterTest {
    @Test
    public void testKilometers() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        Locale.setDefault(Locale.ENGLISH);
        assertEquals("1.23km", distanceFormatter.format(1234.5f));
    }

    @Test
    public void testMeters() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        assertEquals("3m", distanceFormatter.format(3.5f));
    }

    @Test
    public void testNoGps() {
        DistanceFormatter distanceFormatter = new DistanceFormatter();
        assertEquals("", distanceFormatter.format(-1f));
    }
}
