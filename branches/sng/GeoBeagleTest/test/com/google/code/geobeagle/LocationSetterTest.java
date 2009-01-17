
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.notNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.LocationSetter;
import com.google.code.geobeagle.ui.MockableEditText;

import android.location.Location;
import android.view.View.OnFocusChangeListener;

import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class LocationSetterTest extends TestCase {
    private static final String LOCATION1 = "37 11.1 122 22.2 #foobar";

    private static final String LOCATION2 = "38 33.3 122 44.4 #baz";

    public void testSaveLocation() {
        MockableEditText editText = createMock(MockableEditText.class);
        editText.setOnFocusChangeListener((OnFocusChangeListener)notNull());
        editText.setText(LOCATION1);
        editText.setText(LOCATION2);

        replay(editText);
        LocationSetter lsi = new LocationSetter(null, editText, new GpsControl(null, null) {
            public Location getLocation() {
                assertTrue(false);
                return null;
            }
        });
        lsi.setLocation(LOCATION1, null);
        lsi.setLocation(LOCATION2, null);
        assertEquals(LOCATION1, lsi.getPreviousLocations().get(0));
        assertEquals(LOCATION2, lsi.getPreviousLocations().get(1));
        verify(editText);
    }

    public void testMyLocation() {
        final Location location = createMock(Location.class);
        MockableEditText editText = createMock(MockableEditText.class);
        editText.setOnFocusChangeListener((OnFocusChangeListener)notNull());
        editText.setText("37 07.380  122 20.700 # [16:07] My Location");
        expect(location.getLatitude()).andReturn(37.123);
        expect(location.getLongitude()).andReturn(122.345);
        expect(location.getTime()).andReturn(
                new GregorianCalendar(2008, 12, 5, 16, 7, 10).getTime().getTime());

        replay(location);
        replay(editText);
        LocationSetter lsi = new LocationSetter(null, editText, new GpsControl(null, null) {
            public Location getLocation() {
                return location;
            }

        });
        lsi.setLocation(null, null);
        verify(location);
        verify(editText);
    }

    public void testMyLocationNull() {
        MockableEditText editText = createMock(MockableEditText.class);
        editText.setOnFocusChangeListener((OnFocusChangeListener)notNull());
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        errorDisplayer.displayError(R.string.current_location_null);

        replay(editText);
        replay(errorDisplayer);
        LocationSetter locationSetter = new LocationSetter(null, editText, new GpsControl(null,
                null) {
            public Location getLocation() {
                return null;
            }
        });
        locationSetter.setLocation(null, errorDisplayer);
        verify(editText);
        verify(errorDisplayer);
    }
}
