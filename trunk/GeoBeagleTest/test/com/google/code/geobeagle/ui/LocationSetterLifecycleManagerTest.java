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
