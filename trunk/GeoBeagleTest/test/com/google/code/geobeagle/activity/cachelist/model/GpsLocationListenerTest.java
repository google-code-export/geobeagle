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

package com.google.code.geobeagle.activity.cachelist.model;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.location.CombinedLocationListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Bundle.class, Log.class
})
public class GpsLocationListenerTest extends GeoBeagleTest {

    private LocationListener locationListener;
    private LocationControlBuffered mLocationControlBuffered;
    private ActivityVisible activityVisible;

    @Before
    public void setUp() {
        locationListener = createMock(LocationListener.class);
        mLocationControlBuffered = createMock(LocationControlBuffered.class);
        activityVisible = createMock(ActivityVisible.class);
    }

    @Test
    public void testOnLocationChanged() {
        Location location = createMock(Location.class);
        expect(mLocationControlBuffered.getLocation()).andReturn(location);
        locationListener.onLocationChanged(location);
        expect(activityVisible.getVisible()).andReturn(true);

        replayAll();
        new CombinedLocationListener(mLocationControlBuffered, locationListener, activityVisible)
                .onLocationChanged(location);
        verifyAll();
    }

    @Test
    public void testOnProviderDisabled() {
        locationListener.onProviderDisabled("gps");
        expect(activityVisible.getVisible()).andReturn(true);

        replayAll();
        new CombinedLocationListener(null, locationListener, activityVisible)
                .onProviderDisabled("gps");
        verifyAll();
    }

    @Test
    public void testOnProviderEnabled() {
        locationListener.onProviderEnabled("gps");
        expect(activityVisible.getVisible()).andReturn(true);

        replayAll();
        new CombinedLocationListener(null, locationListener, activityVisible)
                .onProviderEnabled("gps");
        verifyAll();
    }

    @Test
    public void testAzimuth() {
        final LocationControlBuffered locationControlBuffered = new LocationControlBuffered(null,
                null, null, null, null, null);
        locationControlBuffered.setAzimuth(23.8f);
        assertEquals(23.8f, locationControlBuffered.getAzimuth(), .1f);
    }

    @Test
    public void testOnStatusChange() {
        Bundle bundle = createMock(Bundle.class);
        locationListener.onStatusChanged("gps", LocationProvider.OUT_OF_SERVICE, bundle);
        expect(activityVisible.getVisible()).andReturn(true);

        replayAll();
        new CombinedLocationListener(null, locationListener, activityVisible).onStatusChanged(
                "gps", LocationProvider.OUT_OF_SERVICE, bundle);
        verifyAll();
    }
}
