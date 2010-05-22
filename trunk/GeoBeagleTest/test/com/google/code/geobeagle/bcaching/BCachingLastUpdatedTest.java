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

package com.google.code.geobeagle.bcaching;

import static org.easymock.EasyMock.expect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.easymock.PowerMock.*;

import android.content.SharedPreferences;

@RunWith(PowerMockRunner.class)
public class BCachingLastUpdatedTest {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        sharedPreferences = createMock(SharedPreferences.class);
        editor = createMock(SharedPreferences.Editor.class);
    }

    @Test
    public void testGetLastUpdateTime() {
        expect(sharedPreferences.getLong(BCachingLastUpdated.BCACHING_LAST_UPDATE, 0)).andReturn(
                8888L);

        replayAll();
        new BCachingLastUpdated(sharedPreferences).getLastUpdateTime();
        verifyAll();
    }

    @Test
    public void testPutLastUpdateTime() {
        expect(sharedPreferences.edit()).andReturn(editor);
        expect(editor.putLong(BCachingLastUpdated.BCACHING_LAST_UPDATE, 7777)).andReturn(editor);
        expect(editor.commit()).andReturn(true);

        replayAll();
        new BCachingLastUpdated(sharedPreferences).putLastUpdateTime(7777);
        verifyAll();
    }

    @Test
    public void testClearLastUpdateTime() {
        expect(sharedPreferences.edit()).andReturn(editor);
        expect(editor.putLong(BCachingLastUpdated.BCACHING_LAST_UPDATE, 0)).andReturn(editor);
        expect(editor.putInt(BCachingLastUpdated.BCACHING_LAST_READ, 0)).andReturn(editor);
        expect(editor.commit()).andReturn(true);

        replayAll();
        new BCachingLastUpdated(sharedPreferences).clearLastUpdateTime();
        verifyAll();
    }
}
