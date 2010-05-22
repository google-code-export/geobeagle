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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegate.ImportIntentManager;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.inject.Provider;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

@RunWith(PowerMockRunner.class)
public class CacheListDelegateTest {
    @Test
    public void testController() {
        ListView listView = PowerMock.createMock(ListView.class);
        View view = PowerMock.createMock(View.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        GeocacheListController geocacheListController = PowerMock
                .createStrictMock(GeocacheListController.class);

        EasyMock.expect(geocacheListController.onContextItemSelected(menuItem)).andReturn(true);
        geocacheListController.onListItemClick(listView, view, 28, 42);
        EasyMock.expect(geocacheListController.onOptionsItemSelected(menuItem)).andReturn(true);

        PowerMock.replayAll();
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null,
                geocacheListController, null, null, null);
        cacheListDelegate.onContextItemSelected(menuItem);
        cacheListDelegate.onListItemClick(listView, view, 28, 42);
        cacheListDelegate.onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testImportAlreadyTriggered() {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        EasyMock.expect(activity.getIntent()).andReturn(intent);
        EasyMock.expect(intent.getAction()).andReturn("android.intent.action.VIEW");
        EasyMock.expect(
                intent.getBooleanExtra(ImportIntentManager.INTENT_EXTRA_IMPORT_TRIGGERED, false))
                .andReturn(true);

        PowerMock.replayAll();
        assertFalse(new ImportIntentManager(activity).isImport());
        PowerMock.verifyAll();
    }

    @Test
    public void testImportEmptyAction() {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        EasyMock.expect(activity.getIntent()).andReturn(intent);
        EasyMock.expect(intent.getAction()).andReturn(null);

        PowerMock.replayAll();
        assertFalse(new ImportIntentManager(activity).isImport());
        PowerMock.verifyAll();
    }

    @Test
    public void testImportNotView() {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        EasyMock.expect(activity.getIntent()).andReturn(intent);
        EasyMock.expect(intent.getAction()).andReturn("android.intent.action.EDIT");

        PowerMock.replayAll();
        assertFalse(new ImportIntentManager(activity).isImport());
        PowerMock.verifyAll();
    }

    @Test
    public void testImportNullIntent() {
        Activity activity = PowerMock.createMock(Activity.class);

        EasyMock.expect(activity.getIntent()).andReturn(null);

        PowerMock.replayAll();
        assertFalse(new ImportIntentManager(activity).isImport());
        PowerMock.verifyAll();
    }

    @Test
    public void testImportTrue() {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        EasyMock.expect(activity.getIntent()).andReturn(intent);
        EasyMock.expect(intent.getAction()).andReturn("android.intent.action.VIEW");
        EasyMock.expect(
                intent.getBooleanExtra(ImportIntentManager.INTENT_EXTRA_IMPORT_TRIGGERED, false))
                .andReturn(false);
        EasyMock.expect(intent.putExtra(ImportIntentManager.INTENT_EXTRA_IMPORT_TRIGGERED, true))
                .andReturn(intent);

        PowerMock.replayAll();
        assertTrue(new ImportIntentManager(activity).isImport());
        PowerMock.verifyAll();
    }

    @Test
    public void testOnContextItemSelected() {
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        GeocacheListController geocacheListController = PowerMock
                .createStrictMock(GeocacheListController.class);

        EasyMock.expect(geocacheListController.onContextItemSelected(menuItem)).andReturn(true);

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, geocacheListController, null, null, null)
                .onContextItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreate() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createStrictMock(GeocacheListPresenter.class);

        geocacheListPresenter.onCreate();

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, null, geocacheListPresenter, null, null).onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateOptionsMenu() {
        Menu menu = PowerMock.createMock(Menu.class);
        GeocacheListController geocacheListController = PowerMock
                .createStrictMock(GeocacheListController.class);

        EasyMock.expect(geocacheListController.onCreateOptionsMenu(menu)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new CacheListDelegate(null, null, null, geocacheListController, null, null, null)
                .onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClick() {
        GeocacheListController geocacheListController = PowerMock
                .createStrictMock(GeocacheListController.class);
        ListView listView = PowerMock.createMock(ListView.class);
        View view = PowerMock.createMock(View.class);

        geocacheListController.onListItemClick(listView, view, 28, 42);

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, geocacheListController, null, null, null)
                .onListItemClick(listView, view, 28, 42);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnMenuOpened() {
        GeocacheListController geocacheListController = PowerMock
                .createStrictMock(GeocacheListController.class);
        Menu menu = PowerMock.createMock(Menu.class);

        EasyMock.expect(geocacheListController.onMenuOpened(27, menu)).andReturn(true);

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, geocacheListController, null, null, null).onMenuOpened(
                27, menu);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsItemSelected() {
        GeocacheListController geocacheListController = PowerMock
                .createStrictMock(GeocacheListController.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        EasyMock.expect(geocacheListController.onOptionsItemSelected(menuItem)).andReturn(true);

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, geocacheListController, null, null, null)
                .onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createStrictMock(GeocacheListPresenter.class);
        GeocacheListController geocacheListController = PowerMock
                .createMock(GeocacheListController.class);
        ActivitySaver activitySaver = PowerMock.createMock(ActivitySaver.class);
        Provider<DbFrontend> dbFrontendProvider = PowerMock.createMock(Provider.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        
        geocacheListPresenter.onPause();
        geocacheListController.onPause();
        activitySaver.save(ActivityType.CACHE_LIST);
        EasyMock.expect(dbFrontendProvider.get()).andReturn(dbFrontend);
        dbFrontend.closeDatabase();

        PowerMock.replayAll();
        new CacheListDelegate(null, activitySaver, null, geocacheListController,
                geocacheListPresenter, dbFrontendProvider, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createStrictMock(GeocacheListPresenter.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        GeocacheListController controller = PowerMock.createMock(GeocacheListController.class);
        ImportIntentManager importIntentManager = PowerMock.createMock(ImportIntentManager.class);

        geocacheListPresenter.onResume(cacheListRefresh);
        EasyMock.expect(importIntentManager.isImport()).andReturn(true);
        controller.onResume(cacheListRefresh, true);

        PowerMock.replayAll();
        new CacheListDelegate(importIntentManager, null, cacheListRefresh, controller,
                geocacheListPresenter, null, null).onResume();
        PowerMock.verifyAll();
    }
}
