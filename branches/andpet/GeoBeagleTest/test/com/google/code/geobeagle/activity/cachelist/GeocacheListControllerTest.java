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

package com.google.code.geobeagle.activity.cachelist;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.database.CachesProviderToggler;
import com.google.code.geobeagle.database.DatabaseDI;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListController.class, ListActivity.class, GeocacheListPresenter.class,
        CacheListOnCreateContextMenuListener.class, DatabaseDI.class
})
public class GeocacheListControllerTest {

    @Test
    public void testCacheListOnCreateContextMenuListener() {
        ContextMenu menu = PowerMock.createMock(ContextMenu.class);
        AdapterContextMenuInfo menuInfo = PowerMock.createMock(AdapterContextMenuInfo.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);

        expect(geocacheVectors.get(11)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        expect(menu.setHeaderTitle("GC123")).andReturn(menu);
        expect(menu.add(0, GeocacheListController.MENU_VIEW, 0, "View")).andReturn(null);
        expect(menu.add(0, GeocacheListController.MENU_EDIT, 1, "Edit")).andReturn(null);
        expect(menu.add(0, GeocacheListController.MENU_DELETE, 2, "Delete")).andReturn(null);

        PowerMock.replayAll();
        menuInfo.position = 12;
        new CacheListOnCreateContextMenuListener(geocacheVectors).onCreateContextMenu(menu, null,
                menuInfo);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnContextItemSelected() {
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        AdapterContextMenuInfo adapterContextMenuInfo = PowerMock
                .createMock(AdapterContextMenuInfo.class);
        CacheAction cacheAction = PowerMock.createMock(CacheAction.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        CacheAction contextActions[] = {
                null, null, cacheAction
        };
        expect(menuItem.getMenuInfo()).andReturn(adapterContextMenuInfo);
        expect(menuItem.getItemId()).andReturn(2);
        expect(geocacheVectors.get(75)).andReturn(geocacheVector);
        expect(geocacheVector.getGeocache()).andReturn(geocache);
        cacheAction.act(geocache);

        PowerMock.replayAll();
        adapterContextMenuInfo.position = 76;
        GeocacheListController geocacheListController = new GeocacheListController(null,
                contextActions, null, null, null, geocacheVectors);
        assertTrue(geocacheListController.onContextItemSelected(menuItem));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateContextMenu() {
        ContextMenu contextMenu = PowerMock.createMock(ContextMenu.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);

        PowerMock.suppressConstructor(AdapterContextMenuInfo.class);
        AdapterContextMenuInfo contextMenuInfo = new AdapterContextMenuInfo(null, 0, 0);
        contextMenuInfo.position = 42;
        EasyMock.expect(geocacheVectors.get(41)).andReturn(geocacheVector);
        EasyMock.expect(geocacheVector.getId()).andReturn("GCABC");
        EasyMock.expect(contextMenu.setHeaderTitle("GCABC")).andReturn(contextMenu);
        EasyMock.expect(contextMenu.add(0, GeocacheListController.MENU_VIEW, 0, "View")).andReturn(
                menuItem);
        EasyMock.expect(contextMenu.add(0, GeocacheListController.MENU_EDIT, 1, "Edit")).andReturn(
                menuItem);
        EasyMock.expect(contextMenu.add(0, GeocacheListController.MENU_DELETE, 2, "Delete"))
                .andReturn(menuItem);

        PowerMock.replayAll();
        new CacheListOnCreateContextMenuListener(geocacheVectors).onCreateContextMenu(contextMenu,
                null, contextMenuInfo);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateOptionsMenu() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        Menu menu = PowerMock.createMock(Menu.class);

        EasyMock.expect(menuActions.onCreateOptionsMenu(menu)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new GeocacheListController(null, null, null, null, menuActions, null)
                .onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClick() {
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        final CacheAction cacheAction = PowerMock.createMock(CacheAction.class);
        CacheAction cacheActions[] = {
                null, cacheAction
        };

        expect(geocacheVectors.get(45)).andReturn(geocacheVector);
        expect(geocacheVector.getGeocache()).andReturn(geocache);
        cacheAction.act(geocache);

        PowerMock.replayAll();
        new GeocacheListController(null, cacheActions, null, null, null, geocacheVectors).
          onListItemClick(null, null, 46, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClickZero() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);

        cacheListRefresh.forceRefresh();

        PowerMock.replayAll();
        new GeocacheListController(cacheListRefresh, null, null, null, null, null).onListItemClick(null,
                null, 0, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnMenuOpened() {
        Menu menu = PowerMock.createMock(Menu.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        CachesProviderToggler toggler = PowerMock.createMock(CachesProviderToggler.class);

        EasyMock.expect(menu.findItem(R.string.menu_toggle_filter)).andReturn(menuItem);
        EasyMock.expect(toggler.isShowingNearest()).andReturn(false);
        EasyMock.expect(menuItem.setTitle(R.string.menu_show_nearest_caches)).andReturn(menuItem);

        PowerMock.replayAll();
        new GeocacheListController(null, null, toggler, null, null, null).onMenuOpened(0,
                menu);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsItemSelected() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        expect(menuItem.getItemId()).andReturn(27);
        EasyMock.expect(menuActions.act(27)).andReturn(true);

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, null, menuActions, null)
                .onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() {
        MenuActionSyncGpx menuActionSync = PowerMock.createMock(MenuActionSyncGpx.class);

        menuActionSync.abort();

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, menuActionSync, null, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);

        cacheListRefresh.forceRefresh();

        PowerMock.replayAll();
        new GeocacheListController(cacheListRefresh, null, null, null, null, null).onResume(
                cacheListRefresh, false);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResumeAndImport() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        MenuActionSyncGpx menuActionSyncGpx = PowerMock.createMock(MenuActionSyncGpx.class);

        cacheListRefresh.forceRefresh();
        menuActionSyncGpx.act();

        PowerMock.replayAll();
        new GeocacheListController(cacheListRefresh, null, null, menuActionSyncGpx, null, null).onResume(
                cacheListRefresh, true);
        PowerMock.verifyAll();
    }
}
