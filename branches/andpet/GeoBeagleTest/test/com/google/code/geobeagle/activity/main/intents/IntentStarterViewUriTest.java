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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.actions.CacheActionViewUri;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Intent;

@RunWith(PowerMockRunner.class)
public class IntentStarterViewUriTest {
    @Test
    public void testStartIntent() {
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        IntentFactory intentFactory = PowerMock.createMock(IntentFactory.class);
        GeocacheToUri geocacheToUri = PowerMock.createMock(GeocacheToUri.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        EasyMock.expect(intentFactory.createIntent(Intent.ACTION_VIEW, "destination uri"))
                .andReturn(intent);
        geoBeagle.startActivity(intent);

        PowerMock.replayAll();
        new CacheActionViewUri(geoBeagle, intentFactory, geocacheToUri).act(geocache);
        PowerMock.verifyAll();
    }

}
