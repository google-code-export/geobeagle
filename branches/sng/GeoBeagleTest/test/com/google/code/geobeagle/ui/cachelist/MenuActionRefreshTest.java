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

package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.WhereFactory;
import com.google.code.geobeagle.ui.cachelist.MenuActionRefresh.AdapterCachesSorter;
import com.google.code.geobeagle.ui.cachelist.MenuActionRefresh.SqlCacheLoader;
import com.google.code.geobeagle.ui.cachelist.MenuActionRefresh.Timing;
import com.google.code.geobeagle.ui.cachelist.MenuActionRefresh.TitleUpdater;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

@PrepareForTest( {
        Handler.class, ListActivity.class, Log.class, MenuActionRefresh.class, TextView.class
})
@RunWith(PowerMockRunner.class)
public class MenuActionRefreshTest {

    @Test
    public void testAdapterCachesSorter() {
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        Timing timing = PowerMock.createMock(Timing.class);

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();
        cacheListData.sort();
        geocacheListAdapter.notifyDataSetChanged();

        PowerMock.replayAll();
        new AdapterCachesSorter(cacheListData, geocacheListAdapter, timing).sort();
        PowerMock.verifyAll();
    }

    @Test
    public void testSqlCacheLoader() {
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        WhereFactory whereFactory = PowerMock.createMock(WhereFactory.class);
        Location location = PowerMock.createMock(Location.class);
        Timing timing = PowerMock.createMock(Timing.class);

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();
        expect(filterNearestCaches.getWhereFactory()).andReturn(whereFactory);

        geocachesSql.loadCaches(location, whereFactory);
        ArrayList<Geocache> geocaches = new ArrayList<Geocache>();
        expect(geocachesSql.getGeocaches()).andReturn(geocaches);
        cacheListData.add(geocaches, locationControlBuffered);

        PowerMock.replayAll();
        new SqlCacheLoader(geocachesSql, filterNearestCaches, cacheListData,
                locationControlBuffered, timing).load(location);
        PowerMock.verifyAll();
    }

    @Test
    public void testTitleUpdater() {
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        Timing timing = PowerMock.createMock(Timing.class);

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();

        expect(geocachesSql.getCount()).andReturn(12);
        expect(cacheListData.size()).andReturn(5);
        expect(filterNearestCaches.getTitleText()).andReturn(R.string.cache_list_title);
        expect(listActivity.getString(R.string.cache_list_title, 5, 12)).andReturn("new title");
        listActivity.setTitle("new title");

        PowerMock.replayAll();
        new TitleUpdater(geocachesSql, listActivity, filterNearestCaches, cacheListData, null,
                timing).update();
        PowerMock.verifyAll();

    }

    @Test
    public void testTiming() {
        Calendar calendar = PowerMock.createMock(Calendar.class);

        PowerMock.mockStatic(Calendar.class);
        PowerMock.mockStatic(Log.class);
        expect(Calendar.getInstance()).andReturn(calendar);

        expect(calendar.getTimeInMillis()).andReturn(10000L);
        expect(Log.v(EasyMock.isA(String.class), EasyMock.isA(String.class))).andReturn(0);
        expect(calendar.getTimeInMillis()).andReturn(10005L);

        PowerMock.replayAll();
        Timing timing = new Timing();
        timing.start();
        timing.lap("test");
        PowerMock.verifyAll();
    }

    @Test
    public void testTitleUpdaterEmpty() {
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        ListTitleFormatter listTitleFormatter = PowerMock.createMock(ListTitleFormatter.class);
        Timing timing = PowerMock.createMock(Timing.class);
        TextView textView = PowerMock.createMock(TextView.class);

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();

        expect(geocachesSql.getCount()).andReturn(12);
        expect(cacheListData.size()).andReturn(0);
        expect(filterNearestCaches.getTitleText()).andReturn(R.string.cache_list_title);
        listActivity.setTitle("new title");
        expect(listActivity.getString(R.string.cache_list_title, 0, 12)).andReturn("new title");
        expect(listActivity.findViewById(android.R.id.empty)).andReturn(textView);
        expect(listTitleFormatter.getBodyText(12)).andReturn(R.string.no_nearby_caches);
        textView.setText(R.string.no_nearby_caches);

        PowerMock.replayAll();
        new TitleUpdater(geocachesSql, listActivity, filterNearestCaches, cacheListData,
                listTitleFormatter, timing).update();
        PowerMock.verifyAll();

    }

    @Test
    public void testActDoNothing() {
        Location here = PowerMock.createMock(Location.class);
        Location lastSqlLocation = PowerMock.createMock(Location.class);
        Location lastSortLocation = PowerMock.createMock(Location.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Timing timing = PowerMock.createMock(Timing.class);

        timing.start();
        expect(locationControlBuffered.getLocation()).andReturn(here);
        expect(here.distanceTo(lastSqlLocation)).andReturn(400f);
        expect(here.distanceTo(lastSortLocation)).andReturn(5f);

        PowerMock.replayAll();
        new MenuActionRefresh(null, locationControlBuffered, null, timing, null, lastSortLocation,
                lastSqlLocation).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testLoad() {
        Location here = PowerMock.createMock(Location.class);
        Location lastSqlLocation = PowerMock.createMock(Location.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Timing timing = PowerMock.createMock(Timing.class);
        SqlCacheLoader sqlCacheLoader = PowerMock.createMock(SqlCacheLoader.class);
        TitleUpdater titleUpdater = PowerMock.createMock(TitleUpdater.class);
        AdapterCachesSorter adapterCachesSorter = PowerMock.createMock(AdapterCachesSorter.class);

        timing.start();
        expect(locationControlBuffered.getLocation()).andReturn(here);
        expect(here.distanceTo(lastSqlLocation)).andReturn(501f);
        sqlCacheLoader.load(here);
        titleUpdater.update();
        adapterCachesSorter.sort();

        PowerMock.replayAll();
        new MenuActionRefresh(adapterCachesSorter, locationControlBuffered, sqlCacheLoader, timing,
                titleUpdater, lastSqlLocation, lastSqlLocation).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testSort() {
        Location here = PowerMock.createMock(Location.class);
        Location lastSqlLocation = PowerMock.createMock(Location.class);
        Location lastSortLocation = PowerMock.createMock(Location.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Timing timing = PowerMock.createMock(Timing.class);
        AdapterCachesSorter adapterCachesSorter = PowerMock.createMock(AdapterCachesSorter.class);

        timing.start();
        expect(locationControlBuffered.getLocation()).andReturn(here);
        expect(here.distanceTo(lastSqlLocation)).andReturn(400f);
        expect(here.distanceTo(lastSortLocation)).andReturn(7f);
        adapterCachesSorter.sort();

        PowerMock.replayAll();
        new MenuActionRefresh(adapterCachesSorter, locationControlBuffered, null, timing, null,
                lastSortLocation, lastSqlLocation).act();
        PowerMock.verifyAll();
    }
}
