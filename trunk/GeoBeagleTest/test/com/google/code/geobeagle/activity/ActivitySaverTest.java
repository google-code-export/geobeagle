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

package com.google.code.geobeagle.activity;

import com.google.code.geobeagle.Geocache;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences.Editor;

@RunWith(PowerMockRunner.class)
public class ActivitySaverTest {
    @Test
    public void save() {
        Editor editor = PowerMock.createMock(Editor.class);

        EasyMock.expect(editor.putInt("lastActivity", ActivityType.CACHE_LIST.toInt())).andReturn(
                editor);
        EasyMock.expect(editor.commit()).andReturn(true);

        PowerMock.replayAll();
        new ActivitySaver(editor).save(ActivityType.CACHE_LIST);
        PowerMock.verifyAll();
    }

    @Test
    public void saveGeocache() {
        Editor editor = PowerMock.createMock(Editor.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(editor.putInt("lastActivity", ActivityType.CACHE_LIST.toInt())).andReturn(
                editor);
        geocache.writeToPrefs(editor);
        EasyMock.expect(editor.commit()).andReturn(true);

        PowerMock.replayAll();
        new ActivitySaver(editor).save(ActivityType.CACHE_LIST, geocache);
        PowerMock.verifyAll();
    }
}
