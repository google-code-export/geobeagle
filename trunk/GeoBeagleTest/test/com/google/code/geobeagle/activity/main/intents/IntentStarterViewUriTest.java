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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Uri.class, IntentStarterViewUri.class
})
public class IntentStarterViewUriTest {
    private GeoBeagle geoBeagle;
    private GeocacheToUri geocacheToUri;
    private Intent intent;
    private Geocache geocache;
    private ErrorDisplayer errorDisplayer;
    private Uri uri;

    @Before
    public void setUp() {
        geoBeagle = PowerMock.createMock(GeoBeagle.class);
        geocacheToUri = PowerMock.createMock(GeocacheToUri.class);
        intent = PowerMock.createMock(Intent.class);
        geocache = PowerMock.createMock(Geocache.class);
        errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        uri = PowerMock.createMock(Uri.class);

        PowerMock.mockStatic(Uri.class);
    }

    @Test
    public void testStartIntent() throws Exception {
        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        EasyMock.expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        EasyMock.expect(Uri.parse("destination uri")).andReturn(uri);
        PowerMock.expectNew(Intent.class, Intent.ACTION_VIEW, uri).andReturn(intent);
        geoBeagle.startActivity(intent);
        PowerMock.replayAll();

        new IntentStarterViewUri(geoBeagle, geocacheToUri, null).startIntent();
        PowerMock.verifyAll();
    }

    @Test
    public void testStartIntentNoHandler() throws Exception {
        ActivityNotFoundException activityNotFoundException = PowerMock
                .createMock(ActivityNotFoundException.class);

        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        EasyMock.expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        EasyMock.expect(Uri.parse("destination uri")).andReturn(uri);
        PowerMock.expectNew(Intent.class, Intent.ACTION_VIEW, uri).andReturn(intent);
        geoBeagle.startActivity(intent);
        EasyMock.expectLastCall().andThrow(activityNotFoundException);
        EasyMock.expect(activityNotFoundException.fillInStackTrace()).andReturn(
                activityNotFoundException);
        errorDisplayer.displayError(R.string.no_intent_handler, "destination uri");

        PowerMock.replayAll();
        new IntentStarterViewUri(geoBeagle, geocacheToUri, errorDisplayer)
                .startIntent();
        PowerMock.verifyAll();
    }
}
