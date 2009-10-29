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

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest( {})
@RunWith(PowerMockRunner.class)
public class ActionAndToleranceTest {
    IGpsLocation mLastLocation;
    IGpsLocation mHere;

    @Before
    public void setUp() {
        mLastLocation = PowerMock.createMock(IGpsLocation.class);
        mHere = PowerMock.createMock(IGpsLocation.class);
        EasyMock.expect(mHere.distanceTo(mLastLocation)).andReturn(20f).anyTimes();
    }
    
    @Test
    public void testWithinTolerance() {
        PowerMock.replayAll();
        final ActionAndTolerance actionAndTolerance = new ActionAndTolerance(null, 21, mLastLocation, 0, false);
        assertFalse(actionAndTolerance.exceedsTolerance(mHere, 90, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void testLocationTolerance() {
        PowerMock.replayAll();
        final ActionAndTolerance actionAndTolerance = new ActionAndTolerance(null, 19, mLastLocation, 0, false);
        assertTrue(actionAndTolerance.exceedsTolerance(mHere, 90, 0));
        PowerMock.verifyAll();
    }
    
    @Test
    public void testTooSoonForUpdate() {
        PowerMock.replayAll();
        final ActionAndTolerance actionAndTolerance = new ActionAndTolerance(null, 21, mLastLocation, 15, false);
        assertFalse(actionAndTolerance.exceedsTolerance(mHere, 90, 10));
        PowerMock.verifyAll();
    }
    
    @Test
    public void testAzimuthChanged() {
        PowerMock.replayAll();
        final ActionAndTolerance actionAndTolerance = new ActionAndTolerance(null, 21, mLastLocation, 0, true);
        assertTrue(actionAndTolerance.exceedsTolerance(mHere, 90, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void testUpdateLastRefreshed() {
        //Changing the time stamp so a refresh is avoided
        PowerMock.replayAll();
        final ActionAndTolerance actionAndTolerance = new ActionAndTolerance(null, 19, mLastLocation, 5, false);
        actionAndTolerance.updateLastRefreshed(mLastLocation, 80, 8);
        assertFalse(actionAndTolerance.exceedsTolerance(mHere, 90, 10));
        PowerMock.verifyAll();
    }

    @Test
    public void testRefresh() {
        RefreshAction refreshAction = PowerMock.createMock(RefreshAction.class);
        refreshAction.refresh();
        PowerMock.replayAll();

        final ActionAndTolerance actionAndTolerance = new ActionAndTolerance(refreshAction, 19, null, 5, false);
        actionAndTolerance.refresh();
        PowerMock.verifyAll();
    }

}
