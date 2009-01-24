
package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.LocationSetter;
import com.google.code.geobeagle.ui.LocationSetterLifecycleManager;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import junit.framework.TestCase;

public class LocationSetterLifecycleManagerTest extends TestCase {

    public void testOnPause() {
        LocationSetter locationSetter = createMock(LocationSetter.class);
        Editor editor = createMock(Editor.class);

        locationSetter.saveBookmarks();
        expect(locationSetter.getLocation()).andReturn("googleplex");
        expect(editor.putString(LocationSetterLifecycleManager.PREFS_LOCATION, "googleplex"))
                .andReturn(editor);

        replay(locationSetter);
        replay(editor);
        LocationSetterLifecycleManager lslm = new LocationSetterLifecycleManager(locationSetter,
                "initial location");
        lslm.onPause(editor);
        verify(locationSetter);
        verify(editor);
    }

    public void testOnResume() {
        LocationSetter locationSetter = createMock(LocationSetter.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        locationSetter.readBookmarks();
        expect(
                sharedPreferences.getString(LocationSetterLifecycleManager.PREFS_LOCATION,
                        "initial location")).andReturn("googleplex");
        locationSetter.setLocation("googleplex", errorDisplayer);

        replay(errorDisplayer);
        replay(locationSetter);
        replay(sharedPreferences);
        LocationSetterLifecycleManager lslm = new LocationSetterLifecycleManager(locationSetter,
                "initial location");
        lslm.onResume(sharedPreferences, errorDisplayer);
        verify(locationSetter);
        verify(sharedPreferences);
        verify(errorDisplayer);
    }
}
