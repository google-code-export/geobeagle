
package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.notNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;

import android.content.Context;
import android.location.Location;
import android.view.View.OnFocusChangeListener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class LocationSetterTest extends TestCase {
    private static final String LOCATION1 = "37 11.1 122 22.2 (foobar)";
    private static final String LOCATION2 = "38 33.3 122 44.4 (baz)";

    public void testSaveBookmarks() throws IOException {
        MockableEditText editText = createMock(MockableEditText.class);
        Context context = createMock(Context.class);
        FileOutputStream fileOutputStream = createMock(FileOutputStream.class);
        final BufferedOutputStream bufferedOutputStream = createStrictMock(BufferedOutputStream.class);

        editText.setOnFocusChangeListener((OnFocusChangeListener)notNull());
        editText.setText(LOCATION1);
        editText.setText(LOCATION2);

        expect(context.openFileOutput(LocationSetter.FNAME_RECENT_LOCATIONS, Context.MODE_PRIVATE))
                .andReturn(fileOutputStream);

        bufferedOutputStream.write(aryEq((LOCATION1 + "\n").getBytes()));
        bufferedOutputStream.write(aryEq((LOCATION2 + "\n").getBytes()));

        bufferedOutputStream.close();
        fileOutputStream.close();

        replay(editText);
        replay(context);
        replay(fileOutputStream);
        replay(bufferedOutputStream);
        LocationSetter locationSetter = new LocationSetter(context, editText, new LocationControl(null,
                null), new Pattern[] {}) {
            protected BufferedOutputStream createBufferedOutputStream(OutputStream outputStream) {
                return bufferedOutputStream;
            }
        };
        locationSetter.setLocation(LOCATION1, null);
        locationSetter.setLocation(LOCATION2, null);
        locationSetter.saveBookmarks();
        assertEquals(LOCATION1, locationSetter.getPreviousLocations().get(0));
        assertEquals(LOCATION2, locationSetter.getPreviousLocations().get(1));
        verify(editText);
        verify(context);
        verify(fileOutputStream);
        verify(bufferedOutputStream);
    }

    public void testSetMyLocation() {
        final Location location = createMock(Location.class);
        MockableEditText editText = createMock(MockableEditText.class);
        editText.setOnFocusChangeListener((OnFocusChangeListener)notNull());
        editText.setText("37 07.380, 122 20.700 ([16:07] My Location)");
        expect(location.getLatitude()).andReturn(37.123);
        expect(location.getLongitude()).andReturn(122.345);
        expect(location.getTime()).andReturn(
                new GregorianCalendar(2008, 12, 5, 16, 7, 10).getTime().getTime());

        replay(location);
        replay(editText);
        LocationSetter locationSetter = new LocationSetter(null, editText, new LocationControl(null,
                null) {
            public Location getLocation() {
                return location;
            }

        }, new Pattern[] {});
        locationSetter.setLocation(null, null);
        verify(location);
        verify(editText);
    }

    public void testSetMyLocationNull() {
        MockableEditText editText = createMock(MockableEditText.class);
        editText.setOnFocusChangeListener((OnFocusChangeListener)notNull());
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        errorDisplayer.displayError(R.string.current_location_null);

        replay(editText);
        replay(errorDisplayer);
        LocationSetter locationSetter = new LocationSetter(null, editText, new LocationControl(null,
                null) {
            public Location getLocation() {
                return null;
            }
        }, null);
        locationSetter.setLocation(null, errorDisplayer);
        verify(editText);
        verify(errorDisplayer);
    }

    public void testReadBookmarks() throws IOException {
        MockableEditText editText = createMock(MockableEditText.class);
        Context context = createMock(Context.class);
        FileInputStream fileInputStream = createMock(FileInputStream.class);
        expect(context.openFileInput(LocationSetter.FNAME_RECENT_LOCATIONS)).andReturn(
                fileInputStream);
        final InputStreamReader inputStreamReader = createMock(InputStreamReader.class);
        final BufferedReader bufferedReader = createMock(BufferedReader.class);
        expect(bufferedReader.readLine()).andReturn(null);
        fileInputStream.close();
        bufferedReader.close();
        inputStreamReader.close();

        replay(context);
        replay(fileInputStream);
        replay(inputStreamReader);
        replay(bufferedReader);
        LocationSetter locationSetter = new LocationSetter(context, editText, null, null) {
            protected InputStreamReader createInputStreamReader(FileInputStream fileInputStream) {
                return inputStreamReader;
            }

            protected BufferedReader createBufferedReader(InputStreamReader inputStreamReader) {
                return bufferedReader;
            }

            public void setLocation(CharSequence c, ErrorDisplayer errorDisplayer) {
            }
        };
        locationSetter.readBookmarks();
        verify(context);
        verify(fileInputStream);
        verify(inputStreamReader);
        verify(bufferedReader);
    }
}
