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
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.compass.GeocacheFromIntentFactory;
import com.google.code.geobeagle.activity.compass.Util;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.os.Bundle;
import android.util.Log;

@PrepareForTest( {
        Bundle.class, Uri.class, UrlQuerySanitizer.class,
        GeocacheFromIntentFactory.class, Log.class, Util.class
})
@RunWith(PowerMockRunner.class)
public class GeocacheFromIntentFactoryTest extends GeoBeagleTest {

    @Test
    public void testGeocacheFromIntentFactory() throws Exception {
        Intent intent = PowerMock.createMock(Intent.class);
        Uri uri = PowerMock.createMock(Uri.class);
        UrlQuerySanitizer urlQuerySanitizer = PowerMock.createMock(UrlQuerySanitizer.class);
        ValueSanitizer valueSanitizer = PowerMock.createMock(ValueSanitizer.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        LocationSaver locationSaver = PowerMock.createMock(LocationSaver.class);

        PowerMock.mockStatic(Util.class);
        PowerMock.mockStatic(UrlQuerySanitizer.class);

        EasyMock.expect(intent.getData()).andReturn(uri);
        EasyMock.expect(uri.getQuery()).andReturn("http://map_query");
        PowerMock.expectNew(UrlQuerySanitizer.class).andReturn(urlQuerySanitizer);
        EasyMock.expect(UrlQuerySanitizer.getAllButNulAndAngleBracketsLegal()).andReturn(
                valueSanitizer);
        EasyMock.expect(Util.parseHttpUri("http://map_query", urlQuerySanitizer, valueSanitizer))
                .andReturn("http://sanitized");
        String[] latlon = {
                "122", "37", "first", "second"
        };
        EasyMock.expect(Util.splitLatLonDescription("http://sanitized")).andReturn(latlon);
        EasyMock.expect(intent.getExtras()).andReturn(null);
        EasyMock.expect(Util.parseCoordinate("122")).andReturn(122.0);
        EasyMock.expect(Util.parseCoordinate("37")).andReturn(37.0);
        EasyMock.expect(
                geocacheFactory.create("first", "second", 122.0, 37.0, Source.WEB_URL, null,
                        CacheType.NULL, 0, 0, 0, true, false)).andReturn(geocache);
        locationSaver.saveLocation(geocache);
        EasyMock.expect(intent.putExtra(GeocacheFromIntentFactory.GEO_BEAGLE_SAVED_IN_DATABASE,
                true)).andReturn(intent);

        PowerMock.replayAll();
        assertEquals(geocache, new GeocacheFromIntentFactory(geocacheFactory, null)
                .viewCacheFromMapsIntent(intent, locationSaver, null));
        PowerMock.verifyAll();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGeocacheFromIntentFactoryBackButton() throws Exception {
        Intent intent = PowerMock.createMock(Intent.class);
        Uri uri = PowerMock.createMock(Uri.class);
        UrlQuerySanitizer urlQuerySanitizer = PowerMock.createMock(UrlQuerySanitizer.class);
        ValueSanitizer valueSanitizer = PowerMock.createMock(ValueSanitizer.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        LocationSaver locationSaver = PowerMock.createMock(LocationSaver.class);
        Bundle extras = PowerMock.createMock(Bundle.class);
        Provider<DbFrontend> dbFrontendProvider = PowerMock.createMock(Provider.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        PowerMock.mockStatic(Util.class);
        PowerMock.mockStatic(UrlQuerySanitizer.class);

        EasyMock.expect(intent.getData()).andReturn(uri);
        EasyMock.expect(uri.getQuery()).andReturn("http://map_query");
        PowerMock.expectNew(UrlQuerySanitizer.class).andReturn(urlQuerySanitizer);
        EasyMock.expect(UrlQuerySanitizer.getAllButNulAndAngleBracketsLegal()).andReturn(
                valueSanitizer);
        EasyMock.expect(Util.parseHttpUri("http://map_query", urlQuerySanitizer, valueSanitizer))
                .andReturn("http://sanitized");
        String[] latlon = {
                "122", "37", "first", "second"
        };
        EasyMock.expect(Util.splitLatLonDescription("http://sanitized")).andReturn(latlon);
        EasyMock.expect(intent.getExtras()).andReturn(extras);
        EasyMock.expect(extras.getBoolean(GeocacheFromIntentFactory.GEO_BEAGLE_SAVED_IN_DATABASE)).andReturn(true);
        EasyMock.expect(dbFrontendProvider.get()).andReturn(dbFrontend);
        EasyMock.expect(dbFrontend.getCache("first")).andReturn(geocache);
        
        PowerMock.replayAll();
        assertEquals(geocache, new GeocacheFromIntentFactory(geocacheFactory, dbFrontendProvider)
                .viewCacheFromMapsIntent(intent, locationSaver, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testGeocacheFromIntentFactoryBadQuery() throws Exception {
        Intent intent = PowerMock.createMock(Intent.class);
        Uri uri = PowerMock.createMock(Uri.class);
        UrlQuerySanitizer urlQuerySanitizer = PowerMock.createMock(UrlQuerySanitizer.class);
        ValueSanitizer valueSanitizer = PowerMock.createMock(ValueSanitizer.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache defaultGeocache = PowerMock.createMock(Geocache.class);
        LocationSaver locationSaver = PowerMock.createMock(LocationSaver.class);

        PowerMock.mockStatic(Util.class);
        PowerMock.mockStatic(UrlQuerySanitizer.class);

        EasyMock.expect(intent.getData()).andReturn(uri);
        EasyMock.expect(uri.getQuery()).andReturn("http://map_query");
        PowerMock.expectNew(UrlQuerySanitizer.class).andReturn(urlQuerySanitizer);
        EasyMock.expect(UrlQuerySanitizer.getAllButNulAndAngleBracketsLegal()).andReturn(
                valueSanitizer);
        EasyMock.expect(Util.parseHttpUri("http://map_query", urlQuerySanitizer, valueSanitizer))
                .andReturn(null);

        PowerMock.replayAll();
        assertEquals(defaultGeocache, new GeocacheFromIntentFactory(geocacheFactory, null)
                .viewCacheFromMapsIntent(intent, locationSaver, defaultGeocache));
        PowerMock.verifyAll();
    }
}
