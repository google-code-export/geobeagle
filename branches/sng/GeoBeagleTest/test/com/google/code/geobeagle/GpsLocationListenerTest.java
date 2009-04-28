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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    Bundle.class
})
public class GpsLocationListenerTest {

    private LocationListener locationListener;
    private LocationControlBuffered mLocationControlBuffered;

    @Before
    public void setUp() {
        locationListener = createMock(LocationListener.class);
        mLocationControlBuffered = createMock(LocationControlBuffered.class);
    }

    @Test
    public void testOnLocationChanged() {
        Location location = createMock(Location.class);
        expect(mLocationControlBuffered.getLocation()).andReturn(location);
        locationListener.onLocationChanged(location);

        replay(location);
        replay(mLocationControlBuffered);
        new CombinedLocationListener(mLocationControlBuffered, locationListener)
                .onLocationChanged(location);
        verify(location);
        verify(mLocationControlBuffered);
    }

    @Test
    public void testOnProviderDisabled() {
        locationListener.onProviderDisabled("gps");

        replay(locationListener);
        new CombinedLocationListener(null, locationListener).onProviderDisabled("gps");
        verify(locationListener);
    }

    @Test
    public void testOnProviderEnabled() {
        locationListener.onProviderEnabled("gps");

        replay(locationListener);
        new CombinedLocationListener(null, locationListener).onProviderEnabled("gps");
        verify(locationListener);
    }

    @Test
    public void testOnStatusChange() {
        Bundle bundle = PowerMock.createMock(Bundle.class);
        locationListener.onStatusChanged("gps", LocationProvider.OUT_OF_SERVICE, bundle);

        replay(locationListener);
        new CombinedLocationListener(null, locationListener).onStatusChanged("gps",
                LocationProvider.OUT_OF_SERVICE, bundle);
        verify(locationListener);
    }
}
