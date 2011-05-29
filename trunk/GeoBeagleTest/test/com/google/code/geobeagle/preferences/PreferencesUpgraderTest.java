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

package com.google.code.geobeagle.preferences;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.preferences.Preferences;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

@RunWith(PowerMockRunner.class)
public class PreferencesUpgraderTest {
    @Test
    public void testPreferencesUpgraderEndToEnd() {
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        Editor editor = createMock(Editor.class);

        expect(sharedPreferences.getString(Preferences.BCACHING_USERNAME, "")).andReturn("tom");
        expect(sharedPreferences.edit()).andReturn(editor);
        expect(editor.putBoolean(Preferences.BCACHING_ENABLED, true)).andReturn(editor);
        expect(editor.commit()).andReturn(true);

        expect(sharedPreferences.getString(GeoBeagleEnvironment.IMPORT_FOLDER, "")).andReturn("");

        replayAll();
        PreferencesUpgrader preferencesUpgrader = new PreferencesUpgrader(new DependencyUpgrader(
                sharedPreferences));
        preferencesUpgrader.upgrade(14);
        verifyAll();
    }

    @Test
    public void testPreferencesUpgraderNewerVersion() {
        PreferencesUpgrader preferencesUpgrader = new PreferencesUpgrader(null);
        preferencesUpgrader.upgrade(15);
    }

    @Test
    public void testPreferencesUpgrader() {
        DependencyUpgrader dependencyUpgrader = createMock(DependencyUpgrader.class);

        dependencyUpgrader.upgrade(Preferences.BCACHING_ENABLED,
                Preferences.BCACHING_USERNAME);
        dependencyUpgrader.upgrade(Preferences.SDCARD_ENABLED,
                GeoBeagleEnvironment.IMPORT_FOLDER);

        replayAll();
        PreferencesUpgrader preferencesUpgrader = new PreferencesUpgrader(dependencyUpgrader);
        preferencesUpgrader.upgrade(14);
        verifyAll();
    }
}
