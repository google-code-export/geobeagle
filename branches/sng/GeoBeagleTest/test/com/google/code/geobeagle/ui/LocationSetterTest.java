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
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.di.GeocacheFromTextFactory;
import com.google.code.geobeagle.io.LocationSaver;

import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.widget.TextView;

import java.util.GregorianCalendar;

import junit.framework.TestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    TextView.class
})
public class LocationSetterTest extends TestCase {

    public void testGetGeocache() {
        TextView textView = PowerMock.createMock(TextView.class);
        GeocacheFromTextFactory geocacheFromTextFactory = PowerMock
                .createMock(GeocacheFromTextFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        expect(textView.getText()).andReturn("GC123");
        expect(geocacheFromTextFactory.create("GC123")).andReturn(geocache);

        PowerMock.replayAll();
        GeocacheViewer geocacheViewer = new GeocacheViewer(null, textView, null,
                geocacheFromTextFactory, null, null, null);
        geocacheViewer.getGeocache();
        PowerMock.verifyAll();
    }

    public void testOnPause() {
        Editor editor = createMock(Editor.class);
        TextView textView = createMock(TextView.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        expect(textView.getText()).andReturn("googleplex");
        expect(editor.putString(GeocacheViewer.PREFS_LOCATION, "googleplex")).andReturn(editor);
        locationSaver.saveLocation("googleplex");

        replay(editor);
        replay(textView);
        replay(locationSaver);
        GeocacheViewer geocacheViewer = new GeocacheViewer(null, textView, null, null, null, null,
                locationSaver);
        geocacheViewer.onPause(editor);
        verify(editor);
        verify(textView);
        verify(locationSaver);
    }

    public void testOnResume() {
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        TextView textView = PowerMock.createMock(TextView.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        textView.setText("googleplex");
        expect(sharedPreferences.getString(GeocacheViewer.PREFS_LOCATION, "initial location"))
                .andReturn("googleplex");

        replay(sharedPreferences);
        replay(textView);
        replay(locationSaver);
        new GeocacheViewer(null, textView, null, null, "initial location", null, locationSaver)
                .onResume(sharedPreferences);
        verify(sharedPreferences);
        verify(textView);
        verify(locationSaver);
    }

    public void testSetMyLocation() {
        final Location location = createMock(Location.class);
        TextView textView = PowerMock.createMock(TextView.class);
        LocationControl locationControl = createMock(LocationControl.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        textView.setText("37 07.380, 122 20.700 ([16:07] My Location)");
        expect(location.getLatitude()).andReturn(37.123);
        expect(location.getLongitude()).andReturn(122.345);
        expect(location.getTime()).andReturn(
                new GregorianCalendar(2008, 12, 5, 16, 7, 10).getTime().getTime());
        expect(locationControl.getLocation()).andReturn(location);
        locationSaver.saveLocation("37 07.380, 122 20.700 ([16:07] My Location)");

        replay(locationSaver);
        replay(location);
        replay(textView);
        replay(locationControl);
        new GeocacheViewer(null, textView, locationControl, null, null, null, locationSaver)
                .setLocation(null);
        verify(location);
        verify(textView);
        verify(locationControl);
        verify(locationSaver);

    }

    public void testSetMyLocationNull() {
        TextView textView = createMock(TextView.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        LocationControl locationControl = createMock(LocationControl.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        expect(locationControl.getLocation()).andReturn(null);
        errorDisplayer.displayError(R.string.current_location_null);

        replay(locationControl);
        replay(textView);
        replay(errorDisplayer);
        replay(locationSaver);
        GeocacheViewer geocacheViewer = new GeocacheViewer(null, textView, locationControl, null,
                null, errorDisplayer, locationSaver);
        geocacheViewer.setLocation(null);
        verify(textView);
        verify(errorDisplayer);
        verify(locationControl);
        verify(locationSaver);
    }

}
