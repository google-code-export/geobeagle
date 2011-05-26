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

package com.google.code.geobeagle.activity.compass.view;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory.Provider;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.compass.view.CheckDetailsButton;
import com.google.code.geobeagle.activity.compass.view.WebPageMenuEnabler;
import com.google.code.geobeagle.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.view.View;

@RunWith(PowerMockRunner.class)
public class WebPageAndDetailsButtonEnablerTest {

    private Activity activity;
    private Geocache geocache;
    private View detailsButton;

    @Before
    public void setUp() {
        activity = createMock(Activity.class);
        geocache = createMock(Geocache.class);
        detailsButton = createMock(View.class);
    }

    @Test
    public void testCheckDetailsButtonGPX() {
        expect(activity.findViewById(R.id.cache_details)).andReturn(detailsButton);
        expect(geocache.getSourceType()).andReturn(Source.GPX);
        detailsButton.setEnabled(true);

        replayAll();
        new CheckDetailsButton(activity).check(geocache);
        verifyAll();
    }

    @Test
    public void testCheckDetailsButtonLOC() {
        expect(activity.findViewById(R.id.cache_details)).andReturn(detailsButton);
        expect(geocache.getSourceType()).andReturn(Source.LOC);
        detailsButton.setEnabled(false);

        replayAll();
        new CheckDetailsButton(activity).check(geocache);
        verifyAll();
    }

    @Test
    public void testCheckWebPageButtonAtlasQuest() {
        expect(geocache.getContentProvider()).andReturn(Provider.ATLAS_QUEST);

        replayAll();
        assertTrue(new WebPageMenuEnabler().shouldEnable(geocache));
    }

    @Test
    public void testCheckWebPageButtonGroundspeak() {
        expect(geocache.getContentProvider()).andReturn(Provider.GROUNDSPEAK);

        replayAll();
        assertTrue(new WebPageMenuEnabler().shouldEnable(geocache));
    }

    @Test
    public void testCheckWebPageButtonMyLocation() {
        expect(geocache.getContentProvider()).andReturn(Provider.MY_LOCATION);

        replayAll();
        assertFalse(new WebPageMenuEnabler().shouldEnable(geocache));
    }
}
