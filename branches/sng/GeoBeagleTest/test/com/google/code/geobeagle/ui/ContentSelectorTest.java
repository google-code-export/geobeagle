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
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Spinner;

@RunWith(PowerMockRunner.class)
public class ContentSelectorTest {

    @Test
    public void testGetIndex() {
        Spinner spinner = PowerMock.createMock(Spinner.class);
        expect(spinner.getSelectedItemPosition()).andReturn(17);

        PowerMock.replayAll();
        assertEquals(17, new ContentSelector(spinner, null).getIndex());
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() {
        Editor editor = PowerMock.createMock(Editor.class);
        Spinner spinner = PowerMock.createMock(Spinner.class);

        EasyMock.expect(spinner.getSelectedItemPosition()).andReturn(12);
        EasyMock.expect(editor.putInt(ContentSelector.CONTENT_PROVIDER, 12)).andReturn(editor);

        PowerMock.replayAll();
        new ContentSelector(spinner, null).onPause(editor);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        SharedPreferences preferences = PowerMock.createMock(SharedPreferences.class);
        Spinner spinner = PowerMock.createMock(Spinner.class);

        EasyMock.expect(preferences.getInt(ContentSelector.CONTENT_PROVIDER, 1)).andReturn(3);
        spinner.setSelection(3);

        PowerMock.replayAll();
        new ContentSelector(spinner, preferences).onResume(preferences);
        PowerMock.verifyAll();
    }
}
