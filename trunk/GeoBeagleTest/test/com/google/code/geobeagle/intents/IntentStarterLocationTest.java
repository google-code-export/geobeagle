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
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.ContentSelector;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.MyLocationProvider;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import junit.framework.TestCase;

public class IntentStarterLocationTest extends TestCase {

    public void testStartIntent() {
        Activity activity = createMock(Activity.class);
        MyLocationProvider myLocationProvider = createMock(MyLocationProvider.class);
        Location location = createMock(Location.class);
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        GetCoordsToast getCoordsToast = createMock(GetCoordsToast.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        Intent intent = createMock(Intent.class);
        ContentSelector contentSelector = createMock(ContentSelector.class);

        getCoordsToast.show();
        expect(myLocationProvider.getLocation()).andReturn(location);
        expect(location.getLatitude()).andReturn(123.45);
        expect(location.getLongitude()).andReturn(37.89);
        expect(contentSelector.getIndex()).andReturn(0);
        expect(resourceProvider.getStringArray(27)).andReturn(
                new String[] {
                        "http://www.geocaching.com/nearest.aspx?lat=%1$.5f&amp;lng=%2$.5f",
                        "http://www.atlasquest.com/results.html?gTypeId=2;gSort=5;gCoord=%1$.5f,%2$.5f"
                });
        activity.startActivity(intent);
        expect(
                intentFactory.createIntent(Intent.ACTION_VIEW,
                        "http://www.geocaching.com/nearest.aspx?lat=123.45000&amp;lng=37.89000"))
                .andReturn(intent);

        replay(activity);
        replay(resourceProvider);
        replay(intentFactory);
        replay(myLocationProvider);
        replay(location);
        replay(getCoordsToast);
        IntentStarterLocation intentStarterLocation = new IntentStarterLocation(activity,
                resourceProvider, intentFactory, myLocationProvider, contentSelector, 27,
                getCoordsToast);
        intentStarterLocation.startIntent();
        verify(activity);
        verify(resourceProvider);
        verify(intentFactory);
        verify(myLocationProvider);
        verify(location);
        verify(getCoordsToast);
    }
}
