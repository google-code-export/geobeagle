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

package com.google.code.geobeagle.activity.cachelist.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest( {})
@RunWith(PowerMockRunner.class)
public class DistanceUpdaterTest {

    @Test
    public void testDistanceUpdater() {
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);

        geocacheListAdapter.notifyDataSetChanged();

        PowerMock.replayAll();
        new DistanceUpdater(geocacheListAdapter).refresh();
        PowerMock.verifyAll();
    }

}
