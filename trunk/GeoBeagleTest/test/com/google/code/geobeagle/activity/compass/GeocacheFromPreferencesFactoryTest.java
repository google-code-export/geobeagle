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
package com.google.code.geobeagle.activity.compass;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.compass.GeocacheFromPreferencesFactory;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;

@RunWith(PowerMockRunner.class)
public class GeocacheFromPreferencesFactoryTest {
    @Test
    public void testCreate() {
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        SharedPreferences preferences = PowerMock.createMock(SharedPreferences.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(preferences.getString(Geocache.ID, "GCMEY7")).andReturn("GC123");
        EasyMock.expect(preferences.getString(Geocache.NAME,  "Google Falls")).andReturn("a cache");
        EasyMock.expect(preferences.getFloat(Geocache.LATITUDE, 0)).andReturn(37f);
        EasyMock.expect(preferences.getFloat(Geocache.LONGITUDE, 0)).andReturn(-122f);
        EasyMock.expect(preferences.getInt(Geocache.SOURCE_TYPE, -1)).andReturn(
                Source.MY_LOCATION.toInt());
        EasyMock.expect(geocacheFactory.sourceFromInt(Source.MY_LOCATION.toInt())).andReturn(
                Source.MY_LOCATION);
        EasyMock.expect(preferences.getString(Geocache.SOURCE_NAME, "")).andReturn(null);
        EasyMock.expect(preferences.getInt(Geocache.CACHE_TYPE, 0)).andReturn(1);
        EasyMock.expect(geocacheFactory.cacheTypeFromInt(1)).andReturn(CacheType.TRADITIONAL);
        EasyMock.expect(preferences.getInt(Geocache.DIFFICULTY, 0)).andReturn(2);
        EasyMock.expect(preferences.getInt(Geocache.CONTAINER, 0)).andReturn(3);
        EasyMock.expect(preferences.getInt(Geocache.TERRAIN, 0)).andReturn(4);
        EasyMock.expect(preferences.getBoolean("available", true)).andReturn(true);
        EasyMock.expect(preferences.getBoolean("archived", false)).andReturn(false);
        EasyMock.expect(
                geocacheFactory.create("GC123", "a cache", 37f, -122f, Source.MY_LOCATION, null,
                        CacheType.TRADITIONAL, 2, 4, 3, true, false)).andReturn(geocache);

        PowerMock.replayAll();
        GeocacheFromPreferencesFactory geocacheFromPreferencesFactory = new GeocacheFromPreferencesFactory(
                geocacheFactory);
        assertEquals(geocache, geocacheFromPreferencesFactory.create(preferences));
        PowerMock.verifyAll();
    }
}
