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

package com.google.code.geobeagle.mainactivity;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.location.LocationLifecycleManager;
import com.google.code.geobeagle.mainactivity.ui.ContentSelector;

import org.junit.Test;

import android.content.SharedPreferences;

public class AppLifecycleManagerTest {

    @Test
    public void testOnPause() {
        LifecycleManager lifecycleManager1 = createMock(LifecycleManager.class);
        LifecycleManager lifecycleManager2 = createMock(LifecycleManager.class);
        SharedPreferences.Editor editor = createMock(SharedPreferences.Editor.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        lifecycleManager1.onPause(editor);
        lifecycleManager2.onPause(editor);
        expect(sharedPreferences.edit()).andReturn(editor);
        expect(editor.commit()).andReturn(true);

        replay(sharedPreferences);
        replay(editor);
        replay(lifecycleManager1);
        replay(lifecycleManager2);
        AppLifecycleManager appLifecycleManager = new AppLifecycleManager(sharedPreferences,
                new LifecycleManager[] {
                        lifecycleManager2, lifecycleManager1
                });
        appLifecycleManager.onPause();
        verify(sharedPreferences);
        verify(editor);
        verify(lifecycleManager1);
        verify(lifecycleManager2);
    }

    @Test
    public void testOnResume() {
        LifecycleManager gpsLifecycleManager = createMock(LocationLifecycleManager.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        ContentSelector contentSelector = createMock(ContentSelector.class);
        gpsLifecycleManager.onResume(sharedPreferences);
        contentSelector.onResume(sharedPreferences);

        replay(sharedPreferences);
        replay(gpsLifecycleManager);
        replay(contentSelector);
        AppLifecycleManager appLifecycleManager = new AppLifecycleManager(sharedPreferences,
                new LifecycleManager[] {
                        contentSelector, gpsLifecycleManager,
                });
        appLifecycleManager.onResume();
        verify(sharedPreferences);
        verify(gpsLifecycleManager);
        verify(contentSelector);
    }
}
