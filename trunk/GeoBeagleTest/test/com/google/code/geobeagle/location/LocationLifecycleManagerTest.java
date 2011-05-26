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

package com.google.code.geobeagle.location;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.activity.compass.LifecycleManager;
import com.google.code.geobeagle.location.LocationLifecycleManager;

import org.junit.Test;

import android.location.LocationListener;
import android.location.LocationManager;

public class LocationLifecycleManagerTest {
    @Test
    public void testOnPause() {
        LocationManager locationManager = createMock(LocationManager.class);
        LocationListener locationListener = createMock(LocationListener.class);
        LifecycleManager lifecycleManager = new LocationLifecycleManager(locationListener,
                locationManager);

        locationManager.removeUpdates(locationListener);

        replay(locationManager);
        lifecycleManager.onPause(null);
        verify(locationManager);
    }

    @Test
    public void testOnResume() {
        LocationManager locationManager = createMock(LocationManager.class);
        LocationListener locationListener = createMock(LocationListener.class);
        LifecycleManager lifecycleManager = new LocationLifecycleManager(locationListener,
                locationManager);

        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);

        replay(locationManager);
        lifecycleManager.onResume(null);
        verify(locationManager);
    }

}
