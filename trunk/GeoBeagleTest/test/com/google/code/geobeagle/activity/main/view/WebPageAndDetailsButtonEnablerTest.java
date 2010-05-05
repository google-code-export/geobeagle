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

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory.Provider;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler.CheckButton;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler.CheckButtons;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler.CheckDetailsButton;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler.CheckWebPageButton;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.View;

@RunWith(PowerMockRunner.class)
public class WebPageAndDetailsButtonEnablerTest {
    @Test
    public void testCheck() {
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        CheckButtons checkButtons = PowerMock.createMock(CheckButtons.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        checkButtons.check(geocache);

        PowerMock.replayAll();
        WebPageAndDetailsButtonEnabler enabler = new WebPageAndDetailsButtonEnabler(geoBeagle,
                checkButtons);
        enabler.check();
        PowerMock.verifyAll();
    }

    @Test
    public void testCheckButtons() {
        CheckButton checkButton = PowerMock.createMock(CheckButton.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        checkButton.check(geocache);

        PowerMock.replayAll();
        CheckButtons checkButtons = new CheckButtons(new CheckButton[] {
            checkButton
        });
        checkButtons.check(geocache);
        PowerMock.verifyAll();
    }

    @Test
    public void testCheckDetailsButtonGPX() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        View detailsButton = PowerMock.createMock(View.class);

        EasyMock.expect(geocache.getSourceType()).andReturn(Source.GPX);
        detailsButton.setEnabled(true);

        PowerMock.replayAll();
        new CheckDetailsButton(detailsButton).check(geocache);
        PowerMock.verifyAll();
    }

    @Test
    public void testCheckDetailsButtonLOC() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        View detailsButton = PowerMock.createMock(View.class);

        EasyMock.expect(geocache.getSourceType()).andReturn(Source.LOC);
        detailsButton.setEnabled(false);

        PowerMock.replayAll();
        new CheckDetailsButton(detailsButton).check(geocache);
        PowerMock.verifyAll();
    }

    @Test
    public void testCheckWebPageButtonAtlasQuest() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        View webPageButton = PowerMock.createMock(View.class);

        EasyMock.expect(geocache.getContentProvider()).andReturn(Provider.ATLAS_QUEST);
        webPageButton.setEnabled(true);

        PowerMock.replayAll();
        new CheckWebPageButton(webPageButton).check(geocache);
        PowerMock.verifyAll();
    }

    @Test
    public void testCheckWebPageButtonGroundspeak() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        View webPageButton = PowerMock.createMock(View.class);

        EasyMock.expect(geocache.getContentProvider()).andReturn(Provider.GROUNDSPEAK);
        webPageButton.setEnabled(true);

        PowerMock.replayAll();
        new CheckWebPageButton(webPageButton).check(geocache);
        PowerMock.verifyAll();
    }

    @Test
    public void testCheckWebPageButtonMyLocation() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        View webPageButton = PowerMock.createMock(View.class);

        EasyMock.expect(geocache.getContentProvider()).andReturn(Provider.MY_LOCATION);
        webPageButton.setEnabled(false);

        PowerMock.replayAll();
        new CheckWebPageButton(webPageButton).check(geocache);
        PowerMock.verifyAll();
    }
}
