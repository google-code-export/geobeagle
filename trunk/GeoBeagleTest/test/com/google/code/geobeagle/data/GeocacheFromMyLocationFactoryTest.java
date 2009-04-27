
package com.google.code.geobeagle.data;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.data.GeocacheFactory.Source;

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
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        Location location = PowerMock.createMock(Location.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(locationControl.getLocation()).andReturn(location);
        EasyMock.expect(location.getTime()).andReturn(1000000L);
        EasyMock.expect(location.getLatitude()).andReturn(37.0);
        EasyMock.expect(location.getLongitude()).andReturn(-122.0);
        EasyMock.expect(
                geocacheFactory.create("ML161640", "[16:16] My Location", 37.0, -122.0,
                        Source.MY_LOCATION, null)).andReturn(geocache);

        PowerMock.replayAll();
        GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = new GeocacheFromMyLocationFactory(
                geocacheFactory, locationControl);
        assertEquals(geocache, geocacheFromMyLocationFactory.create());
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateNullLocation() {
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);

        EasyMock.expect(locationControl.getLocation()).andReturn(null);

        PowerMock.replayAll();
        GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = new GeocacheFromMyLocationFactory(
                null, locationControl);
        assertEquals(null, geocacheFromMyLocationFactory.create());
        PowerMock.verifyAll();
    }
}
