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

import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.ui.LocationSetter;

import android.content.Context;
import android.content.Intent;

import junit.framework.TestCase;

public class IntentStarterRadarTest extends TestCase {

    public void testStartIntent() {
        Intent intent = createMock(Intent.class);
        Context context = createMock(Context.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        Destination destination = createMock(Destination.class);

        expect(locationSetter.getDestination()).andReturn(destination);
        expect(intentFactory.createIntent("com.google.android.radar.SHOW_RADAR")).andReturn(intent);
        expect(destination.getLatitude()).andReturn(37.175d);
        expect(intent.putExtra("latitude", 37.175f)).andReturn(intent);
        expect(destination.getLongitude()).andReturn(122.8375d);
        expect(intent.putExtra("longitude", 122.8375f)).andReturn(intent);
        context.startActivity(intent);

        replay(locationSetter);
        replay(destination);
        replay(intentFactory);
        replay(intent);
        replay(context);
        new IntentStarterRadar(context, intentFactory, locationSetter).startIntent();
        verify(locationSetter);
        verify(destination);
        verify(intentFactory);
        verify(intent);
        verify(context);
    }
}
