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

import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.DatabaseDI;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.view.Menu;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListController.class, ListActivity.class,
        CacheListOnCreateContextMenuListener.class, DatabaseDI.class
})
public class GeocacheListControllerTest {

    @Test
    public void testOnCreateOptionsMenu() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        Menu menu = PowerMock.createMock(Menu.class);
        CacheAction defaultCacheAction = PowerMock.createMock(CacheAction.class);

        EasyMock.expect(menuActions.onCreateOptionsMenu(menu)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new GeocacheListController(null, null, null, menuActions, defaultCacheAction)
                .onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClick() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        final CacheAction cacheAction = PowerMock.createMock(CacheAction.class);
        CacheAction cacheActions[] = {
                null, cacheAction
        };
        CacheListAdapter cacheListAdapter = PowerMock.createMock(CacheListAdapter.class);
        CacheAction defaultCacheAction = PowerMock.createMock(CacheAction.class);
        EasyMock.expect(cacheListAdapter.getGeocacheAt(45)).andReturn(geocache);
        defaultCacheAction.act(geocache);

        PowerMock.replayAll();
        new GeocacheListController(cacheListAdapter, cacheActions, null, null, defaultCacheAction).
          onListItemClick(null, null, 46, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() {
        MenuActionSyncGpx menuActionSync = PowerMock.createMock(MenuActionSyncGpx.class);

        menuActionSync.abort();

        PowerMock.replayAll();
        new GeocacheListController(null, null, menuActionSync, null, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        CacheListAdapter cacheList = PowerMock.createMock(CacheListAdapter.class);

        cacheList.forceRefresh();

        PowerMock.replayAll();
        new GeocacheListController(cacheList, null, null, null, null).onResume(
                false);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResumeAndImport() {
        CacheListAdapter cacheList = PowerMock.createMock(CacheListAdapter.class);
        MenuActionSyncGpx menuActionSyncGpx = PowerMock.createMock(MenuActionSyncGpx.class);

        cacheList.forceRefresh();
        menuActionSyncGpx.act();

        PowerMock.replayAll();
        new GeocacheListController(cacheList, null, menuActionSyncGpx, null, null).onResume(
                true);
        PowerMock.verifyAll();
    }
}
