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

public class IntentStarterViewUriTest extends TestCase {

    public void testStartIntent() {
        Context context = createMock(Context.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        DestinationToUri destinationToUri = createMock(DestinationToUri.class);
        Intent intent = createMock(Intent.class);

        Destination destination = createMock(Destination.class);
        expect(locationSetter.getDestination()).andReturn(destination);
        expect(destinationToUri.convert(destination)).andReturn("destination uri");
        expect(intentFactory.createIntent(Intent.ACTION_VIEW, "destination uri")).andReturn(intent);
        context.startActivity(intent);

        replay(locationSetter);
        replay(destinationToUri);
        replay(intentFactory);
        replay(context);
        new IntentStarterViewUri(context, intentFactory, locationSetter, destinationToUri)
                .startIntent();
        verify(locationSetter);
        verify(destinationToUri);
        verify(intentFactory);
        verify(context);
    }

}
