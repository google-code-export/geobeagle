
package com.google.code.geobeagle;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.LocationSetter;

import android.content.SharedPreferences;

import junit.framework.TestCase;

public class LifecycleManagerTest extends TestCase {

    public void testOnPause() {
        GpsControl gpsControl = createMock(GpsControl.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        LifecycleManager lifecycleManager = new LifecycleManager(gpsControl, locationSetter,
                sharedPreferences);
        locationSetter.save();
        expect(locationSetter.getLocation()).andReturn("somewhere");
        gpsControl.onPause();
        SharedPreferences.Editor editor = createMock(SharedPreferences.Editor.class);
        expect(sharedPreferences.edit()).andReturn(editor);
        expect(editor.putString("Location", "somewhere")).andReturn(editor);
        expect(editor.commit()).andReturn(true);

        replay(sharedPreferences);
        replay(editor);
        replay(locationSetter);
        replay(gpsControl);
        lifecycleManager.onPause();
        verify(gpsControl);
        verify(locationSetter);
        verify(sharedPreferences);
        verify(editor);
    }

    public void testOnResume() {
        GpsControl gpsControl = createMock(GpsControl.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        LifecycleManager lifecycleManager = new LifecycleManager(gpsControl, locationSetter,
                sharedPreferences);
        expect(
                sharedPreferences.getString(LifecycleManager.PREFS_LOCATION,
                        "initial destination")).andReturn("saved destination");
        locationSetter.load();
        locationSetter.setLocation("saved destination", errorDisplayer);
        gpsControl.onResume();

        replay(sharedPreferences);
        replay(locationSetter);
        replay(gpsControl);
        lifecycleManager.onResume(errorDisplayer, "initial destination");
        verify(sharedPreferences);
        verify(gpsControl);
        verify(locationSetter);
    }
}
