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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.IGpsLocation;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.Timing;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

@PrepareForTest( {
        Handler.class, ListActivity.class, Log.class, CacheListRefresh.class, TextView.class,
        Timing.class, Calendar.class
})
@RunWith(PowerMockRunner.class)
public class CacheListRefreshTest {

    private Timing mTiming;

    @Before
    public void allowLogging() {
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
        mTiming = PowerMock.createMock(Timing.class);
        mTiming.start();
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mTiming.getTime()).andReturn(10000L).anyTimes();
    }

    @Test
    public void testUpdateFlag() {
        UpdateFlag updateFlag = new UpdateFlag();
        assertTrue(updateFlag.updatesEnabled());
        updateFlag.setUpdatesEnabled(false);
        assertFalse(updateFlag.updatesEnabled());
    }

    @Test
    public void testActionManager_getMinActionExceedingTolerance() {
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);
        ActionAndTolerance actionAndTolerance0 = PowerMock.createMock(ActionAndTolerance.class);
        ActionAndTolerance actionAndTolerance1 = PowerMock.createMock(ActionAndTolerance.class);

        EasyMock.expect(actionAndTolerance0.exceedsTolerance(here, 90, 0)).andReturn(false);
        EasyMock.expect(actionAndTolerance1.exceedsTolerance(here, 90, 0)).andReturn(true);

        PowerMock.replayAll();
        assertEquals(1, new ActionManager(new ActionAndTolerance[] {
                actionAndTolerance0, actionAndTolerance1
        }).getMinActionExceedingTolerance(here, 90, 0));
        PowerMock.verifyAll();

    }

    @Test
    public void testActionManager_performActions() {
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);
        ActionAndTolerance actionAndTolerance0 = PowerMock.createMock(ActionAndTolerance.class);
        ActionAndTolerance actionAndTolerance1 = PowerMock.createMock(ActionAndTolerance.class);

        actionAndTolerance1.refresh();
        actionAndTolerance1.updateLastRefreshed(here, 90, 0);

        PowerMock.replayAll();
        new ActionManager(new ActionAndTolerance[] {
                actionAndTolerance0, actionAndTolerance1
        }).performActions(here, 90, 1, 0);
        PowerMock.verifyAll();

    }

    @Test
    public void testCacheListRefresh_ForceRefresh() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Timing timing = PowerMock.createMock(Timing.class);
        ActionManager actionManager = PowerMock.createMock(ActionManager.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        EasyMock.expect(timing.getTime()).andReturn(100000L);
        timing.start();
        EasyMock.expect(locationControlBuffered.getGpsLocation()).andReturn(here);
        EasyMock.expect(locationControlBuffered.getAzimuth()).andReturn(90f);
        actionManager.performActions(here, 90, 0, 100000);

        PowerMock.replayAll();
        new CacheListRefresh(actionManager, timing, locationControlBuffered, null).forceRefresh();
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheListRefresh_Refresh() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        ActionManager actionManager = PowerMock.createMock(ActionManager.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        EasyMock.expect(updateFlag.updatesEnabled()).andReturn(true);
        EasyMock.expect(locationControlBuffered.getGpsLocation()).andReturn(here);
        EasyMock.expect(locationControlBuffered.getAzimuth()).andReturn(90f);
        EasyMock.expect(actionManager.getMinActionExceedingTolerance(here, 90, 10000L))
                .andReturn(3);
        actionManager.performActions(here, 90, 3, 10000L);

        PowerMock.replayAll();
        new CacheListRefresh(actionManager, mTiming, locationControlBuffered, updateFlag).refresh();
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheListRefresh_RefreshNoUpdate() {
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        EasyMock.expect(updateFlag.updatesEnabled()).andReturn(false);

        PowerMock.replayAll();
        new CacheListRefresh(null, null, null, updateFlag).refresh();
        PowerMock.verifyAll();
    }

    /*
     * //Is this test relevant any longer?
     * @Test public void testCacheListRefresh_RefreshDbClosed() {
     * ISQLiteDatabase writableDatabase =
     * PowerMock.createMock(ISQLiteDatabase.class);
     * PowerMock.mockStatic(Log.class);
     * EasyMock.expect(writableDatabase.isOpen()).andReturn(false);
     * EasyMock.expect(Log.d((String)EasyMock.anyObject(),
     * (String)EasyMock.anyObject())) .andReturn(0).anyTimes();
     * PowerMock.replayAll(); new CacheListRefresh(null, null, null).refresh();
     * PowerMock.verifyAll(); }
     */

}
