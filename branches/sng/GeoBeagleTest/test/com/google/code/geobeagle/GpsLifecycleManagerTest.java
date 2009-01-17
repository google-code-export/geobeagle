
package com.google.code.geobeagle;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.location.LocationListener;
import android.location.LocationManager;

import junit.framework.TestCase;

public class GpsLifecycleManagerTest extends TestCase {

    public void testOnPause() {
        LocationManager locationManager = createMock(LocationManager.class);
        LocationListener locationListener = createMock(LocationListener.class);
        GpsLifecycleManager gpsLifecycleManager = new GpsLifecycleManager(locationListener,
                locationManager);

        locationManager.removeUpdates(locationListener);

        replay(locationManager);
        gpsLifecycleManager.onPause();
        verify(locationManager);
    }

    public void testOnResume() {
        LocationManager locationManager = createMock(LocationManager.class);
        LocationListener locationListener = createMock(LocationListener.class);
        GpsLifecycleManager gpsLifecycleManager = new GpsLifecycleManager(locationListener,
                locationManager);

        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);

        replay(locationManager);
        gpsLifecycleManager.onResume();
        verify(locationManager);
    }

}
