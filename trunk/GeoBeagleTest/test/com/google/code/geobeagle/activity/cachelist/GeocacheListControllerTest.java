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

import com.google.code.geobeagle.actions.ContextActions;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListPresenter;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.xmlimport.AbortState;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListController.class, ListActivity.class, CacheListPresenter.class,
        CacheListOnCreateContextMenuListener.class, DatabaseDI.class, Log.class
})
public class GeocacheListControllerTest extends GeoBeagleTest {
    private AbortState abortState;
    private ContextMenu contextMenu;
    private CacheListRefresh cacheListRefresh;
    private AdapterContextMenuInfo adapterContextMenuInfo;

    @Before
    public void setUp() {
        abortState = PowerMock.createMock(AbortState.class);
        adapterContextMenuInfo = PowerMock.createMock(AdapterContextMenuInfo.class);
        cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        contextMenu = PowerMock.createMock(ContextMenu.class);
    }

    @Test
    public void testCacheListOnCreateContextMenuListener() {
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);

        expect(geocacheVectors.get(11)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        expect(contextMenu.setHeaderTitle("GC123")).andReturn(contextMenu);
        expect(contextMenu.add(0, GeocacheListController.MENU_VIEW, 0, "View")).andReturn(null);
        expect(contextMenu.add(0, GeocacheListController.MENU_EDIT, 1, "Edit")).andReturn(null);
        expect(contextMenu.add(0, GeocacheListController.MENU_DELETE, 2, "Delete")).andReturn(null);

        PowerMock.replayAll();
        adapterContextMenuInfo.position = 12;
        new CacheListOnCreateContextMenuListener(geocacheVectors).onCreateContextMenu(contextMenu, null,
                adapterContextMenuInfo);
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnContextItemSelected() {
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        AdapterContextMenuInfo adapterContextMenuInfo = PowerMock
                .createMock(AdapterContextMenuInfo.class);
        ContextActions contextActions = PowerMock.createMock(ContextActions.class);
        Provider<ContextActions> contextActionsProvider = PowerMock.createMock(Provider.class);

        expect(contextActionsProvider.get()).andReturn(contextActions);
        expect(menuItem.getMenuInfo()).andReturn(adapterContextMenuInfo);
        expect(menuItem.getItemId()).andReturn(2);
        contextActions.act(2, 75);

        PowerMock.replayAll();
        adapterContextMenuInfo.position = 76;
        GeocacheListController geocacheListController = new GeocacheListController(null, null,
                null, null, contextActionsProvider);
        assertTrue(geocacheListController.onContextItemSelected(menuItem));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateContextMenu() {
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
    @SuppressWarnings("unchecked")
    public void testOnCreateOptionsMenu() {
        Menu menu = PowerMock.createMock(Menu.class);
        Provider<CacheListMenuActions> menuActionsProvider = PowerMock.createMock(Provider.class);
        CacheListMenuActions menuActions = PowerMock.createMock(CacheListMenuActions.class);

        expect(menuActionsProvider.get()).andReturn(menuActions);
        expect(menuActions.onCreateOptionsMenu(menu)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new GeocacheListController(null, null, null, menuActionsProvider, null)
                .onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnListItemClick() {
        ContextActions contextActions = PowerMock.createMock(ContextActions.class);
        Provider<ContextActions> contextActionsProvider = PowerMock.createMock(Provider.class);

        expect(contextActionsProvider.get()).andReturn(contextActions);
        contextActions.act(1, 45);

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, null, contextActionsProvider)
                .onListItemClick(
                46);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClickZero() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        cacheListRefresh.forceRefresh();

        PowerMock.replayAll();
        new GeocacheListController(cacheListRefresh, null, null, null, null).onListItemClick(0);
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnOptionsItemSelected() {
        Provider<CacheListMenuActions> menuActionsProvider = PowerMock.createMock(Provider.class);
        CacheListMenuActions menuActions = PowerMock.createMock(CacheListMenuActions.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        expect(menuActionsProvider.get()).andReturn(menuActions);
        expect(menuItem.getItemId()).andReturn(27);
        expect(menuActions.act(27)).andReturn(true);

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, menuActionsProvider, null)
                .onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnPause() {
        Provider<MenuActionSyncGpx> menuActionSyncProvider = PowerMock.createMock(Provider.class);
        MenuActionSyncGpx menuActionSync = PowerMock.createMock(MenuActionSyncGpx.class);

        expect(menuActionSyncProvider.get()).andReturn(menuActionSync);
        abortState.abort();
        menuActionSync.abort();

        PowerMock.replayAll();
        new GeocacheListController(null, abortState, menuActionSyncProvider, null, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        cacheListRefresh.forceRefresh();

        PowerMock.replayAll();
        new GeocacheListController(cacheListRefresh, null, null, null, null).onResume(
                false);
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnResumeAndImport() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        Provider<MenuActionSyncGpx> menuActionSyncGpxProvider = PowerMock
                .createMock(Provider.class);
        MenuActionSyncGpx menuActionSyncGpx = PowerMock.createMock(MenuActionSyncGpx.class);

        expect(menuActionSyncGpxProvider.get()).andReturn(menuActionSyncGpx);
        cacheListRefresh.forceRefresh();
        menuActionSyncGpx.act();

        PowerMock.replayAll();
        new GeocacheListController(cacheListRefresh, null, menuActionSyncGpxProvider, null, null)
                .onResume(true);
        PowerMock.verifyAll();
    }
}
