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

import com.google.code.geobeagle.LocationControl.LocationChooser;

import android.location.Location;
import android.location.LocationManager;

import junit.framework.TestCase;

public class LocationControlTest extends TestCase {

    public void compareLocations(boolean expected, long firstTime, float firstAccuracy,
            long secondTime, float secondAccuracy, float distance) {
        Location location1 = createMock(Location.class);
        Location location2 = createMock(Location.class);
        expect(location1.getTime()).andStubReturn(firstTime);
        expect(location1.getAccuracy()).andStubReturn(firstAccuracy);
        expect(location2.getTime()).andStubReturn(secondTime);
        expect(location2.getAccuracy()).andStubReturn(secondAccuracy);
        expect(location1.distanceTo(location2)).andStubReturn(distance);

        replay(location1);
        replay(location2);
        LocationControl.LocationChooser locationChooser = new LocationControl.LocationChooser();
        assertEquals(expected ? location2 : location1, locationChooser.choose(location1, location2));
        verify(location1);
        verify(location2);
    }

    public void testCompareLocations() {
        // Choose first

        // identical
        compareLocations(false, 0L, 0f, 0L, 0f, 0);

        // second one is newer, but not as accurate, distance < a1 + a2.
        compareLocations(false, 0L, 5f, 1L, 6f, 10);

        // Choose second

        // second one is newer, same accuracy
        compareLocations(true, 0L, 0f, 1L, 0f, 0);

        // second one is newer and more accurate
        compareLocations(true, 0L, 2f, 1L, 1f, 0);

        // second one is newer, but not as accurate, distance > a1 + a2.
        compareLocations(true, 0L, 5f, 1L, 4f, 10);

        // second one is newer, but not as accurate, distance = a1 + a2.
        compareLocations(true, 0L, 5f, 1L, 5f, 10);
    }

    public void testCompareNullLocation() {
        Location location = createMock(Location.class);
        expect(location.getTime()).andStubReturn(1000L);
        expect(location.getAccuracy()).andStubReturn(1000f);

        replay(location);
        LocationControl.LocationChooser locationChooser = new LocationControl.LocationChooser();
        assertEquals(location, locationChooser.choose(null, location));
        assertEquals(location, locationChooser.choose(location, null));
        verify(location);
    }

    public void testGetLocationNetworkChooseGps() {
        LocationManager locationManager = createMock(LocationManager.class);
        LocationChooser locationChooser = createMock(LocationControl.LocationChooser.class);
        LocationControl locationControl = new LocationControl(locationManager, locationChooser);
        Location gpsLocation = createMock(Location.class);
        Location networkLocation = createMock(Location.class);
        expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).andReturn(
                gpsLocation);
        expect(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).andReturn(
                networkLocation);

        expect(locationChooser.choose(gpsLocation, networkLocation)).andReturn(gpsLocation);

        replay(locationChooser);
        replay(locationManager);
        assertEquals(gpsLocation, locationControl.getLocation());
        verify(locationManager);
        verify(locationChooser);
    }

    public void testGetLocationNetworkChooseNetwork() {
        LocationManager locationManager = createMock(LocationManager.class);
        LocationChooser locationChooser = createMock(LocationControl.LocationChooser.class);
        LocationControl locationControl = new LocationControl(locationManager, locationChooser);
        Location gpsLocation = createMock(Location.class);
        Location networkLocation = createMock(Location.class);
        expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).andReturn(
                gpsLocation);
        expect(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).andReturn(
                networkLocation);

        expect(locationChooser.choose(gpsLocation, networkLocation)).andReturn(networkLocation);

        replay(locationChooser);
        replay(locationManager);
        assertEquals(networkLocation, locationControl.getLocation());
        verify(locationManager);
        verify(locationChooser);
    }

}
