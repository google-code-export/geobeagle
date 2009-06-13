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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.context.ContextAction;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActions;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.database.Database;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.xmlimport.GpxImporter;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListController.class, ListActivity.class, GeocacheListPresenter.class,
        CacheListOnCreateContextMenuListener.class
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
        expect(menu.add(0, GeocacheListController.MENU_DELETE, 1, "Delete")).andReturn(null);

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
        ContextAction contextAction = PowerMock.createMock(ContextAction.class);

        ContextAction contextActions[] = {
                null, null, contextAction
        };
        expect(menuItem.getMenuInfo()).andReturn(adapterContextMenuInfo);
        expect(menuItem.getItemId()).andReturn(2);
        contextAction.act(75);

        PowerMock.replayAll();
        adapterContextMenuInfo.position = 76;
        GeocacheListController geocacheListController = new GeocacheListController(null, null,
                contextActions, null, null, null, null, null, null);
        assertTrue(geocacheListController.onContextItemSelected(menuItem));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnContextItemSelectedError() {
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        RuntimeException runtimeException = new RuntimeException();
        expect(menuItem.getMenuInfo()).andThrow(runtimeException);
        errorDisplayer.displayErrorAndStack(runtimeException);

        PowerMock.replayAll();
        GeocacheListController geocacheListController = new GeocacheListController(null, null,
                null, null, null, null, null, null, errorDisplayer);
        assertTrue(geocacheListController.onContextItemSelected(menuItem));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreate() throws InterruptedException {
        PowerMock.replayAll();
        new GeocacheListController(null, null, null, null, null, null, null, null, null).onCreate();
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
        EasyMock.expect(contextMenu.add(0, GeocacheListController.MENU_DELETE, 1, "Delete"))
                .andReturn(menuItem);

        PowerMock.replayAll();
        new CacheListOnCreateContextMenuListener(geocacheVectors).onCreateContextMenu(contextMenu,
                null, contextMenuInfo);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateOptionsMenu() {
        Menu menu = PowerMock.createMock(Menu.class);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        MenuInflater menuInflater = PowerMock.createMock(MenuInflater.class);

        expect(listActivity.getMenuInflater()).andReturn(menuInflater);
        menuInflater.inflate(R.menu.cache_list_menu, menu);

        PowerMock.replayAll();
        GeocacheListController geocacheListController = new GeocacheListController(listActivity,
                null, null, null, null, null, null, null, null);
        assertTrue(geocacheListController.onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClick() {
        final ContextAction contextAction = PowerMock.createMock(ContextAction.class);
        ContextAction contextActions[] = {
                null, contextAction
        };

        contextAction.act(45);

        PowerMock.replayAll();
        new GeocacheListController(null, null, contextActions, null, null, null, null, null, null)
                .onListItemClick(null, null, 46, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClickError() {
        final ContextAction contextAction = PowerMock.createMock(ContextAction.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        ContextAction contextActions[] = {
                null, contextAction
        };
        RuntimeException runtimeException = new RuntimeException();
        contextAction.act(46);
        EasyMock.expectLastCall().andThrow(runtimeException);
        errorDisplayer.displayErrorAndStack(runtimeException);

        PowerMock.replayAll();
        new GeocacheListController(null, null, contextActions, null, null, null, null, null,
                errorDisplayer).onListItemClick(null, null, 47, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClickZero() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);

        cacheListRefresh.forceRefresh();

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, null, null, null, cacheListRefresh, null, null)
                .onListItemClick(null, null, 0, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnMenuOpened() {
        Menu menu = PowerMock.createMock(Menu.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);

        EasyMock.expect(menu.findItem(R.id.menu_toggle_filter)).andReturn(menuItem);
        EasyMock.expect(filterNearestCaches.getMenuString()).andReturn(
                R.string.menu_show_nearest_caches);
        EasyMock.expect(menuItem.setTitle(R.string.menu_show_nearest_caches)).andReturn(menuItem);

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, null, null, null, null, filterNearestCaches,
                null).onMenuOpened(0, menu);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsItemSelected() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        expect(menuItem.getItemId()).andReturn(27);
        menuActions.act(27);

        PowerMock.replayAll();
        new GeocacheListController(null, menuActions, null, null, null, null, null, null, null)
                .onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsItemSelectedError() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        Exception exception = new RuntimeException();
        expect(menuItem.getItemId()).andThrow(exception);
        errorDisplayer.displayErrorAndStack(exception);

        PowerMock.replayAll();
        new GeocacheListController(null, menuActions, null, null, null, null, null, null,
                errorDisplayer).onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() throws InterruptedException {
        GpxImporter gpxImporter = PowerMock.createMock(GpxImporter.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Database database = PowerMock.createMock(Database.class);

        gpxImporter.abort();
        sqliteWrapper.close();

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, sqliteWrapper, database, gpxImporter, null,
                null, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPauseError() throws InterruptedException {
        GpxImporter gpxImporter = PowerMock.createMock(GpxImporter.class);

        gpxImporter.abort();
        EasyMock.expectLastCall().andThrow(new InterruptedException());

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, null, null, gpxImporter, null, null, null)
                .onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() throws InterruptedException {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Database database = PowerMock.createMock(Database.class);

        sqliteWrapper.openWritableDatabase(database);
        cacheListRefresh.forceRefresh();

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, sqliteWrapper, database, null, cacheListRefresh, null, null)
                .onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResumeError() throws InterruptedException {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Database database = PowerMock.createMock(Database.class);

        sqliteWrapper.openWritableDatabase(database);
        cacheListRefresh.forceRefresh();
        RuntimeException runtimeException = new RuntimeException();
        EasyMock.expectLastCall().andThrow(runtimeException);
        errorDisplayer.displayErrorAndStack(runtimeException);

        PowerMock.replayAll();
        new GeocacheListController(null, null, null, sqliteWrapper, database, null,
                cacheListRefresh, null, errorDisplayer).onResume();
        PowerMock.verifyAll();
    }
}