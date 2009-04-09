
package com.google.code.geobeagle.ui.cachelist;

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
        GeocacheListController geocacheListController = PowerMock
                .createStrictMock(GeocacheListController.class);
        ListView listView = PowerMock.createMock(ListView.class);
        View view = PowerMock.createMock(View.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        EasyMock.expect(geocacheListController.onContextItemSelected(menuItem)).andReturn(true);
        geocacheListController.onListItemClick(listView, view, 28, 42);
        EasyMock.expect(geocacheListController.onOptionsItemSelected(menuItem)).andReturn(true);

        PowerMock.replayAll();
        CacheListDelegate cacheListDelegate = new CacheListDelegate(geocacheListController, null);
        cacheListDelegate.onContextItemSelected(menuItem);
        cacheListDelegate.onListItemClick(listView, view, 28, 42);
        cacheListDelegate.onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testPresenter() {
        Menu menu = PowerMock.createMock(Menu.class);
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createStrictMock(GeocacheListPresenter.class);

        geocacheListPresenter.onCreate();
        EasyMock.expect(geocacheListPresenter.onCreateOptionsMenu(menu)).andReturn(true);
        geocacheListPresenter.onPause();
        geocacheListPresenter.onResume();

        PowerMock.replayAll();
        CacheListDelegate cacheListDelegate = new CacheListDelegate(null, geocacheListPresenter);
        cacheListDelegate.onCreate();
        cacheListDelegate.onCreateOptionsMenu(menu);
        cacheListDelegate.onPause();
        cacheListDelegate.onResume();
        PowerMock.verifyAll();
    }
}
