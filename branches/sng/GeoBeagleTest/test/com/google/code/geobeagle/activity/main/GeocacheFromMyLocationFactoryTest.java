
package com.google.code.geobeagle.activity.main;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;

import java.util.Locale;

@RunWith(PowerMockRunner.class)
public class GeocacheFromMyLocationFactoryTest {

    @Test
    public void testCreate() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Location location = PowerMock.createMock(Location.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        Locale.setDefault(Locale.ENGLISH);

        EasyMock.expect(locationControlBuffered.getLocation()).andReturn(location);
        EasyMock.expect(location.getTime()).andReturn(1000000L);
        EasyMock.expect(location.getLatitude()).andReturn(37.0);
        EasyMock.expect(location.getLongitude()).andReturn(-122.0);
        EasyMock.expect(
                geocacheFactory.create("ML161640", "[16:16] My Location", 37.0, -122.0,
                        Source.MY_LOCATION, null, CacheType.MY_LOCATION, 0, 0, 0)).andReturn(
                geocache);

        PowerMock.replayAll();
        assertEquals(geocache, new GeocacheFromMyLocationFactory(geocacheFactory,
                locationControlBuffered).create());
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateNullLocation() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);

        EasyMock.expect(locationControlBuffered.getLocation()).andReturn(null);

        PowerMock.replayAll();
        assertEquals(null, new GeocacheFromMyLocationFactory(null, locationControlBuffered)
                .create());
        PowerMock.verifyAll();
    }
}
