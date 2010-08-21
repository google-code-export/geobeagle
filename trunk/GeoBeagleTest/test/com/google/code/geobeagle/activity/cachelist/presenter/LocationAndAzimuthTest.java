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

package com.google.code.geobeagle.activity.cachelist.presenter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.IGpsLocation;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@PrepareForTest( {
    Log.class
})
@RunWith(PowerMockRunner.class)
public class LocationAndAzimuthTest {

    @Test
    public void testLocationAndAzimuthTolerance_AzimuthChanged() {
        IGpsLocation currentLocation = PowerMock.createMock(IGpsLocation.class);

        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();

        PowerMock.replayAll();
        assertTrue(new LocationAndAzimuthTolerance(null, 0).exceedsTolerance(currentLocation, 110,
                0));
        PowerMock.verifyAll();
    }

    @Test
    public void testLocationAndAzimuthTolerance_LocationChanged() {
        IGpsLocation currentLocation = PowerMock.createMock(IGpsLocation.class);
        LocationTolerance locationTolerance = PowerMock.createMock(LocationTolerance.class);

        EasyMock.expect(locationTolerance.exceedsTolerance(currentLocation, 0, 0)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new LocationAndAzimuthTolerance(locationTolerance, 0).exceedsTolerance(
                currentLocation, 0, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void testLocationAndAzimuthTolerance_LocationUnchanged() {
        IGpsLocation currentLocation = PowerMock.createMock(IGpsLocation.class);
        LocationTolerance locationTolerance = PowerMock.createMock(LocationTolerance.class);

        EasyMock.expect(locationTolerance.exceedsTolerance(currentLocation, 0, 0)).andReturn(false);

        PowerMock.replayAll();
        assertFalse(new LocationAndAzimuthTolerance(locationTolerance, 0).exceedsTolerance(
                currentLocation, 0, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void testLocationAndAzimuthTolerance_UpdateLastRefreshed() {
        IGpsLocation currentLocation = PowerMock.createMock(IGpsLocation.class);
        LocationTolerance locationTolerance = PowerMock.createMock(LocationTolerance.class);

        locationTolerance.updateLastRefreshed(currentLocation, 90, 0);

        PowerMock.replayAll();
        new LocationAndAzimuthTolerance(locationTolerance, 0).updateLastRefreshed(currentLocation,
                90, 0);
        PowerMock.verifyAll();
    }
}
