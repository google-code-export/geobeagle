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
public class ActionAndToleranceTest {

    @Test
    public void testActionAndTolerance_ExceedsTolerance() {
        ToleranceStrategy toleranceStrategy = PowerMock.createMock(ToleranceStrategy.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        EasyMock.expect(toleranceStrategy.exceedsTolerance(here, 90, 0)).andReturn(true);

        PowerMock.replayAll();
        final ActionAndTolerance actionAndTolerance = new ActionAndTolerance(null,
                toleranceStrategy);
        assertTrue(actionAndTolerance.exceedsTolerance(here, 90, 0));
        PowerMock.verifyAll();
    }

    @Test
    public void testActionAndTolerance_Refresh() {
        RefreshAction refreshAction = PowerMock.createMock(RefreshAction.class);

        refreshAction.refresh();

        PowerMock.replayAll();
        new ActionAndTolerance(refreshAction, null).refresh();
        PowerMock.verifyAll();
    }

    @Test
    public void testActionAndTolerance_UpdateLoastRefreshed() {
        ToleranceStrategy toleranceStrategy = PowerMock.createMock(ToleranceStrategy.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        toleranceStrategy.updateLastRefreshed(here, 90, 0);

        PowerMock.replayAll();
        final ActionAndTolerance actionAndTolerance = new ActionAndTolerance(null,
                toleranceStrategy);
        actionAndTolerance.updateLastRefreshed(here, 90, 0);
        PowerMock.verifyAll();
    }
}
