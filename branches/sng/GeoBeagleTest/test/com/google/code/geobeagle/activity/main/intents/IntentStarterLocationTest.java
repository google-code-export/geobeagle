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

package com.google.code.geobeagle.activity.main.intents;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.activity.main.view.MyLocationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class IntentStarterLocationTest {

    @Test
    public void testStartIntentNoLocation() {
        MyLocationProvider myLocationProvider = PowerMock.createMock(MyLocationProvider.class);

        expect(myLocationProvider.getLocation()).andReturn(null);

        PowerMock.replayAll();
        new IntentStarterLocation(null, null, null, myLocationProvider, null, 0, null)
                .startIntent();
        PowerMock.verifyAll();
    }
}
