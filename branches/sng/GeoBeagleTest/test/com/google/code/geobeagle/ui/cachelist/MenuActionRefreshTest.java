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
import com.google.code.geobeagle.ui.cachelist.MenuActionRefresh.SortRunnable;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.location.Location;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

@PrepareForTest( {
        Handler.class, ListActivity.class, MenuActionRefresh.class, TextView.class, Toast.class
})
@RunWith(PowerMockRunner.class)
public class MenuActionRefreshTest {

    @Test
    public void testAct() throws Exception {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        Handler handler = PowerMock.createMock(Handler.class);
        MenuActionRefresh.SortRunnable sortRunnable = PowerMock
                .createMock(MenuActionRefresh.SortRunnable.class);
        Toast toast = PowerMock.createMock(Toast.class);

        PowerMock.mockStatic(Toast.class);
        EasyMock.expect(Toast.makeText(listActivity, R.string.sorting, Toast.LENGTH_SHORT))
                .andReturn(toast);
        toast.show();
        PowerMock.expectNew(SortRunnable.class, EasyMock.isA(MenuActionRefresh.class)).andReturn(
                sortRunnable);
        EasyMock.expect(handler.postDelayed(sortRunnable, 200)).andReturn(true);

        PowerMock.replayAll();
        new MenuActionRefresh(listActivity, handler, null, null, null, null, null, null).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testSort() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        Location location = PowerMock.createMock(Location.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        WhereFactory whereFactory = PowerMock.createMock(WhereFactory.class);
        ArrayList<Geocache> geocaches = new ArrayList<Geocache>(0);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        ListTitleFormatter listTitleFormatter = PowerMock.createMock(ListTitleFormatter.class);
        TextView textView = PowerMock.createMock(TextView.class);

        expect(locationControlBuffered.getLocation()).andReturn(location);
        expect(filterNearestCaches.getWhereFactory()).andReturn(whereFactory);
        geocachesSql.loadCaches(location, whereFactory);
        expect(geocachesSql.getGeocaches()).andReturn(geocaches);
        cacheListData.add(geocaches, locationControlBuffered);
        listActivity.setListAdapter(geocacheListAdapter);
        expect(geocachesSql.getCount()).andReturn(1000);
        expect(listActivity.getString(R.string.cache_list_title, 0, 1000)).andReturn(
                "0 caches out of 1000");
        expect(cacheListData.size()).andReturn(0);
        expect(filterNearestCaches.getTitleText()).andReturn(R.string.cache_list_title);
        listActivity.setTitle("0 caches out of 1000");
        expect(listActivity.findViewById(android.R.id.empty)).andReturn(textView);
        expect(listTitleFormatter.getBodyText(1000)).andReturn(R.string.no_nearby_caches);
        textView.setText(R.string.no_nearby_caches);

        PowerMock.replayAll();
        new MenuActionRefresh(listActivity, null, locationControlBuffered, filterNearestCaches,
                geocachesSql, cacheListData, geocacheListAdapter, listTitleFormatter).sort();
        PowerMock.verifyAll();
    }

    @Test
    public void testSortRunnable() {
        MenuActionRefresh menuActionRefresh = PowerMock.createMock(MenuActionRefresh.class);

        menuActionRefresh.sort();

        PowerMock.replayAll();
        new MenuActionRefresh.SortRunnable(menuActionRefresh).run();
        PowerMock.verifyAll();
    }
}
