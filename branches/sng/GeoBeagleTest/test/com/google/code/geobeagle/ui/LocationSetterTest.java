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
import com.google.code.geobeagle.io.LocationBookmarksSql;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;

import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class LocationSetterTest extends TestCase {

    public void testOnPause() {
        Editor editor = createMock(Editor.class);
        MockableEditText editText = createMock(MockableEditText.class);

        expect(editText.getText()).andReturn("googleplex");
        expect(editor.putString(LocationSetter.PREFS_LOCATION, "googleplex")).andReturn(editor);

        replay(editor);
        replay(editText);
        LocationSetter locationSetter = new LocationSetter(null, editText, null, null, null, null, null);
        locationSetter.onPause(editor);
        verify(editor);
        verify(editText);
    }

    public void testOnResume() {
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        MockableEditText editText = createMock(MockableEditText.class);
        LocationBookmarksSql locationBookmarksTextFile = createMock(LocationBookmarksSql.class);

        editText.setText("googleplex");
        expect(sharedPreferences.getString(LocationSetter.PREFS_LOCATION, "initial location"))
                .andReturn("googleplex");
        locationBookmarksTextFile.saveLocation("googleplex");

        replay(sharedPreferences);
        replay(editText);
        replay(locationBookmarksTextFile);
        LocationSetter locationSetter = new LocationSetter(null, editText, null, null,
                locationBookmarksTextFile, "initial location", null);
        locationSetter.onResume(sharedPreferences);
        verify(sharedPreferences);
        verify(editText);
        verify(locationBookmarksTextFile);
    }

    public void testSetMyLocation() {
        final Location location = createMock(Location.class);
        MockableEditText editText = createMock(MockableEditText.class);
        LocationBookmarksSql locationBookmarksTextFile = createMock(LocationBookmarksSql.class);
        LocationControl locationControl = createMock(LocationControl.class);

        editText.setText("37 07.380, 122 20.700 ([16:07] My Location)");
        expect(location.getLatitude()).andReturn(37.123);
        expect(location.getLongitude()).andReturn(122.345);
        expect(location.getTime()).andReturn(
                new GregorianCalendar(2008, 12, 5, 16, 7, 10).getTime().getTime());
        expect(locationControl.getLocation()).andReturn(location);
        locationBookmarksTextFile.saveLocation("37 07.380, 122 20.700 ([16:07] My Location)");

        replay(location);
        replay(editText);
        replay(locationBookmarksTextFile);
        replay(locationControl);
        LocationSetter locationSetter = new LocationSetter(null, editText, locationControl, null,
                locationBookmarksTextFile, null, null);
        locationSetter.setLocation(null);
        verify(location);
        verify(editText);
        verify(locationBookmarksTextFile);
        verify(locationControl);
    }

    public void testSetMyLocationNull() {
        MockableEditText editText = createMock(MockableEditText.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        LocationControl locationControl = createMock(LocationControl.class);

        expect(locationControl.getLocation()).andReturn(null);
        errorDisplayer.displayError(R.string.current_location_null);

        replay(locationControl);
        replay(editText);
        replay(errorDisplayer);
        LocationSetter locationSetter = new LocationSetter(null, editText, locationControl, null,
                null, null, errorDisplayer);
        locationSetter.setLocation(null);
        verify(editText);
        verify(errorDisplayer);
        verify(locationControl);
    }

}
