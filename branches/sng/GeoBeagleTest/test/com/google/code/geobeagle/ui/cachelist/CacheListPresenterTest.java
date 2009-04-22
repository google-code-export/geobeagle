
package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.ui.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.ui.cachelist.GeocacheListPresenter.ResumeRunnable;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        ListActivity.class, GeocacheListPresenter.class, Handler.class, Toast.class
})
public class CacheListPresenterTest {

    @Test
    public void testCreate() throws Exception {
        ListActivity activity = PowerMock.createMock(ListActivity.class);
        CacheListOnCreateContextMenuListener listener = PowerMock
                .createMock(CacheListOnCreateContextMenuListener.class);
        ListView listView = PowerMock.createMock(ListView.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        UpdateGpsWidgetRunnable updateGpsWidgetRunnable = PowerMock
                .createMock(UpdateGpsWidgetRunnable.class);

        activity.setContentView(R.layout.cache_list);
        PowerMock.expectNew(CacheListOnCreateContextMenuListener.class, geocacheVectors).andReturn(
                listener);
        expect(activity.getListView()).andReturn(listView);
        listView.setOnCreateContextMenuListener(listener);
        updateGpsWidgetRunnable.run();

        PowerMock.replayAll();
        new GeocacheListPresenter(null, null, null, updateGpsWidgetRunnable, null, geocacheVectors,
                null, null, activity, null, null, null, null).onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateOptionsMenu() {
        Menu menu = PowerMock.createMock(Menu.class);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        MenuInflater menuInflater = PowerMock.createMock(MenuInflater.class);

        expect(listActivity.getMenuInflater()).andReturn(menuInflater);
        menuInflater.inflate(R.menu.cache_list_menu, menu);

        PowerMock.replayAll();
        GeocacheListPresenter geocacheListPresenter = new GeocacheListPresenter(null, null, null,
                null, null, null, null, null, listActivity, null, null, null, null);
        assertTrue(geocacheListPresenter.onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testPause() throws InterruptedException {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        LocationListener locationListener = PowerMock.createMock(LocationListener.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);

        locationManager.removeUpdates(locationListener);
        sqliteWrapper.close();

        PowerMock.replayAll();
        new GeocacheListPresenter(locationManager, null, locationListener, null, null, null, null,
                null, null, null, null, sqliteWrapper, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testResume() {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        LocationListener locationListener = PowerMock.createMock(LocationListener.class);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        Location here = PowerMock.createMock(Location.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Database database = PowerMock.createMock(Database.class);

        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);
        ArrayList<Geocache> locations = new ArrayList<Geocache>(0);
        sqliteWrapper.openWritableDatabase(database);
        geocachesSql.loadNearestCaches();
        expect(geocachesSql.getGeocaches()).andReturn(locations);
        expect(locationControl.getLocation()).andReturn(here);
        cacheListData.add(locations, here);
        listActivity.setListAdapter(geocacheListAdapter);
        expect(geocachesSql.getCount()).andReturn(1000);
        expect(listActivity.getString(R.string.cache_list_title, 0, 1000)).andReturn(
                "0 caches out of 1000");
        listActivity.setTitle("0 caches out of 1000");

        PowerMock.replayAll();
        new GeocacheListPresenter(locationManager, locationControl, locationListener, null,
                geocachesSql, null, geocacheListAdapter, cacheListData, listActivity, null, null,
                sqliteWrapper, database).onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void testResumeError() {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        LocationListener locationListener = PowerMock.createMock(LocationListener.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        Exception e = new RuntimeException();
        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        EasyMock.expectLastCall().andThrow(e);
        errorDisplayer.displayErrorAndStack(e);

        PowerMock.replayAll();
        new GeocacheListPresenter(locationManager, null, locationListener, null, null, null, null,
                null, null, null, errorDisplayer, null, null).onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void testResumeRunnable() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);

        geocacheListPresenter.onResume();

        PowerMock.replayAll();
        new ResumeRunnable(geocacheListPresenter).run();
        PowerMock.verifyAll();

    }

    @Test
    public void testSort() throws Exception {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        Handler handler = PowerMock.createMock(Handler.class);
        ResumeRunnable resumeRunnable = PowerMock.createMock(ResumeRunnable.class);
        Toast toast = PowerMock.createMock(Toast.class);

        PowerMock.mockStatic(Toast.class);
        EasyMock.expect(Toast.makeText(listActivity, R.string.sorting, Toast.LENGTH_SHORT))
                .andReturn(toast);
        toast.show();
        PowerMock.expectNew(ResumeRunnable.class, EasyMock.isA(GeocacheListPresenter.class))
                .andReturn(resumeRunnable);
        EasyMock.expect(handler.postDelayed(resumeRunnable, 200)).andReturn(true);

        PowerMock.replayAll();
        new GeocacheListPresenter(null, null, null, null, null, null, null, null, listActivity,
                handler, null, null, null).sort();
        PowerMock.verifyAll();
    }
}
