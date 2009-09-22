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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.activity.main.view.CacheButtonOnClickListener;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ActivityNotFoundException;

@RunWith(PowerMockRunner.class)
public class CacheButtonOnClickListenerTest {

    @Test
    public void testOnClick_ActivityNotFound() {
        CacheAction cacheAction = PowerMock.createMock(CacheAction.class);
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        ActivityNotFoundException activityNotFoundException = PowerMock
                .createMock(ActivityNotFoundException.class);

        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        cacheAction.act(geocache);
        EasyMock.expectLastCall().andThrow(activityNotFoundException);
        EasyMock.expect(activityNotFoundException.fillInStackTrace()).andReturn(
                activityNotFoundException);
        EasyMock.expect(activityNotFoundException.getMessage()).andReturn("no radar");
        errorDisplayer.displayError(R.string.error2, "no radar" , " problem");

        PowerMock.replayAll();
        new CacheButtonOnClickListener(cacheAction, geoBeagle, " problem", errorDisplayer).onClick(null);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnClick_RandomError() {
        CacheAction cacheAction = PowerMock.createMock(CacheAction.class);
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        NumberFormatException numberFormatException = PowerMock
                .createMock(NumberFormatException.class);

        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        cacheAction.act(geocache);
        EasyMock.expectLastCall().andThrow(numberFormatException);
        EasyMock.expect(numberFormatException.fillInStackTrace()).andReturn(numberFormatException);
        EasyMock.expect(numberFormatException.getMessage()).andReturn("random problem");
        errorDisplayer.displayError(R.string.error1, "random problem");

        PowerMock.replayAll();
        new CacheButtonOnClickListener(cacheAction, geoBeagle, " problem", errorDisplayer).onClick(null);
        PowerMock.verifyAll();
    }
}
