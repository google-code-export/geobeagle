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
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.ui.cachelist.Action;
import com.google.code.geobeagle.ui.cachelist.GeocacheListDelegate;
import com.google.code.geobeagle.ui.cachelist.GeocacheListAdapter;
import com.google.code.geobeagle.ui.cachelist.GeocacheListDelegate.CacheListOnCreateContextMenuListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.location.Location;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListDelegate.class, ListActivity.class
})
public class CacheListDelegateTest {

    @Test
    public void testCacheListOnCreateContextMenuListener() {
        ContextMenu menu = PowerMock.createMock(ContextMenu.class);
        AdapterContextMenuInfo menuInfo = PowerMock.createMock(AdapterContextMenuInfo.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);

        expect(geocacheVectors.get(12)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        expect(menu.setHeaderTitle("GC123")).andReturn(menu);
        expect(menu.add(0, GeocacheListDelegate.MENU_VIEW, 0, "View")).andReturn(null);
        expect(menu.add(0, GeocacheListDelegate.MENU_DELETE, 1, "Delete")).andReturn(null);

        PowerMock.replayAll();
        menuInfo.position = 12;
        CacheListOnCreateContextMenuListener c = new CacheListOnCreateContextMenuListener(
                geocacheVectors);
        c.onCreateContextMenu(menu, null, menuInfo);
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheListOnCreateContextMenuListenerMyLocation() {
        ContextMenu menu = PowerMock.createMock(ContextMenu.class);
        AdapterContextMenuInfo menuInfo = PowerMock.createMock(AdapterContextMenuInfo.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);

        expect(geocacheVectors.get(0)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("My Current Location");
        expect(menu.setHeaderTitle("My Current Location")).andReturn(menu);
        expect(menu.add(0, GeocacheListDelegate.MENU_VIEW, 0, "View")).andReturn(null);

        PowerMock.replayAll();
        menuInfo.position = 0;
        CacheListOnCreateContextMenuListener c = new CacheListOnCreateContextMenuListener(
                geocacheVectors);
        c.onCreateContextMenu(menu, null, menuInfo);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnContextItemSelected() {
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        AdapterContextMenuInfo adapterContextMenuInfo = PowerMock
                .createMock(AdapterContextMenuInfo.class);
        Action action = PowerMock.createMock(Action.class);

        Action actions[] = {
                null, null, action
        };

        expect(menuItem.getMenuInfo()).andReturn(adapterContextMenuInfo);
        expect(menuItem.getItemId()).andReturn(2);
        action.act(76, null);

        PowerMock.replayAll();
        adapterContextMenuInfo.position = 76;
        GeocacheListDelegate geocacheListDelegate = new GeocacheListDelegate(null, null, null,
                null, null, null, null, actions, null, null);
        assertTrue(geocacheListDelegate.onContextItemSelected(menuItem));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreate() {
        ListActivity activity = PowerMock.createMock(ListActivity.class);
        ListView listView = PowerMock.createMock(ListView.class);
        CacheListOnCreateContextMenuListener.Factory factory = PowerMock
                .createMock(CacheListOnCreateContextMenuListener.Factory.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);

        activity.setContentView(R.layout.cache_list);
        expect(activity.getListView()).andReturn(listView);
        expect(factory.create(geocacheVectors)).andReturn(activity);
        listView.setOnCreateContextMenuListener(activity);

        PowerMock.replayAll();
        new GeocacheListDelegate(activity, null, null, null, geocacheVectors, null, null, null,
                factory, null).onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateOptionsMenu() {
        Menu menu = PowerMock.createMock(Menu.class);

        expect(menu.add(R.string.menu_import_gpx)).andReturn(null);

        PowerMock.replayAll();
        GeocacheListDelegate geocacheListDelegate = new GeocacheListDelegate(null, null, null,
                null, null, null, null, null, null, null);
        assertTrue(geocacheListDelegate.onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClick() {
        final Action action = PowerMock.createMock(Action.class);
        Action actions[] = {
                null, action
        };

        action.act(46, null);

        PowerMock.replayAll();
        GeocacheListDelegate geocacheListDelegate = new GeocacheListDelegate(null, null, null,
                null, null, null, null, actions, null, null);
        geocacheListDelegate.onListItemClick(null, null, 46, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        Location here = PowerMock.createMock(Location.class);

        ArrayList<Geocache> locations = new ArrayList<Geocache>(0);
        geocachesSql.loadNearestCaches();
        expect(geocachesSql.getGeocaches()).andReturn(locations);
        expect(locationControl.getLocation()).andReturn(here);
        cacheListData.add(locations, here);
        listActivity.setListAdapter(geocacheListAdapter);
        expect(geocachesSql.getCount()).andReturn(1000);
        expect(listActivity.getString(R.string.cache_list_title, 0, 1000)).andReturn(
                "0 caches out of 1000");
        listActivity.setTitle("0 caches out of 1000");

        PowerMock.replayAll();
        new GeocacheListDelegate(listActivity, geocachesSql, locationControl, cacheListData, null,
                geocacheListAdapter, null, null, null, null).onResume();
        PowerMock.verifyAll();
    }
}
