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

package com.google.code.geobeagle.cachelist;

import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegate;
import com.google.code.geobeagle.activity.cachelist.CacheListRefreshFactory;
import com.google.code.geobeagle.activity.cachelist.GeocacheListControllerFactory;
import com.google.code.geobeagle.activity.cachelist.GeocacheListControllerNull;
import com.google.code.geobeagle.activity.cachelist.IGeocacheListController;
import com.google.code.geobeagle.activity.cachelist.TitleUpdaterFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.NullClosable;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

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
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, null, null, null, null,
                null, geocacheListControllerNull, null);
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
        new CacheListDelegate(null, null, null, null, null, null, geocacheListControllerNull, null)
                .onContextItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreate() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createStrictMock(GeocacheListPresenter.class);

        geocacheListPresenter.onCreate();

        PowerMock.replayAll();
        new CacheListDelegate(null, null, null, geocacheListPresenter, null, null, null, null)
                .onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnCreateOptionsMenu() {
        Menu menu = PowerMock.createMock(Menu.class);
        GeocacheListControllerNull geocacheListControllerNull = PowerMock
                .createStrictMock(GeocacheListControllerNull.class);

        EasyMock.expect(geocacheListControllerNull.onCreateOptionsMenu(menu)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new CacheListDelegate(null, null, null, null, null, null,
                geocacheListControllerNull, null).onCreateOptionsMenu(menu));
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
        new CacheListDelegate(null, null, null, null, null, null, geocacheListControllerNull, null)
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
        new CacheListDelegate(null, null, null, null, null, null, geocacheListControllerNull, null)
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
        new CacheListDelegate(null, null, null, null, null, null, geocacheListControllerNull, null)
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
        NullClosable sqliteDatabaseNull = PowerMock.createMock(NullClosable.class);

        geocacheListPresenter.onPause();
        geocacheListControllerNull.onPause();
        activitySaver.save(ActivityType.CACHE_LIST);
        sqliteDatabaseNull.close();

        PowerMock.replayAll();
        new CacheListDelegate(activitySaver, null, null, geocacheListPresenter, null, null,
                geocacheListControllerNull, sqliteDatabaseNull).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        GeoBeagleSqliteOpenHelper geoBeagleSqliteOpenHelper = PowerMock
                .createMock(GeoBeagleSqliteOpenHelper.class);
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

        EasyMock.expect(geoBeagleSqliteOpenHelper.getWritableSqliteWrapper()).andReturn(
                sqliteWrapper);
        EasyMock.expect(titleUpdaterFactory.create(sqliteWrapper)).andReturn(titleUpdater);
        EasyMock.expect(cacheListRefreshFactory.create(titleUpdater, sqliteWrapper)).andReturn(
                cacheListRefresh);
        geocacheListPresenter.onResume(cacheListRefresh);
        EasyMock
                .expect(
                        geocacheListControllerFactory.create(cacheListRefresh, titleUpdater,
                                sqliteWrapper)).andReturn(controller);
        controller.onResume(cacheListRefresh);

        PowerMock.replayAll();
        new CacheListDelegate(null, cacheListRefreshFactory, geocacheListControllerFactory,
                geocacheListPresenter, geoBeagleSqliteOpenHelper, titleUpdaterFactory,
                geocacheListControllerNull, null).onResume();
        PowerMock.verifyAll();
    }
}
