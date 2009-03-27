
package com.google.code.geobeagle.data;

import static org.junit.Assert.*;

import com.google.code.geobeagle.data.di.GeocacheFactory;

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

        EasyMock.expect(preferences.getInt("contentSelectorIndex", 1)).andReturn(0);
        EasyMock.expect(preferences.getString("id", "GCMEY7")).andReturn("GC123");
        EasyMock.expect(preferences.getString("name", "Google Falls")).andReturn("a cache");
        EasyMock.expect(preferences.getFloat("latitude", 37.42235f)).andReturn(37f);
        EasyMock.expect(preferences.getFloat("longitude", -122.082217f)).andReturn(-122f);
        EasyMock.expect(geocacheFactory.create(0, "GC123", "a cache", 37f, -122f)).andReturn(
                geocache);
        
        PowerMock.replayAll();
        GeocacheFromPreferencesFactory geocacheFromPreferencesFactory = new GeocacheFromPreferencesFactory(
                geocacheFactory);
        assertEquals(geocache, geocacheFromPreferencesFactory.create(preferences));
        PowerMock.verifyAll();
    }
}
