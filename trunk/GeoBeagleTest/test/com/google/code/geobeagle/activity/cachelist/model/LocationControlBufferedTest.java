
package com.google.code.geobeagle.activity.cachelist.model;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.GpsDisabledLocation;
import com.google.code.geobeagle.GpsEnabledLocation;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceSortStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.NullSortStrategy;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;
import android.location.LocationManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpsEnabledLocation.class, GeocacheVector.class
})
public class LocationControlBufferedTest {
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
        PowerMock.verifyAll();
    }

    public void testAzimuth() {

        LocationControlBuffered locationControlBuffered = new LocationControlBuffered(null, null,
                null, null);
        locationControlBuffered.setAzimuth(19f);
        assertEquals(19f, locationControlBuffered.getAzimuth());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetSortStrategyDistance() {
        DistanceSortStrategy distanceSortStrategy = PowerMock
                .createMock(DistanceSortStrategy.class);
        Location location = PowerMock.createMock(Location.class);
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        Provider<LocationManager> locationManagerProvider = PowerMock.createMock(Provider.class);

        EasyMock.expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
                .andReturn(location);
        EasyMock.expect(locationManagerProvider.get()).andReturn(locationManager);
        EasyMock.expect(location.getLatitude()).andReturn(37.0);
        EasyMock.expect(location.getLongitude()).andReturn(122.0);

        PowerMock.replayAll();
        final LocationControlBuffered locationControlBuffered = new LocationControlBuffered(
                locationManagerProvider,
                distanceSortStrategy, null, null);
        locationControlBuffered.onLocationChanged(null);
        assertEquals(distanceSortStrategy, locationControlBuffered.getSortStrategy());
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetSortStrategyNull() {
        NullSortStrategy nullSortStrategy = PowerMock.createMock(NullSortStrategy.class);
        Provider<LocationManager> locationManagerProvider = PowerMock.createMock(Provider.class);
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);

        EasyMock.expect(locationManagerProvider.get()).andReturn(locationManager);
        EasyMock.expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
                .andReturn(null);

        PowerMock.replayAll();
        final LocationControlBuffered locationControlBuffered = new LocationControlBuffered(
                locationManagerProvider,
                null, nullSortStrategy, null);
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
                null, null);
        locationControlBuffered.onProviderDisabled(null);
        locationControlBuffered.onProviderEnabled(null);
        locationControlBuffered.onStatusChanged(null, 0, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnLocationChangedNotNull() {
        GpsDisabledLocation gpsLocation = PowerMock.createMock(GpsDisabledLocation.class);
        Location location = PowerMock.createMock(Location.class);
        Provider<LocationManager> locationManagerProvider = PowerMock.createMock(Provider.class);
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);

        EasyMock.expect(locationManagerProvider.get()).andReturn(locationManager);
        EasyMock.expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
                .andReturn(location);
        // EasyMock.expect(locationControl.getLocation()).andReturn(location);
        EasyMock.expect(location.getLatitude()).andReturn(1.0);
        EasyMock.expect(location.getLongitude()).andReturn(2.0);

        PowerMock.replayAll();
        LocationControlBuffered locationControlBuffered = new LocationControlBuffered(
                locationManagerProvider, null,
                null, gpsLocation);
        locationControlBuffered.onLocationChanged(location);
        locationControlBuffered.getGpsLocation();
        PowerMock.verifyAll();
    }
}
