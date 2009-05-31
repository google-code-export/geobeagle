
package com.google.code.geobeagle.location;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.cachelist.GeocacheVector;
import com.google.code.geobeagle.cachelist.GeocacheVector.DistanceSortStrategy;
import com.google.code.geobeagle.cachelist.GeocacheVector.NullSortStrategy;
import com.google.code.geobeagle.location.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.location.LocationControlBuffered.GpsEnabledLocation;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpsEnabledLocation.class, GeocacheVector.class
})
public class LocationControlBufferedTest {
    @Test
    public void getDisabledDistance() {
        assertEquals(Float.MAX_VALUE, new GpsDisabledLocation()
                .distanceToGpsDisabledLocation((GpsDisabledLocation)null), 0f);
        assertEquals(Float.MAX_VALUE, new GpsDisabledLocation()
                .distanceToGpsEnabledLocation((GpsEnabledLocation)null), 0f);
    }

    @Test
    public void getEnabledDistance() {
        GpsDisabledLocation gpsDisabledLocation = new GpsDisabledLocation();
        GpsEnabledLocation gpsEnabledLocation1 = new GpsEnabledLocation(1, 2);
        GpsEnabledLocation gpsEnabledLocation2 = new GpsEnabledLocation(3, 4);

        PowerMock.mockStatic(GeocacheVector.class);
        EasyMock.expect(GeocacheVector.calculateDistanceFast(3, 4, 1, 2)).andReturn(5f);

        PowerMock.replayAll();

        assertEquals(5f, gpsEnabledLocation1.distanceTo(gpsEnabledLocation2), 0f);
        assertEquals(Float.MAX_VALUE, gpsEnabledLocation1.distanceTo(gpsDisabledLocation), 0f);
        assertEquals(Float.MAX_VALUE, gpsEnabledLocation1
                .distanceToGpsDisabledLocation(gpsDisabledLocation), 0f);
        PowerMock.verifyAll();
    }

    public void testAzimuth() {

        LocationControlBuffered locationControlBuffered = new LocationControlBuffered(null, null,
                null, null, null, null);
        locationControlBuffered.setAzimuth(19f);
        assertEquals(19f, locationControlBuffered.getAzimuth());
    }

    @Test
    public void testGetSortStrategyDistance() {
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        DistanceSortStrategy distanceSortStrategy = PowerMock
                .createMock(DistanceSortStrategy.class);
        Location location = PowerMock.createMock(Location.class);

        EasyMock.expect(locationControl.getLocation()).andReturn(location);

        PowerMock.replayAll();
        final LocationControlBuffered locationControlBuffered = new LocationControlBuffered(
                locationControl, distanceSortStrategy, null, null, null, location);
        locationControlBuffered.onLocationChanged(null);
        assertEquals(distanceSortStrategy, locationControlBuffered.getSortStrategy());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetSortStrategyNull() {
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        NullSortStrategy nullSortStrategy = PowerMock.createMock(NullSortStrategy.class);

        EasyMock.expect(locationControl.getLocation()).andReturn(null);

        PowerMock.replayAll();
        final LocationControlBuffered locationControlBuffered = new LocationControlBuffered(
                locationControl, null, nullSortStrategy, null, null, null);
        locationControlBuffered.onLocationChanged(null);
        assertEquals(nullSortStrategy, locationControlBuffered.getSortStrategy());
        PowerMock.verifyAll();
    }

    @Test
    public void testGpsDisabledLocation() {
        final GpsDisabledLocation gpsDisabledLocation = new GpsDisabledLocation();
        assertEquals(Float.MAX_VALUE, gpsDisabledLocation.distanceTo(null), 0f);
        assertEquals(Float.MAX_VALUE, gpsDisabledLocation.distanceTo(null), 0f);
    }

    @Test
    public void testMisc() {
        LocationControlBuffered locationControlBuffered = new LocationControlBuffered(null, null,
                null, null, null, null);
        locationControlBuffered.onProviderDisabled(null);
        locationControlBuffered.onProviderEnabled(null);
        locationControlBuffered.onStatusChanged(null, 0, null);
    }

    @Test
    public void testOnLocationChangedNotNull() {
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        GpsDisabledLocation gpsLocation = PowerMock.createMock(GpsDisabledLocation.class);
        Location location = PowerMock.createMock(Location.class);

        EasyMock.expect(locationControl.getLocation()).andReturn(location);
        EasyMock.expect(location.getLatitude()).andReturn(1.0);
        EasyMock.expect(location.getLongitude()).andReturn(2.0);

        PowerMock.replayAll();
        LocationControlBuffered locationControlBuffered = new LocationControlBuffered(
                locationControl, null, null, gpsLocation, gpsLocation, location);
        locationControlBuffered.onLocationChanged(location);
        locationControlBuffered.getGpsLocation();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnLocationChangedNull() {
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        GpsDisabledLocation gpsLocation = PowerMock.createMock(GpsDisabledLocation.class);
        Location location1 = PowerMock.createMock(Location.class);
        Location location2 = PowerMock.createMock(Location.class);

        EasyMock.expect(locationControl.getLocation()).andReturn(location1);
        EasyMock.expect(locationControl.getLocation()).andReturn(location2);

        PowerMock.replayAll();
        LocationControlBuffered locationControlBuffered = new LocationControlBuffered(
                locationControl, null, null, gpsLocation, gpsLocation, location2);
        locationControlBuffered.onLocationChanged(null);
        assertEquals(location1, locationControlBuffered.getLocation());
        locationControlBuffered.onLocationChanged(null);
        assertEquals(location2, locationControlBuffered.getLocation());
        assertEquals(gpsLocation, locationControlBuffered.getGpsLocation());
        PowerMock.verifyAll();
    }
}
