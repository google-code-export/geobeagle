/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.LocationSaver;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;

import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class LocationSetterTest extends TestCase {

    public void testOnPause() {
        Editor editor = createMock(Editor.class);
        MockableEditText editText = createMock(MockableEditText.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        expect(editText.getText()).andReturn("googleplex");
        expect(editor.putString(LocationSetter.PREFS_LOCATION, "googleplex")).andReturn(editor);
        locationSaver.saveLocation("googleplex");
        
        replay(editor);
        replay(editText);
        replay(locationSaver);
        LocationSetter locationSetter = new LocationSetter(null, editText, null, null, null, null,
                locationSaver);
        locationSetter.onPause(editor);
        verify(editor);
        verify(editText);
        verify(locationSaver);
    }

    public void testOnResume() {
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        MockableEditText editText = createMock(MockableEditText.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        editText.setText("googleplex");
        expect(sharedPreferences.getString(LocationSetter.PREFS_LOCATION, "initial location"))
                .andReturn("googleplex");

        replay(sharedPreferences);
        replay(editText);
        replay(locationSaver);
        new LocationSetter(null, editText, null, null, "initial location", null, locationSaver)
                .onResume(sharedPreferences);
        verify(sharedPreferences);
        verify(editText);
        verify(locationSaver);
    }

    public void testSetMyLocation() {
        final Location location = createMock(Location.class);
        MockableEditText editText = createMock(MockableEditText.class);
        LocationControl locationControl = createMock(LocationControl.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        editText.setText("37 07.380, 122 20.700 ([16:07] My Location)");
        expect(location.getLatitude()).andReturn(37.123);
        expect(location.getLongitude()).andReturn(122.345);
        expect(location.getTime()).andReturn(
                new GregorianCalendar(2008, 12, 5, 16, 7, 10).getTime().getTime());
        expect(locationControl.getLocation()).andReturn(location);
        locationSaver.saveLocation("37 07.380, 122 20.700 ([16:07] My Location)");

        replay(locationSaver);
        replay(location);
        replay(editText);
        replay(locationControl);
        new LocationSetter(null, editText, locationControl, null, null, null, locationSaver)
                .setLocation(null);
        verify(location);
        verify(editText);
        verify(locationControl);
        verify(locationSaver);

    }

    public void testSetMyLocationNull() {
        MockableEditText editText = createMock(MockableEditText.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        LocationControl locationControl = createMock(LocationControl.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        expect(locationControl.getLocation()).andReturn(null);
        errorDisplayer.displayError(R.string.current_location_null);

        replay(locationControl);
        replay(editText);
        replay(errorDisplayer);
        replay(locationSaver);
        LocationSetter locationSetter = new LocationSetter(null, editText, locationControl, null,
                null, errorDisplayer, locationSaver);
        locationSetter.setLocation(null);
        verify(editText);
        verify(errorDisplayer);
        verify(locationControl);
        verify(locationSaver);
    }

}
