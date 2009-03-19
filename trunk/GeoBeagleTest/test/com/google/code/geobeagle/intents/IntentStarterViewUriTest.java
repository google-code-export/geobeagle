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

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.ui.LocationSetter;

import android.content.Context;
import android.content.Intent;

import junit.framework.TestCase;

public class IntentStarterViewUriTest extends TestCase {

    public void testStartIntent() {
        Context context = createMock(Context.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        GeocacheToUri geocacheToUri = createMock(GeocacheToUri.class);
        Intent intent = createMock(Intent.class);

        Geocache geocache = createMock(Geocache.class);
        expect(locationSetter.getGeocache()).andReturn(geocache);
        expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        expect(intentFactory.createIntent(Intent.ACTION_VIEW, "destination uri")).andReturn(intent);
        context.startActivity(intent);

        replay(locationSetter);
        replay(geocacheToUri);
        replay(intentFactory);
        replay(context);
        new IntentStarterViewUri(context, intentFactory, locationSetter, geocacheToUri)
                .startIntent();
        verify(locationSetter);
        verify(geocacheToUri);
        verify(intentFactory);
        verify(context);
    }

}
