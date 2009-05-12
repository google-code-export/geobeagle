/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.LocationListener;
import android.location.LocationManager;

@RunWith(PowerMockRunner.class)
public class CombinedLocationManagerTest {
    @Test
    public void testRemoveUpdates() {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        LocationListener locationListener = PowerMock.createMock(LocationListener.class);

        locationManager.removeUpdates(locationListener);

        PowerMock.replayAll();
        new CombinedLocationManager(locationManager).removeUpdates(locationListener);
        PowerMock.verifyAll();
    }

    @Test
    public void testIsProviderEnabled() {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);

        EasyMock.expect(locationManager.isProviderEnabled("gps")).andReturn(false);
        EasyMock.expect(locationManager.isProviderEnabled("network")).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new CombinedLocationManager(locationManager).isProviderEnabled());
        PowerMock.verifyAll();
    }

    @Test
    public void testIsProviderEnabledDisabled() {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);

        EasyMock.expect(locationManager.isProviderEnabled("gps")).andReturn(false);
        EasyMock.expect(locationManager.isProviderEnabled("network")).andReturn(false);

        PowerMock.replayAll();
        assertFalse(new CombinedLocationManager(locationManager).isProviderEnabled());
        PowerMock.verifyAll();
    }
    @Test
    public void testRequestLocationUpdates() {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        LocationListener locationListener = PowerMock.createMock(LocationListener.class);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 7, 4,
                locationListener);
        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 7, 4, locationListener);

        PowerMock.replayAll();
        new CombinedLocationManager(locationManager).requestLocationUpdates(7, 4, locationListener);
        PowerMock.verifyAll();
    }
}
