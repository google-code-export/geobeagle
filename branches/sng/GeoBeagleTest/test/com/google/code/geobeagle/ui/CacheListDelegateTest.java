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

import com.google.code.geobeagle.CacheListActions;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.io.LocationBookmarksSql;
import com.google.code.geobeagle.ui.CacheListDelegate.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.ui.CacheListDelegate.SimpleAdapterFactory;

import android.app.ListActivity;
import android.location.Location;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;

public class CacheListDelegateTest extends TestCase {
    public void testCacheListOnCreateContextMenuListener() {
        ContextMenu menu = createMock(ContextMenu.class);
        AdapterContextMenuInfo menuInfo = createMock(AdapterContextMenuInfo.class);
        CacheListData cacheListData = createMock(CacheListData.class);

        expect(cacheListData.getId(12)).andReturn("GC123");
        expect(menu.setHeaderTitle("GC123")).andReturn(menu);
        expect(menu.add(0, CacheListDelegate.MENU_VIEW, 0, "View")).andReturn(null);
        expect(menu.add(0, CacheListDelegate.MENU_DELETE, 1, "Delete")).andReturn(null);

        replay(menu);
        replay(menuInfo);
        replay(cacheListData);
        menuInfo.position = 12;
        CacheListOnCreateContextMenuListener c = new CacheListOnCreateContextMenuListener(
                cacheListData);
        c.onCreateContextMenu(menu, null, menuInfo);
        verify(menu);
        verify(menuInfo);
        verify(cacheListData);
    }

    public void testCacheListOnCreateContextMenuListenerMyLocation() {
        ContextMenu menu = createMock(ContextMenu.class);
        AdapterContextMenuInfo menuInfo = createMock(AdapterContextMenuInfo.class);
        CacheListData cacheListData = createMock(CacheListData.class);

        expect(cacheListData.getId(0)).andReturn("My Current Location");
        expect(menu.setHeaderTitle("My Current Location")).andReturn(menu);
        expect(menu.add(0, CacheListDelegate.MENU_VIEW, 0, "View")).andReturn(null);

        replay(menu);
        replay(menuInfo);
        replay(cacheListData);
        menuInfo.position = 0;
        CacheListOnCreateContextMenuListener c = new CacheListOnCreateContextMenuListener(
                cacheListData);
        c.onCreateContextMenu(menu, null, menuInfo);
        verify(menu);
        verify(menuInfo);
        verify(cacheListData);
    }

    public void testOnContextItemSelected() {
        MenuItem menuItem = createMock(MenuItem.class);
        AdapterContextMenuInfo adapterContextMenuInfo = createMock(AdapterContextMenuInfo.class);
        CacheListActions.Action action = createMock(CacheListActions.Action.class);

        CacheListActions.Action actions[] = {
                null, null, action
        };

        expect(menuItem.getMenuInfo()).andReturn(adapterContextMenuInfo);
        expect(menuItem.getItemId()).andReturn(2);
        action.act(76, null);

        replay(menuItem);
        adapterContextMenuInfo.position = 76;
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null, null, null,
                null, actions, null, null);
        assertTrue(cacheListDelegate.onContextItemSelected(menuItem));
        verify(menuItem);
    }

    public void testOnCreate() {
        ListActivity activity = createMock(ListActivity.class);
        ListView listView = createMock(ListView.class);
        CacheListOnCreateContextMenuListener.Factory factory = createMock(CacheListOnCreateContextMenuListener.Factory.class);
        CacheListData cacheListData = createMock(CacheListData.class);

        activity.setContentView(R.layout.cache_list);
        expect(activity.getListView()).andReturn(listView);
        expect(factory.create(cacheListData)).andReturn(activity);
        listView.setOnCreateContextMenuListener(activity);

        replay(activity);
        replay(listView);
        replay(factory);
        replay(cacheListData);
        new CacheListDelegate(activity, null, null, null, cacheListData, null, null, factory, null)
                .onCreate();
        verify(activity);
        verify(listView);
        verify(factory);
        verify(cacheListData);
    }

    public void testOnCreateOptionsMenu() {
        Menu menu = createMock(Menu.class);

        expect(menu.add(R.string.menu_import_gpx)).andReturn(null);

        replay(menu);
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null, null, null,
                null, null, null, null);
        assertTrue(cacheListDelegate.onCreateOptionsMenu(menu));
        verify(menu);
    }

    public void testOnListItemClick() {
        final CacheListActions.Action action = createMock(CacheListActions.Action.class);
        CacheListActions.Action actions[] = {
                null, action
        };

        action.act(46, null);

        replay(action);
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null, null, null,
                null, actions, null, null);
        cacheListDelegate.onListItemClick(null, null, 46, 0);
        verify(action);
    }

    public void testOnResume() {
        ListActivity listActivity = createMock(ListActivity.class);
        SimpleAdapterFactory simpleAdapterFactory = createMock(SimpleAdapterFactory.class);
        SimpleAdapter simpleAdapter = createMock(SimpleAdapter.class);
        LocationBookmarksSql locationBookmarks = createMock(LocationBookmarksSql.class);
        CacheListData cacheListData = createMock(CacheListData.class);
        LocationControl locationControl = createMock(LocationControl.class);
        Location here = createMock(Location.class);
        ArrayList<CharSequence> locations = new ArrayList<CharSequence>(0);
        ArrayList<Map<String, Object>> adapterData = new ArrayList<Map<String, Object>>(0);

        locationBookmarks.onResume(null);
        expect(locationBookmarks.getLocations()).andReturn(locations);
        expect(locationControl.getLocation()).andReturn(here);
        cacheListData.add(locations, here);
        expect(cacheListData.getAdapterData()).andReturn(adapterData);
        expect(
                simpleAdapterFactory.create(listActivity, adapterData, R.layout.cache_row,
                        CacheListDelegate.ADAPTER_FROM, CacheListDelegate.ADAPTER_TO)).andReturn(
                simpleAdapter);
        listActivity.setListAdapter(simpleAdapter);

        replay(simpleAdapterFactory);
        replay(locationBookmarks);
        replay(cacheListData);
        replay(locationControl);
        new CacheListDelegate(listActivity, locationBookmarks, locationControl,
                simpleAdapterFactory, cacheListData, null, null, null, null).onResume();
        verify(simpleAdapterFactory);
        verify(locationBookmarks);
        verify(cacheListData);
        verify(locationControl);
    }
}
