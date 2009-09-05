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
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;

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
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);

        EasyMock.expect(geocacheListControllerNull.onContextItemSelected(menuItem)).andReturn(true);
        geocacheListControllerNull.onListItemClick(listView, view, 28, 42);
        EasyMock.expect(geocacheListControllerNull.onOptionsItemSelected(menuItem)).andReturn(true);

        PowerMock.replayAll();
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null, null,
                geocacheListControllerNull, null, null);
        cacheListDelegate.onContextItemSelected(menuItem);
        cacheListDelegate.onListItemClick(listView, view, 28, 42);
        cacheListDelegate.onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnContextItemSelected() {
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);

        EasyMock.expect(geocacheListControllerNull.onContextItemSelected(menuItem)).andReturn(true);

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, null, geocacheListControllerNull, null, null)
                .onContextItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreate() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createStrictMock(GeocacheListPresenter.class);

        geocacheListPresenter.onCreate();

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, null, null, geocacheListPresenter, null).onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateOptionsMenu() {
        Menu menu = PowerMock.createMock(Menu.class);
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);

        EasyMock.expect(geocacheListControllerNull.onCreateOptionsMenu(menu)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new CacheListDelegate(null, null, null, null, geocacheListControllerNull, null,
                null).onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClick() {
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);
        ListView listView = PowerMock.createMock(ListView.class);
        View view = PowerMock.createMock(View.class);

        geocacheListControllerNull.onListItemClick(listView, view, 28, 42);

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, null, geocacheListControllerNull, null, null)
                .onListItemClick(listView, view, 28, 42);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnMenuOpened() {
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);
        Menu menu = PowerMock.createMock(Menu.class);

        EasyMock.expect(geocacheListControllerNull.onMenuOpened(27, menu)).andReturn(true);

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, null, geocacheListControllerNull, null, null)
                .onMenuOpened(27, menu);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsItemSelected() {
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        EasyMock.expect(geocacheListControllerNull.onOptionsItemSelected(menuItem)).andReturn(true);

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, null, geocacheListControllerNull, null, null)
                .onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createStrictMock(GeocacheListPresenter.class);
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);
        ActivitySaver activitySaver = PowerMock.createMock(ActivitySaver.class);

        geocacheListPresenter.onPause();
        geocacheListControllerNull.onPause();
        activitySaver.save(ActivityType.CACHE_LIST);

        PowerMock.replayAll();
        new CacheListDelegate(null, activitySaver, null, null, geocacheListControllerNull,
                geocacheListPresenter, null).onPause();
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
    public void testOnResume() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createStrictMock(GeocacheListPresenter.class);
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        TitleUpdaterFactory titleUpdaterFactory = PowerMock.createMock(TitleUpdaterFactory.class);
        TitleUpdater titleUpdater = PowerMock.createMock(TitleUpdater.class);
        CacheListRefreshFactory cacheListRefreshFactory = PowerMock
                .createMock(CacheListRefreshFactory.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        GeocacheListControllerFactory geocacheListControllerFactory = PowerMock
                .createMock(GeocacheListControllerFactory.class);
        IGeocacheListController controller = PowerMock.createMock(IGeocacheListController.class);
        ImportIntentManager importIntentManager = PowerMock.createMock(ImportIntentManager.class);

        EasyMock.expect(titleUpdaterFactory.create(sqliteWrapper)).andReturn(titleUpdater);
        EasyMock.expect(cacheListRefreshFactory.create(titleUpdater, sqliteWrapper)).andReturn(
                cacheListRefresh);
        geocacheListPresenter.onResume(cacheListRefresh);
        EasyMock
                .expect(
                        geocacheListControllerFactory.create(cacheListRefresh, titleUpdater,
                                sqliteWrapper)).andReturn(controller);
        EasyMock.expect(importIntentManager.isImport()).andReturn(true);
        controller.onResume(cacheListRefresh, true);

        PowerMock.replayAll();
        new CacheListDelegate(importIntentManager, null, cacheListRefreshFactory,
                geocacheListControllerFactory, geocacheListControllerNull, geocacheListPresenter,
                titleUpdaterFactory).onResume(sqliteWrapper);
        PowerMock.verifyAll();
    }
}
