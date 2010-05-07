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

package com.google.code.geobeagle.activity.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.database.DbFrontend;

import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import android.content.Intent;

public class IncomingIntentHandlerTest {

    @Test
    public void testIncomingIntentHandler_IntentNull() {
        Geocache geocache = PowerMock.createMock(Geocache.class);

        assertEquals(geocache, new IncomingIntentHandler(null, null, null)
                .maybeGetGeocacheFromIntent(null, geocache, null));
    }

    @Test
    public void testIncomingIntentHandler_Maps() {
        Intent intent = PowerMock.createMock(Intent.class);
        GeocacheFromIntentFactory geocacheFromIntentFactory = PowerMock
                .createMock(GeocacheFromIntentFactory.class);

        EasyMock.expect(intent.getAction()).andReturn(Intent.ACTION_VIEW);
        EasyMock.expect(intent.getType()).andReturn(null);
        EasyMock
                .expect(
                        geocacheFromIntentFactory.viewCacheFromMapsIntent(
                                intent, null)).andReturn(null);

        PowerMock.replayAll();
        assertNull(new IncomingIntentHandler(null, geocacheFromIntentFactory,
                null).maybeGetGeocacheFromIntent(intent, null, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testIncomingIntentHandler_NullCache() {
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeocacheFactory geocacheFactory = PowerMock
                .createMock(GeocacheFactory.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        EasyMock.expect(intent.getAction()).andReturn(
                GeocacheListController.SELECT_CACHE);
        EasyMock.expect(intent.getStringExtra("geocacheId")).andReturn("gc123");
        EasyMock.expect(dbFrontend.loadCacheFromId("gc123")).andReturn(null);
        EasyMock.expect(
                geocacheFactory.create("", "", 0, 0, Source.MY_LOCATION, "",
                        CacheType.NULL, 0, 0, 0)).andReturn(geocache);

        PowerMock.replayAll();
        assertEquals(geocache, new IncomingIntentHandler(geocacheFactory, null,
                dbFrontend).maybeGetGeocacheFromIntent(intent, null, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testIncomingIntentHandler_ActionSelect() {
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        EasyMock.expect(intent.getAction()).andReturn(
                GeocacheListController.SELECT_CACHE);
        EasyMock.expect(intent.getStringExtra("geocacheId")).andReturn("id1");
        EasyMock.expect(dbFrontend.loadCacheFromId("id1")).andReturn(geocache);

        PowerMock.replayAll();
        assertEquals(geocache,
                new IncomingIntentHandler(null, null, dbFrontend)
                        .maybeGetGeocacheFromIntent(intent, geocache, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testIncomingIntentHandler_ActionUnrecognized() {
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        EasyMock.expect(intent.getAction())
                .andReturn(Intent.ACTION_BATTERY_LOW);

        PowerMock.replayAll();
        assertEquals(geocache,
                new IncomingIntentHandler(null, null, dbFrontend)
                        .maybeGetGeocacheFromIntent(intent, geocache, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testIncomingIntentHandler_ActionNull() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        Intent intent = PowerMock.createMock(Intent.class);

        EasyMock.expect(intent.getAction()).andReturn(null);

        PowerMock.replayAll();
        assertEquals(geocache, new IncomingIntentHandler(null, null, null)
                .maybeGetGeocacheFromIntent(intent, geocache, null));
        PowerMock.verifyAll();
    }

}
