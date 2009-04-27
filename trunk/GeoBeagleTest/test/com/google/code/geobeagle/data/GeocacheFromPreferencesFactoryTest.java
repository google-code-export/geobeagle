
package com.google.code.geobeagle.data;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.data.GeocacheFactory.Source;

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
        EasyMock.expect(preferences.getString(Geocache.NAME, "Google Falls")).andReturn("a cache");
        EasyMock.expect(preferences.getFloat(Geocache.LATITUDE, 37.42235f)).andReturn(37f);
        EasyMock.expect(preferences.getFloat(Geocache.LONGITUDE, -122.082217f)).andReturn(-122f);
        EasyMock.expect(preferences.getInt(Geocache.SOURCE_TYPE, -1)).andReturn(
                Source.MY_LOCATION.toInt());
        EasyMock.expect(geocacheFactory.sourceFromInt(Source.MY_LOCATION.toInt())).andReturn(
                Source.MY_LOCATION);
        EasyMock.expect(preferences.getString(Geocache.SOURCE_NAME, null)).andReturn(null);
        EasyMock.expect(
                geocacheFactory.create("GC123", "a cache", 37f, -122f, Source.MY_LOCATION, null))
                .andReturn(geocache);

        PowerMock.replayAll();
        GeocacheFromPreferencesFactory geocacheFromPreferencesFactory = new GeocacheFromPreferencesFactory(
                geocacheFactory);
        assertEquals(geocache, geocacheFromPreferencesFactory.create(preferences));
        PowerMock.verifyAll();
    }
}
