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

@PrepareForTest( {})
@RunWith(PowerMockRunner.class)
public class LocationToleranceTest {

    @Test
    public void LocationTolerance_ExceedsTolerance() {
        IGpsLocation gpsLocation = PowerMock.createMock(IGpsLocation.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        EasyMock.expect(here.distanceTo(gpsLocation)).andReturn(20f);

        PowerMock.replayAll();
        assertTrue(new LocationTolerance(10, gpsLocation, 0).exceedsTolerance(here, 90, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void LocationTolerance_DoesntExceedsTolerance() {
        IGpsLocation gpsLocation = PowerMock.createMock(IGpsLocation.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        EasyMock.expect(here.distanceTo(gpsLocation)).andReturn(5f);

        PowerMock.replayAll();
        assertFalse(new LocationTolerance(10, gpsLocation, 0).exceedsTolerance(here, 90, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void LocationTolerance_DoesntExceedsTimeTolerance() {
        IGpsLocation gpsLocation = PowerMock.createMock(IGpsLocation.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        PowerMock.replayAll();
        assertFalse(new LocationTolerance(10, gpsLocation, 5000).exceedsTolerance(here, 90, 4000));
        PowerMock.verifyAll();
    }

    @Test
    public void LocationTolerance_UpdateLastRefreshed() {
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        new LocationTolerance(10, null, 0).updateLastRefreshed(here, 20, 0);
    }
}
