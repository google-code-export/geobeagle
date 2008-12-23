
package com.google.code.geobeagle;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.content.SharedPreferences;

import junit.framework.TestCase;

public class LifecycleManagerImplTest extends TestCase {

    public void testOnPause() {
        GpsControl gpsControl = createMock(GpsControl.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        LifecycleManager lifecycleManager = new LifecycleManagerImpl(gpsControl, locationSetter,
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

        LifecycleManager lifecycleManager = new LifecycleManagerImpl(gpsControl, locationSetter,
                sharedPreferences);
        expect(
                sharedPreferences.getString(LifecycleManagerImpl.PREFS_LOCATION, "initial destination")).andReturn(
                "saved destination");
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
