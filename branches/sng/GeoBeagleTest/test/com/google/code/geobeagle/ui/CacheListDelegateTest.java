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

package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Action;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.ui.CacheListDelegate.CacheListOnCreateContextMenuListener;

import android.app.ListActivity;
import android.location.Location;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.util.ArrayList;

import junit.framework.TestCase;

public class CacheListDelegateTest extends TestCase {

    public void testCacheListOnCreateContextMenuListener() {
        ContextMenu menu = createMock(ContextMenu.class);
        AdapterContextMenuInfo menuInfo = createMock(AdapterContextMenuInfo.class);
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);

        expect(geocacheVectors.get(12)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        expect(menu.setHeaderTitle("GC123")).andReturn(menu);
        expect(menu.add(0, CacheListDelegate.MENU_VIEW, 0, "ViewAction")).andReturn(null);
        expect(menu.add(0, CacheListDelegate.MENU_DELETE, 1, "DeleteAction")).andReturn(null);

        replay(menu);
        replay(menuInfo);
        replay(geocacheVectors);
        replay(geocacheVector);
        menuInfo.position = 12;
        CacheListOnCreateContextMenuListener c = new CacheListOnCreateContextMenuListener(
                geocacheVectors);
        c.onCreateContextMenu(menu, null, menuInfo);
        verify(menu);
        verify(menuInfo);
        verify(geocacheVectors);
        verify(geocacheVector);
    }

    public void testCacheListOnCreateContextMenuListenerMyLocation() {
        ContextMenu menu = createMock(ContextMenu.class);
        AdapterContextMenuInfo menuInfo = createMock(AdapterContextMenuInfo.class);
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);

        expect(geocacheVectors.get(0)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("My Current Location");
        expect(menu.setHeaderTitle("My Current Location")).andReturn(menu);
        expect(menu.add(0, CacheListDelegate.MENU_VIEW, 0, "ViewAction")).andReturn(null);

        replay(menu);
        replay(menuInfo);
        replay(geocacheVectors);
        replay(geocacheVector);
        menuInfo.position = 0;
        CacheListOnCreateContextMenuListener c = new CacheListOnCreateContextMenuListener(
                geocacheVectors);
        c.onCreateContextMenu(menu, null, menuInfo);
        verify(menu);
        verify(menuInfo);
        verify(geocacheVectors);
        verify(geocacheVector);
    }

    public void testOnContextItemSelected() {
        MenuItem menuItem = createMock(MenuItem.class);
        AdapterContextMenuInfo adapterContextMenuInfo = createMock(AdapterContextMenuInfo.class);
        Action action = createMock(Action.class);

        Action actions[] = {
                null, null, action
        };

        expect(menuItem.getMenuInfo()).andReturn(adapterContextMenuInfo);
        expect(menuItem.getItemId()).andReturn(2);
        action.act(76, null);

        replay(menuItem);
        adapterContextMenuInfo.position = 76;
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null, null, null,
                null, null, actions, null, null);
        assertTrue(cacheListDelegate.onContextItemSelected(menuItem));
        verify(menuItem);
    }

    public void testOnCreate() {
        ListActivity activity = createMock(ListActivity.class);
        ListView listView = createMock(ListView.class);
        CacheListOnCreateContextMenuListener.Factory factory = createMock(CacheListOnCreateContextMenuListener.Factory.class);
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);

        activity.setContentView(R.layout.cache_list);
        expect(activity.getListView()).andReturn(listView);
        expect(factory.create(geocacheVectors)).andReturn(activity);
        listView.setOnCreateContextMenuListener(activity);

        replay(activity);
        replay(listView);
        replay(factory);
        replay(geocacheVectors);
        new CacheListDelegate(activity, null, null, null, geocacheVectors, null, null, null,
                factory, null).onCreate();
        verify(activity);
        verify(listView);
        verify(factory);
        verify(geocacheVectors);
    }

    public void testOnCreateOptionsMenu() {
        Menu menu = createMock(Menu.class);

        expect(menu.add(R.string.menu_import_gpx)).andReturn(null);

        replay(menu);
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null, null, null,
                null, null, null, null, null);
        assertTrue(cacheListDelegate.onCreateOptionsMenu(menu));
        verify(menu);
    }

    public void testOnListItemClick() {
        final Action action = createMock(Action.class);
        Action actions[] = {
                null, action
        };

        action.act(46, null);

        replay(action);
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null, null, null,
                null, null, actions, null, null);
        cacheListDelegate.onListItemClick(null, null, 46, 0);
        verify(action);
    }

    public void testOnResume() {
        ListActivity listActivity = createMock(ListActivity.class);
        GeocacheListAdapter geocacheListAdapter = createMock(GeocacheListAdapter.class);
        SimpleAdapter simpleAdapter = createMock(SimpleAdapter.class);
        GeocachesSql locationBookmarks = createMock(GeocachesSql.class);
        CacheListData cacheListData = createMock(CacheListData.class);
        LocationControl locationControl = createMock(LocationControl.class);
        Location here = createMock(Location.class);
        ArrayList<Geocache> locations = new ArrayList<Geocache>(0);

        locationBookmarks.loadNearestCaches();
        expect(locationBookmarks.getGeocaches()).andReturn(locations);
        expect(locationControl.getLocation()).andReturn(here);
        cacheListData.add(locations, here);
        listActivity.setListAdapter(simpleAdapter);
        expect(locationBookmarks.getCount()).andReturn(1000);

        replay(geocacheListAdapter);
        replay(locationBookmarks);
        replay(cacheListData);
        replay(locationControl);
        new CacheListDelegate(listActivity, locationBookmarks, locationControl, cacheListData,
                null, null, null, null, null, null).onResume();
        verify(geocacheListAdapter);
        verify(locationBookmarks);
        verify(cacheListData);
        verify(locationControl);
    }
}
