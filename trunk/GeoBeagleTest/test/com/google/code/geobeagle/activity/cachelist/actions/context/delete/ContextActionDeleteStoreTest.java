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

package com.google.code.geobeagle.activity.cachelist.actions.context.delete;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

@RunWith(PowerMockRunner.class)
public class ContextActionDeleteStoreTest {
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        sharedPreferences = createMock(SharedPreferences.class);
    }

    @Test
    public void testSaveCacheToDelete() {
        Editor editor = createMock(Editor.class);

        expect(sharedPreferences.edit()).andReturn(editor);
        expect(editor.putString(ContextActionDeleteStore.CACHE_TO_DELETE_ID, "GC123")).andReturn(
                editor);
        expect(editor.putString(ContextActionDeleteStore.CACHE_TO_DELETE_NAME, "My cache"))
                .andReturn(editor);
        expect(editor.commit()).andReturn(true);

        replayAll();
        new ContextActionDeleteStore(sharedPreferences).saveCacheToDelete("GC123", "My cache");
        verifyAll();
    }

}
