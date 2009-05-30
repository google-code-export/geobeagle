
package com.google.code.geobeagle.io;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.mainactivity.GeocacheFactory.Source;

import org.junit.Test;

public class DbToGeocacheAdapterTest {

    @Test
    public void testSourceNameToSourceType() {
        DbToGeocacheAdapter dbToGeocacheAdapter = new DbToGeocacheAdapter();
        assertEquals(Source.WEB_URL, dbToGeocacheAdapter.sourceNameToSourceType("intent"));
        assertEquals(Source.MY_LOCATION, dbToGeocacheAdapter.sourceNameToSourceType("mylocation"));
        assertEquals(Source.GPX, dbToGeocacheAdapter.sourceNameToSourceType("foo"));
        assertEquals(Source.LOC, dbToGeocacheAdapter.sourceNameToSourceType("foo.Loc"));
    }

    @Test
    public void testSourceTypeToSourceName() {
        DbToGeocacheAdapter dbToGeocacheAdapter = new DbToGeocacheAdapter();

        assertEquals("mylocation", dbToGeocacheAdapter.sourceTypeToSourceName(Source.MY_LOCATION,
                "foo"));
        assertEquals("intent", dbToGeocacheAdapter.sourceTypeToSourceName(Source.WEB_URL, "foo"));
        assertEquals("chicago", dbToGeocacheAdapter.sourceTypeToSourceName(Source.GPX, "chicago"));
    }
}
