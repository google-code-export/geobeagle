
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
        GpsLifecycleManager gpsLifecycleManager = createMock(GpsLifecycleManager.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        LifecycleManager lifecycleManager = new LifecycleManager(gpsLifecycleManager, locationSetter,
                sharedPreferences);
        locationSetter.save();
        expect(locationSetter.getLocation()).andReturn("somewhere");
        gpsLifecycleManager.onPause();
        SharedPreferences.Editor editor = createMock(SharedPreferences.Editor.class);
        expect(sharedPreferences.edit()).andReturn(editor);
        expect(editor.putString("Location", "somewhere")).andReturn(editor);
        expect(editor.commit()).andReturn(true);

        replay(sharedPreferences);
        replay(editor);
        replay(locationSetter);
        replay(gpsLifecycleManager);
        lifecycleManager.onPause();
        verify(gpsLifecycleManager);
        verify(locationSetter);
        verify(sharedPreferences);
        verify(editor);
    }

    public void testOnResume() {
        GpsLifecycleManager gpsLifecycleManager = createMock(GpsLifecycleManager.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        LifecycleManager lifecycleManager = new LifecycleManager(gpsLifecycleManager, locationSetter,
                sharedPreferences);
        expect(
                sharedPreferences.getString(LifecycleManager.PREFS_LOCATION,
                        "initial destination")).andReturn("saved destination");
        locationSetter.load();
        locationSetter.setLocation("saved destination", errorDisplayer);
        gpsLifecycleManager.onResume();

        replay(sharedPreferences);
        replay(locationSetter);
        replay(gpsLifecycleManager);
        lifecycleManager.onResume(errorDisplayer, "initial destination");
        verify(sharedPreferences);
        verify(gpsLifecycleManager);
        verify(locationSetter);
    }
}
