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

package com.google.code.geobeagle.activity.compass.intents;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.compass.intents.GeocacheToUri;
import com.google.code.geobeagle.activity.compass.intents.IntentStarterViewUri;
import com.google.code.geobeagle.cacheloader.CacheLoaderException;

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
    private CompassActivity compassActivity;
    private GeocacheToUri geocacheToUri;
    private Intent intent;
    private Geocache geocache;
    private ErrorDisplayer errorDisplayer;
    private Uri uri;

    @Before
    public void setUp() {
        compassActivity = createMock(CompassActivity.class);
        geocacheToUri = createMock(GeocacheToUri.class);
        intent = createMock(Intent.class);
        geocache = createMock(Geocache.class);
        errorDisplayer = createMock(ErrorDisplayer.class);
        uri = createMock(Uri.class);

        mockStatic(Uri.class);
    }

    @Test
    public void testStartIntent() throws Exception {
        expect(compassActivity.getGeocache()).andReturn(geocache);
        expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        expect(Uri.parse("destination uri")).andReturn(uri);
        expectNew(Intent.class, Intent.ACTION_VIEW, uri).andReturn(intent);
        compassActivity.startActivity(intent);
        replayAll();

        new IntentStarterViewUri(compassActivity, geocacheToUri, null).startIntent();
        verifyAll();
    }

    @Test
    public void testStartIntentNoHandler() throws Exception {
        ActivityNotFoundException activityNotFoundException = PowerMock
                .createMock(ActivityNotFoundException.class);

        expect(compassActivity.getGeocache()).andReturn(geocache);
        expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        expect(Uri.parse("destination uri")).andReturn(uri);
        expectNew(Intent.class, Intent.ACTION_VIEW, uri).andReturn(intent);
        compassActivity.startActivity(intent);
        expectLastCall().andThrow(activityNotFoundException);
        expect(activityNotFoundException.fillInStackTrace()).andReturn(
                activityNotFoundException);
        errorDisplayer.displayError(R.string.no_intent_handler, "destination uri");

        replayAll();
        new IntentStarterViewUri(compassActivity, geocacheToUri, errorDisplayer)
                .startIntent();
        verifyAll();
    }

    @Test
    public void ioExceptionShouldDisplayError() throws Exception {
        CacheLoaderException cacheLoaderException = PowerMock
                .createMock(CacheLoaderException.class);

        expect(compassActivity.getGeocache()).andReturn(geocache);
        expect(geocacheToUri.convert(geocache)).andThrow(cacheLoaderException);
        expect(cacheLoaderException.getError()).andReturn(R.string.error_loading_url);
        expect(cacheLoaderException.getArgs()).andReturn(new Object[0]);
        expect(cacheLoaderException.fillInStackTrace()).andReturn(cacheLoaderException);
        errorDisplayer.displayError(R.string.error_loading_url);

        replayAll();
        new IntentStarterViewUri(compassActivity, geocacheToUri, errorDisplayer).startIntent();
        verifyAll();
    }
}
