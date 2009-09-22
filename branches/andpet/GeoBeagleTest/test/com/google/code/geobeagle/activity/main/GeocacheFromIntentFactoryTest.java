
package com.google.code.geobeagle.activity.main;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.DbFrontend;

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

@PrepareForTest( {
        Uri.class, UrlQuerySanitizer.class, GeocacheFromIntentFactory.class, Util.class
})
@RunWith(PowerMockRunner.class)
public class GeocacheFromIntentFactoryTest {

    @Test
    public void geocacheFromIntentFactory() throws Exception {
        Intent intent = PowerMock.createMock(Intent.class);
        Uri uri = PowerMock.createMock(Uri.class);
        UrlQuerySanitizer urlQuerySanitizer = PowerMock.createMock(UrlQuerySanitizer.class);
        ValueSanitizer valueSanitizer = PowerMock.createMock(ValueSanitizer.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
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
        EasyMock.expect(Util.parseCoordinate("122")).andReturn(122.0);
        EasyMock.expect(Util.parseCoordinate("37")).andReturn(37.0);
        EasyMock.expect(
                geocacheFactory.create("first", "second", 122.0, 37.0, Source.WEB_URL, null,
                        CacheType.NULL, 0, 0, 0)).andReturn(geocache);
        geocache.saveLocation(dbFrontend);

        PowerMock.replayAll();
        assertEquals(geocache, new GeocacheFromIntentFactory(geocacheFactory, dbFrontend)
                .viewCacheFromMapsIntent(intent));
        PowerMock.verifyAll();
    }
}
