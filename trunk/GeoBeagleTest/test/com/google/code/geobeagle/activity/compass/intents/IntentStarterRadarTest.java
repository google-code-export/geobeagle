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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.compass.fieldnotes.HasGeocache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Intent.class, IntentStarterGeo.class
})
public class IntentStarterRadarTest {

    @Test
    public void testStartIntent() throws Exception {
        Intent intent = PowerMock.createMock(Intent.class);
        HasGeocache hasGeocache = PowerMock.createMock(HasGeocache.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        Activity activity = PowerMock.createMock(Activity.class);
        
        expect(hasGeocache.get(activity)).andReturn(geocache);
        expect(geocache.getLatitude()).andReturn(37.175d);
        expect(intent.putExtra("latitude", 37.175f)).andReturn(intent);
        expect(geocache.getLongitude()).andReturn(122.8375d);
        expect(intent.putExtra("longitude", 122.8375f)).andReturn(intent);
        activity.startActivity(intent);

        PowerMock.replayAll();
        new IntentStarterGeo(activity, intent, hasGeocache).startIntent();
        PowerMock.verifyAll();
    }
}
