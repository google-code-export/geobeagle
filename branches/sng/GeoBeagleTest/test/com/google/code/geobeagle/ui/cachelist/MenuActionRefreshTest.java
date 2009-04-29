
package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.ui.cachelist.MenuActionRefresh.SortRunnable;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;

@PrepareForTest( {
        Handler.class, ListActivity.class, MenuActionRefresh.class, Toast.class
})
@RunWith(PowerMockRunner.class)
public class MenuActionRefreshTest {

    @Test
    public void testAct() throws Exception {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        Handler handler = PowerMock.createMock(Handler.class);
        MenuActionRefresh.SortRunnable sortRunnable = PowerMock
                .createMock(MenuActionRefresh.SortRunnable.class);
        Toast toast = PowerMock.createMock(Toast.class);

        PowerMock.mockStatic(Toast.class);
        EasyMock.expect(Toast.makeText(listActivity, R.string.sorting, Toast.LENGTH_SHORT))
                .andReturn(toast);
        toast.show();
        PowerMock.expectNew(SortRunnable.class, EasyMock.isA(MenuActionRefresh.class)).andReturn(
                sortRunnable);
        EasyMock.expect(handler.postDelayed(sortRunnable, 200)).andReturn(true);

        PowerMock.replayAll();
        new MenuActionRefresh(listActivity, handler, null, null, null, null).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testSort() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);

        ArrayList<Geocache> locations = new ArrayList<Geocache>(0);
        geocachesSql.loadNearestCaches(locationControlBuffered);
        expect(geocachesSql.getGeocaches()).andReturn(locations);
        cacheListData.add(locations, locationControlBuffered);
        listActivity.setListAdapter(geocacheListAdapter);
        expect(geocachesSql.getCount()).andReturn(1000);
        expect(listActivity.getString(R.string.cache_list_title, 0, 1000)).andReturn(
                "0 caches out of 1000");
        listActivity.setTitle("0 caches out of 1000");

        PowerMock.replayAll();
        new MenuActionRefresh(listActivity, null, locationControlBuffered, geocachesSql,
                cacheListData, geocacheListAdapter).sort();
        PowerMock.verifyAll();
    }

    @Test
    public void testSortRunnable() {
        MenuActionRefresh menuActionRefresh = PowerMock.createMock(MenuActionRefresh.class);

        menuActionRefresh.sort();

        PowerMock.replayAll();
        new MenuActionRefresh.SortRunnable(menuActionRefresh).run();
        PowerMock.verifyAll();
    }
}
