
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import junit.framework.TestCase;

public class GpsControlTest extends TestCase {

    public void testGetLocation() {
        LocationManager locationManager = createMock(LocationManager.class);
        GpsControl gpsControl = new GpsControl(locationManager, null);
        Location location = createMock(Location.class);

        expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).andReturn(
                location);

        replay(locationManager);
        assertEquals(location, gpsControl.getLocation());
        verify(locationManager);
    }

    public void testOnPause() {
        LocationManager locationManager = createMock(LocationManager.class);
        LocationListener locationListener = createMock(LocationListener.class);
        GpsControl gpsControl = new GpsControl(locationManager, locationListener);

        locationManager.removeUpdates(locationListener);

        replay(locationManager);
        gpsControl.onPause();
        verify(locationManager);
    }

    public void testOnResume() {
        LocationManager locationManager = createMock(LocationManager.class);
        LocationListener locationListener = createMock(LocationListener.class);
        GpsControl gpsControl = new GpsControl(locationManager, locationListener);

        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        replay(locationManager);
        gpsControl.onResume();
        verify(locationManager);
    }

}
