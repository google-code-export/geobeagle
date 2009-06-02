
package com.google.code.geobeagle.activity.main;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.activity.cachelist.model.LocationControlBuffered;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;

@RunWith(PowerMockRunner.class)
public class GeocacheFromMyLocationFactoryTest {

    @Test
    public void testCreate() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Location location = PowerMock.createMock(Location.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(locationControlBuffered.getLocation()).andReturn(location);
        EasyMock.expect(location.getTime()).andReturn(1000000L);
        EasyMock.expect(location.getLatitude()).andReturn(37.0);
        EasyMock.expect(location.getLongitude()).andReturn(-122.0);
        EasyMock.expect(
                geocacheFactory.create("ML161640", "[16:16] My Location", 37.0, -122.0,
                        Source.MY_LOCATION, null)).andReturn(geocache);

        PowerMock.replayAll();
        GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = new GeocacheFromMyLocationFactory(
                geocacheFactory, locationControlBuffered);
        assertEquals(geocache, geocacheFromMyLocationFactory.create());
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateNullLocation() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);

        EasyMock.expect(locationControlBuffered.getLocation()).andReturn(null);

        PowerMock.replayAll();
        GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = new GeocacheFromMyLocationFactory(
                null, locationControlBuffered);
        assertEquals(null, geocacheFromMyLocationFactory.create());
        PowerMock.verifyAll();
    }
}
