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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ActivityNotFoundException;
import android.content.Intent;

@RunWith(PowerMockRunner.class)
public class IntentStarterViewUriTest {
    private GeoBeagle geoBeagle;
    private IntentFactory intentFactory;
    private GeocacheToUri geocacheToUri;
    private Intent intent;
    private Geocache geocache;
    private ErrorDisplayer errorDisplayer;

    @Before
    public void setUp() {
        geoBeagle = PowerMock.createMock(GeoBeagle.class);
        intentFactory = PowerMock.createMock(IntentFactory.class);
        geocacheToUri = PowerMock.createMock(GeocacheToUri.class);
        intent = PowerMock.createMock(Intent.class);
        geocache = PowerMock.createMock(Geocache.class);
        errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
    }
    
    @Test
    public void testStartIntent() {
        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        EasyMock.expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        EasyMock.expect(intentFactory.createIntent(Intent.ACTION_VIEW, "destination uri"))
                .andReturn(intent);
        geoBeagle.startActivity(intent);

        PowerMock.replayAll();
        new IntentStarterViewUri(geoBeagle, intentFactory, geocacheToUri, null).startIntent();
        PowerMock.verifyAll();
    }

    @Test
    public void testStartIntentNoHandler() {
        ActivityNotFoundException activityNotFoundException = PowerMock
                .createMock(ActivityNotFoundException.class);

        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        EasyMock.expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        EasyMock.expect(intentFactory.createIntent(Intent.ACTION_VIEW, "destination uri"))
                .andReturn(intent);
        geoBeagle.startActivity(intent);
        EasyMock.expectLastCall().andThrow(activityNotFoundException);
        EasyMock.expect(activityNotFoundException.fillInStackTrace()).andReturn(
                activityNotFoundException);
        errorDisplayer.displayError(R.string.no_intent_handler, "destination uri");

        PowerMock.replayAll();
        new IntentStarterViewUri(geoBeagle, intentFactory, geocacheToUri, errorDisplayer)
                .startIntent();
        PowerMock.verifyAll();
    }
}
