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
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.DbFrontend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionMyLocationTest {

    @Test
    public void testAct() {
        CacheListAdapter cacheListAdapter = PowerMock.createMock(CacheListAdapter.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        //ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        cacheListAdapter.forceRefresh();
        geocache.saveToDb(dbFrontend);

        PowerMock.replayAll();
        new MenuActionMyLocation(null, geocacheFactory, null, dbFrontend, null, null).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testActNullLocation() {
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        errorDisplayer.displayError(R.string.current_location_null);

        PowerMock.replayAll();
        new MenuActionMyLocation(errorDisplayer, null, null, null, null, null).act();
        PowerMock.verifyAll();
    }
}
