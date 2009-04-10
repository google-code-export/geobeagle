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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheFactory;
import com.google.code.geobeagle.data.GeocacheFactory.Source;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.View;

import junit.framework.TestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeoBeagle.class, Geocache.class
})
public class WebPageAndDetailsButtonEnablerTest extends TestCase {

    @Test
    public void testCheck() {
        GeoBeagle geoBeagle = EasyMock.createMock(GeoBeagle.class);
        Geocache geocache = EasyMock.createMock(Geocache.class);
        View cachePageButton = EasyMock.createMock(View.class);
        View detailsButton = EasyMock.createMock(View.class);

        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        EasyMock.expect(geocache.getSourceType()).andReturn(Source.GPX);
        EasyMock.expect(geocache.getContentProvider()).andReturn(GeocacheFactory.Provider.GROUNDSPEAK);
        cachePageButton.setEnabled(true);
        detailsButton.setEnabled(true);

        EasyMock.replay(geoBeagle);
        EasyMock.replay(geocache);
        WebPageAndDetailsButtonEnabler enabler = new WebPageAndDetailsButtonEnabler(geoBeagle,
                cachePageButton, detailsButton);
        enabler.check();
        PowerMock.verifyAll();
    }
}
