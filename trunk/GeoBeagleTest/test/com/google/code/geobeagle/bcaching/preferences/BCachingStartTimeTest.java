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

package com.google.code.geobeagle.bcaching.preferences;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;

@RunWith(PowerMockRunner.class)
public class BCachingStartTimeTest {

    private SharedPreferences sharedPreferences;
    private LastReadPosition lastReadPosition;
    private PreferencesWriter preferencesWriter;

    @Before
    public void setUp() {
        sharedPreferences = createMock(SharedPreferences.class);
        lastReadPosition = createMock(LastReadPosition.class);
        preferencesWriter = createMock(PreferencesWriter.class);
    }

    @Test
    public void testClearStartTime() {
        preferencesWriter.putLong(BCachingStartTime.BCACHING_START, 0);
        lastReadPosition.put(0);

        replayAll();
        new BCachingStartTime(sharedPreferences, preferencesWriter, lastReadPosition)
                .clearStartTime();
        verifyAll();
    }

    @Test
    public void testGetLastUpdateTime() {
        expect(sharedPreferences.getLong(BCachingStartTime.BCACHING_START, 0)).andReturn(8888L);

        replayAll();
        new BCachingStartTime(sharedPreferences, null, null).getLastUpdateTime();
        verifyAll();
    }

    @Test
    public void testPutNextStartTime() {
        preferencesWriter.putLong(BCachingStartTime.BCACHING_NEXT_START, 7777);

        replayAll();
        new BCachingStartTime(null, preferencesWriter, null).putNextStartTime(7777);
        verifyAll();
    }
}
