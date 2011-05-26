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

package com.google.code.geobeagle.cachelist;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.EditCacheActivity;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.database.LocationSaver;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    Intent.class, MenuActionMyLocation.class
})
public class MenuActionMyLocationTest {

    @Test
    public void testAct() throws Exception {
        GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = PowerMock
                .createMock(GeocacheFromMyLocationFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        LocationSaver locationSaver = PowerMock.createMock(LocationSaver.class);
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);
        
        EasyMock.expect(geocacheFromMyLocationFactory.create()).andReturn(geocache);
        locationSaver.saveLocation(geocache);
        PowerMock.expectNew(Intent.class, activity, EditCacheActivity.class).andReturn(intent);
        EasyMock.expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        activity.startActivityForResult(intent, 0);
        
        PowerMock.replayAll();
        final MenuActionMyLocation menuActionMyLocation = new MenuActionMyLocation(
                activity, null, geocacheFromMyLocationFactory, locationSaver);
        menuActionMyLocation.act();
        PowerMock.verifyAll();
    }

    @Test
    public void testActNullLocation() {
        GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = PowerMock
                .createMock(GeocacheFromMyLocationFactory.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        EasyMock.expect(geocacheFromMyLocationFactory.create()).andReturn(null);
        errorDisplayer.displayError(R.string.current_location_null);

        PowerMock.replayAll();
        new MenuActionMyLocation(null, errorDisplayer, geocacheFromMyLocationFactory, null).act();
        PowerMock.verifyAll();
    }
}
