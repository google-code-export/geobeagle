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

package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.ContentSelector;
import com.google.code.geobeagle.ui.MyLocationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import java.util.Locale;

@RunWith(PowerMockRunner.class)
public class IntentStarterLocationTest {

    @Test
    public void testStartIntent() {
        Activity activity = PowerMock.createMock(Activity.class);
        MyLocationProvider myLocationProvider = PowerMock.createMock(MyLocationProvider.class);
        Location location = PowerMock.createMock(Location.class);
        ResourceProvider resourceProvider = PowerMock.createMock(ResourceProvider.class);
        Toast getCoordsToast = PowerMock.createMock(Toast.class);
        IntentFactory intentFactory = PowerMock.createMock(IntentFactory.class);
        Intent intent = PowerMock.createMock(Intent.class);
        ContentSelector contentSelector = PowerMock.createMock(ContentSelector.class);

        // Make sure this works even if decimal point symbol is "," and not ".".
        Locale.setDefault(Locale.GERMANY);
        getCoordsToast.show();
        expect(myLocationProvider.getLocation()).andReturn(location);
        expect(location.getLatitude()).andReturn(123.45);
        expect(location.getLongitude()).andReturn(37.89);
        expect(contentSelector.getIndex()).andReturn(0);
        expect(resourceProvider.getStringArray(R.id.nearest_objects)).andReturn(new String[] {
            "http://www.geocaching.com/nearest.aspx?lat=%1$.5f&amp;lng=%2$.5f",
        });
        activity.startActivity(intent);
        expect(
                intentFactory.createIntent(Intent.ACTION_VIEW,
                        "http://www.geocaching.com/nearest.aspx?lat=123.45000&amp;lng=37.89000"))
                .andReturn(intent);

        PowerMock.replayAll();
        new IntentStarterLocation(activity, resourceProvider, intentFactory, myLocationProvider,
                contentSelector, R.id.nearest_objects, getCoordsToast).startIntent();
        PowerMock.verifyAll();
    }

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
