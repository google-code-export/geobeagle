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

package com.google.code.geobeagle.mainactivity.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.mainactivity.GeoBeagle;
import com.google.code.geobeagle.mainactivity.ui.GeocacheViewer;

import org.junit.Test;

import android.content.Intent;

public class IntentStarterViewUriTest {
    @Test
    public void testStartIntent() {
        GeoBeagle geoBeagle = createMock(GeoBeagle.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        GeocacheViewer geocacheViewer = createMock(GeocacheViewer.class);
        GeocacheToUri geocacheToUri = createMock(GeocacheToUri.class);
        Intent intent = createMock(Intent.class);

        Geocache geocache = createMock(Geocache.class);
        expect(geoBeagle.getGeocache()).andReturn(geocache);
        expect(geocacheToUri.convert(geocache)).andReturn("destination uri");
        expect(intentFactory.createIntent(Intent.ACTION_VIEW, "destination uri")).andReturn(intent);
        geoBeagle.startActivity(intent);

        replay(geocacheViewer);
        replay(geocacheToUri);
        replay(intentFactory);
        replay(geoBeagle);
        new IntentStarterViewUri(geoBeagle, intentFactory, geocacheViewer, geocacheToUri)
                .startIntent();
        verify(geocacheViewer);
        verify(geocacheToUri);
        verify(intentFactory);
        verify(geoBeagle);
    }

}
