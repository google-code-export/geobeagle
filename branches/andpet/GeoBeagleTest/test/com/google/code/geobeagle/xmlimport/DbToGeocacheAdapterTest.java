
package com.google.code.geobeagle.xmlimport;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.SourceNameTranslator;

import org.junit.Test;

public class DbToGeocacheAdapterTest {

    @Test
    public void testSourceNameToSourceType() {
        SourceNameTranslator dbToGeocacheAdapter = new SourceNameTranslator();
        assertEquals(Source.WEB_URL, dbToGeocacheAdapter.sourceNameToSourceType("intent"));
        assertEquals(Source.MY_LOCATION, dbToGeocacheAdapter.sourceNameToSourceType("mylocation"));
        assertEquals(Source.GPX, dbToGeocacheAdapter.sourceNameToSourceType("foo"));
        assertEquals(Source.LOC, dbToGeocacheAdapter.sourceNameToSourceType("foo.Loc"));
    }

    @Test
    public void testSourceTypeToSourceName() {
        SourceNameTranslator dbToGeocacheAdapter = new SourceNameTranslator();

        assertEquals("mylocation", dbToGeocacheAdapter.sourceTypeToSourceName(Source.MY_LOCATION,
                "foo"));
        assertEquals("intent", dbToGeocacheAdapter.sourceTypeToSourceName(Source.WEB_URL, "foo"));
        assertEquals("chicago", dbToGeocacheAdapter.sourceTypeToSourceName(Source.GPX, "chicago"));
    }
}
