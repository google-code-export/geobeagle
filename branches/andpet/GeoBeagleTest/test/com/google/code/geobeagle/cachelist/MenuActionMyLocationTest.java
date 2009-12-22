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

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionMyLocation;
import com.google.code.geobeagle.database.DbFrontend;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;
import java.util.TimeZone;

@RunWith(PowerMockRunner.class)
public class MenuActionMyLocationTest {

    @Test
    public void testAct() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProvider.class);
        CacheAction cacheAction = PowerMock.createMock(CacheAction.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        GeoFix geoFix = PowerMock.createMock(GeoFix.class);

        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

        EasyMock.expect(geoFix.getTime()).andReturn(1000L);
        EasyMock.expect(geoFixProvider.getLocation()).andReturn(geoFix);
        EasyMock.expect(geoFix.getLatitude()).andReturn(122.0);
        EasyMock.expect(geoFix.getLongitude()).andReturn(-37.0);

        EasyMock.expect(
                geocacheFactory.create("ML160001", "[16:00] My Location", 122.0, -37.0,
                        Source.MY_LOCATION, null, CacheType.MY_LOCATION, 0, 0, 0)).andReturn(
                geocache);
        EasyMock.expect(geocache.saveToDb(dbFrontend)).andReturn(true);
        cacheAction.act(geocache);

        PowerMock.replayAll();
        new MenuActionMyLocation(null, geocacheFactory, geoFixProvider, dbFrontend, null,
                cacheAction).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testActNullLocation() {
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProvider.class);

        EasyMock.expect(geoFixProvider.getLocation()).andReturn(null);
        
        PowerMock.replayAll();
        new MenuActionMyLocation(null, null, geoFixProvider, null, null, null).act();
        PowerMock.verifyAll();
    }
}
