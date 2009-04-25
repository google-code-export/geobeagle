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
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.LocationControl.LocationChooser;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;
import android.location.LocationManager;

@RunWith(PowerMockRunner.class)
public class LocationControlTest {

    @Test
    public void compareIdentical() {
        compareLocations(false, 0L, 0f, 0L, 0f, 0);
    }

    private void compareLocations(boolean expected, long firstTime, float firstAccuracy,
            long secondTime, float secondAccuracy, float distance) {
        Location location1 = PowerMock.createMock(Location.class);
        Location location2 = PowerMock.createMock(Location.class);

        EasyMock.expect(location1.getTime()).andStubReturn(firstTime);
        EasyMock.expect(location1.getAccuracy()).andStubReturn(firstAccuracy);
        EasyMock.expect(location2.getTime()).andStubReturn(secondTime);
        EasyMock.expect(location2.getAccuracy()).andStubReturn(secondAccuracy);
        EasyMock.expect(location1.distanceTo(location2)).andStubReturn(distance);

        PowerMock.replayAll();
        assertEquals(expected ? location2 : location1, new LocationChooser().choose(location1,
                location2));
        PowerMock.verifyAll();
    }

    @Test
    public void compareSecondNewerAndMoreAccurate() {
        compareLocations(true, 0L, 2f, 1L, 1f, 0);
    }

    @Test
    public void compareSecondNewerAndSameAccuracy() {
        compareLocations(true, 0L, 0f, 1L, 0f, 0);
    }

    @Test
    public void compareSecondNewerButNotAsAccurateAndDistanceEqualsAccuracy() {
        compareLocations(true, 0L, 5f, 1L, 5f, 10);
    }

    @Test
    public void compareSecondNewerButNotAsAccurateAndDistanceGreaterThanAccuracy() {
        compareLocations(true, 0L, 1f, 1L, 2f, 4f);
    }

    @Test
    public void compareSecondNewerButNotAsAccurateAndDistanceLessThanAccuracy() {
        compareLocations(false, 0L, 5f, 1L, 6f, 10);
    }

    @Test
    public void testCompareNullLocation() {
        Location location = PowerMock.createMock(Location.class);
        expect(location.getTime()).andStubReturn(1000L);
        expect(location.getAccuracy()).andStubReturn(1000f);

        PowerMock.replayAll();
        LocationControl.LocationChooser locationChooser = new LocationControl.LocationChooser();
        assertEquals(location, locationChooser.choose(null, location));
        assertEquals(location, locationChooser.choose(location, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetLocationNetworkChooseGps() {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        LocationChooser locationChooser = PowerMock
                .createMock(LocationControl.LocationChooser.class);
        Location gpsLocation = PowerMock.createMock(Location.class);
        Location networkLocation = PowerMock.createMock(Location.class);

        LocationControl locationControl = new LocationControl(locationManager, locationChooser);
        expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).andReturn(
                gpsLocation);
        expect(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).andReturn(
                networkLocation);

        expect(locationChooser.choose(gpsLocation, networkLocation)).andReturn(gpsLocation);

        PowerMock.replayAll();
        assertEquals(gpsLocation, locationControl.getLocation());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetLocationNetworkChooseNetwork() {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        LocationChooser locationChooser = PowerMock
                .createMock(LocationControl.LocationChooser.class);
        Location gpsLocation = PowerMock.createMock(Location.class);
        Location networkLocation = PowerMock.createMock(Location.class);

        LocationControl locationControl = new LocationControl(locationManager, locationChooser);
        expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).andReturn(
                gpsLocation);
        expect(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).andReturn(
                networkLocation);

        expect(locationChooser.choose(gpsLocation, networkLocation)).andReturn(networkLocation);

        PowerMock.replayAll();
        assertEquals(networkLocation, locationControl.getLocation());
        PowerMock.verifyAll();
    }

}
