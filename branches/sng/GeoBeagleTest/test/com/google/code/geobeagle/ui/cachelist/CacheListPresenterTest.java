
package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.ui.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        ListActivity.class, GeocacheListPresenter.class
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
                null, null, activity, null).onCreate();
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
                null, null, null, null, null, listActivity, null);
        assertTrue(geocacheListPresenter.onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testPause() throws InterruptedException {
        LocationManager locationManager = PowerMock.createMock(LocationManager.class);
        LocationListener locationListener = PowerMock.createMock(LocationListener.class);

        locationManager.removeUpdates(locationListener);

        PowerMock.replayAll();
        new GeocacheListPresenter(locationManager, null, locationListener, null, null, null, null,
                null, null, null).onPause();
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

        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);
        ArrayList<Geocache> locations = new ArrayList<Geocache>(0);
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
                geocachesSql, null, geocacheListAdapter, cacheListData, listActivity, null)
                .onResume();
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
                null, null, errorDisplayer).onResume();
        PowerMock.verifyAll();
    }
}
