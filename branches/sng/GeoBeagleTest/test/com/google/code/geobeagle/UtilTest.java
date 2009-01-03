
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.net.UrlQuerySanitizer;
import android.net.UrlQuerySanitizer.ValueSanitizer;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
    public void testMinutesToDegrees() {
        assertEquals(122.0, Util.minutesToDegrees("122"));
        assertEquals(122.5, Util.minutesToDegrees("122 30"));
        assertEquals(122.51, Util.minutesToDegrees("122 30.600"));
        assertEquals(-122.51, Util.minutesToDegrees("-122 30.600"));
        assertEquals(-0.0165, Util.minutesToDegrees("W 000¡ 00.990"));
        assertEquals(-0.0165, Util.minutesToDegrees("-000¡ 00.990"));
        assertEquals(-122.51, Util.minutesToDegrees("-122¡ 30.600"));
        assertEquals(122.51, Util.minutesToDegrees("E 122¡ 30.600"));
        assertEquals(-122.51, Util.minutesToDegrees("W 122¡ 30.600"));
        assertEquals(-37.0, Util.minutesToDegrees("S 37¡ 0.000"));
        assertEquals(-37.0, Util.minutesToDegrees(" S 37¡ 0.000"));
    }

    public void testGetLatLonFromQueryGCAtEnd() {
        // Util.getLatLonFromQuery("a");
        String[] ll = Util.getLatLonFromQuery("N+47¡+40.464+W+122¡+20.119+(GCTANE)+");
        assertEquals("N 47¡ 40.464", ll[0]);
        assertEquals("W 122¡ 20.119", ll[1]);
        assertEquals("GCTANE", ll[2]);
    }

    public void testGetLatLonFromQuery() {
        String[] latLonFromQuery = Util.getLatLonFromQuery("GC1ERCC@N+37¡+15.165+W+122¡+02.620+");
        assertEquals("N 37¡ 15.165", latLonFromQuery[0]);
        assertEquals("W 122¡ 02.620", latLonFromQuery[1]);
        latLonFromQuery = Util.getLatLonFromQuery("GC1@N+37¡+15.165+W+122¡+02.620+");
        assertEquals("N 37¡ 15.165", latLonFromQuery[0]);
        assertEquals("W 122¡ 02.620", latLonFromQuery[1]);

        latLonFromQuery = Util.getLatLonFromQuery("GC1@N+37¡+15.165+E+122¡+02.620+");
        assertEquals("N 37¡ 15.165", latLonFromQuery[0]);
        assertEquals("E 122¡ 02.620", latLonFromQuery[1]);

        latLonFromQuery = Util.getLatLonFromQuery("GC1@N+3¡+15.165+E+122¡+02.620+");
        assertEquals("N 3¡ 15.165", latLonFromQuery[0]);
        assertEquals("E 122¡ 02.620", latLonFromQuery[1]);
    }

    public void testParseHttpUri() {
        UrlQuerySanitizer sanitizer = createMock(UrlQuerySanitizer.class);
        ValueSanitizer valueSanitizer = createMock(ValueSanitizer.class);
        sanitizer.registerParameters(Util.geocachingQueryParam, valueSanitizer);
        final String unsanitizedQuery = "#unsanitized_query";
        sanitizer.parseQuery(unsanitizedQuery);
        final String sanitizedQuery = "#sanitized_query";
        expect(sanitizer.getValue("q")).andReturn(sanitizedQuery);

        replay(sanitizer);
        assertEquals(sanitizedQuery, Util.parseHttpUri(unsanitizedQuery, sanitizer, valueSanitizer));
        verify(sanitizer);
    }

    public void testConvertDegreesToMinutes() {
        assertEquals("-122 30.000", Util.degreesToMinutes(-122.5));
        assertEquals("-122 30.600", Util.degreesToMinutes(-122.51));
        assertEquals("-122 03.000", Util.degreesToMinutes(-122.05));
        assertEquals("-0 03.000", Util.degreesToMinutes(-0.05));
    }

}
