
package com.google.code.geobeagle;

import static org.junit.Assert.*;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;

@RunWith(PowerMockRunner.class)
public class LocationControlBufferedTest {
    @Test
    public void testGetLocation() {
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        Location location1 = PowerMock.createMock(Location.class);
        Location location2 = PowerMock.createMock(Location.class);

        EasyMock.expect(locationControl.getLocation()).andReturn(location1);
        EasyMock.expect(locationControl.getLocation()).andReturn(location2);

        PowerMock.replayAll();
        LocationControlBuffered locationControlBuffered = new LocationControlBuffered(
                locationControl);
        locationControlBuffered.onLocationChanged(null);
        assertEquals(location1, locationControlBuffered.getLocation());
        locationControlBuffered.onLocationChanged(null);
        assertEquals(location2, locationControlBuffered.getLocation());
        PowerMock.verifyAll();
    }

    @Test
    public void testMisc() {
        LocationControlBuffered locationControlBuffered = new LocationControlBuffered(null);
        locationControlBuffered.onProviderDisabled(null);
        locationControlBuffered.onProviderEnabled(null);
        locationControlBuffered.onStatusChanged(null, 0, null);
    }
}
