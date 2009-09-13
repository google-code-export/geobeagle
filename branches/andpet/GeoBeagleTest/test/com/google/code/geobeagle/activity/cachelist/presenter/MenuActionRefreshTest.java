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

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.database.FilterNearestCaches;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

@PrepareForTest( {
        Handler.class, ListActivity.class, Log.class, CacheListRefresh.class, TextView.class,
        CacheListDelegateDI.Timing.class, Calendar.class
})
@RunWith(PowerMockRunner.class)
public class MenuActionRefreshTest {

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
    public void testAdapterCachesSorter() {
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        CacheListDelegateDI.Timing timing = PowerMock.createMock(CacheListDelegateDI.Timing.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        SortStrategy sortStrategy = PowerMock.createMock(SortStrategy.class);
        ArrayList<GeocacheVector> arrayList = new ArrayList<GeocacheVector>();

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(cacheListData.get()).andReturn(arrayList);
        EasyMock.expect(locationControlBuffered.getSortStrategy()).andReturn(sortStrategy);
        sortStrategy.sort(arrayList);

        PowerMock.replayAll();
        new AdapterCachesSorter(cacheListData, timing, locationControlBuffered).refresh();
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheListRefresh_ForceRefresh() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        CacheListDelegateDI.Timing timing = PowerMock.createMock(CacheListDelegateDI.Timing.class);
        ActionManager actionManager = PowerMock.createMock(ActionManager.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        EasyMock.expect(timing.getTime()).andReturn(100000L);
        timing.start();
        EasyMock.expect(locationControlBuffered.getGpsLocation()).andReturn(here);
        EasyMock.expect(locationControlBuffered.getAzimuth()).andReturn(90f);
        actionManager.performActions(here, 90, 0, 100000);

        PowerMock.replayAll();
        new CacheListRefresh(actionManager, timing, locationControlBuffered).forceRefresh();
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheListRefresh_Refresh() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        CacheListDelegateDI.Timing timing = PowerMock.createMock(CacheListDelegateDI.Timing.class);
        ActionManager actionManager = PowerMock.createMock(ActionManager.class);
        IGpsLocation here = PowerMock.createMock(IGpsLocation.class);

        timing.start();
        EasyMock.expect(timing.getTime()).andReturn(10000L);
        EasyMock.expect(locationControlBuffered.getGpsLocation()).andReturn(here);
        EasyMock.expect(locationControlBuffered.getAzimuth()).andReturn(90f);
        EasyMock.expect(actionManager.getMinActionExceedingTolerance(here, 90, 10000L))
                .andReturn(3);
        actionManager.performActions(here, 90, 3, 10000L);

        PowerMock.replayAll();
        new CacheListRefresh(actionManager, timing, locationControlBuffered)
                .refresh();
        PowerMock.verifyAll();
    }

    /*
     //Is this test relevant any longer?
    @Test
    public void testCacheListRefresh_RefreshDbClosed() {
        ISQLiteDatabase writableDatabase = PowerMock.createMock(ISQLiteDatabase.class);
        PowerMock.mockStatic(Log.class);

        EasyMock.expect(writableDatabase.isOpen()).andReturn(false);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();

        PowerMock.replayAll();
        new CacheListRefresh(null, null, null).refresh();
        PowerMock.verifyAll();
    }
    */

    @Test
    public void testDistanceUpdater() {
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);

        geocacheListAdapter.notifyDataSetChanged();

        PowerMock.replayAll();
        new DistanceUpdater(geocacheListAdapter).refresh();
        PowerMock.verifyAll();
    }

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

    @Test
    public void testTitleUpdater() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        CacheListDelegateDI.Timing timing = PowerMock.createMock(CacheListDelegateDI.Timing.class);

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();

        EasyMock.expect(filterNearestCaches.getTitleText()).andReturn(R.string.cache_list_title);
        EasyMock.expect(listActivity.getString(R.string.cache_list_title, 5, 12)).andReturn(
                "new title");
        listActivity.setTitle("new title");

        PowerMock.replayAll();
        new TitleUpdater(listActivity, filterNearestCaches, null,
                timing).update(5, 12);
        PowerMock.verifyAll();

    }

    @Test
    public void testTitleUpdaterEmpty() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        ListTitleFormatter listTitleFormatter = PowerMock.createMock(ListTitleFormatter.class);
        CacheListDelegateDI.Timing timing = PowerMock.createMock(CacheListDelegateDI.Timing.class);
        TextView textView = PowerMock.createMock(TextView.class);

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();

        EasyMock.expect(filterNearestCaches.getTitleText()).andReturn(R.string.cache_list_title);
        listActivity.setTitle("new title");
        EasyMock.expect(listActivity.getString(R.string.cache_list_title, 0, 12)).andReturn(
                "new title");
        EasyMock.expect(listActivity.findViewById(android.R.id.empty)).andReturn(textView);
        EasyMock.expect(listTitleFormatter.getBodyText(12)).andReturn(R.string.no_nearby_caches);
        textView.setText(R.string.no_nearby_caches);

        PowerMock.replayAll();
        new TitleUpdater(listActivity, filterNearestCaches, listTitleFormatter, timing).update(0, 12);
        PowerMock.verifyAll();
    }
}
