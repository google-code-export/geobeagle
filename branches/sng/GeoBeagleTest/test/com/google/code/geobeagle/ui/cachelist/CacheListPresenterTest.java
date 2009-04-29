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

package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.CombinedLocationManager;
import com.google.code.geobeagle.LocationControlBuffered;
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
import com.google.code.geobeagle.ui.cachelist.GeocacheListPresenter.BaseAdapterLocationListener;
import com.google.code.geobeagle.ui.cachelist.GeocacheListPresenter.SortRunnable;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.location.LocationListener;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListPresenter.class, Handler.class, ListActivity.class, Log.class, Toast.class
})
public class CacheListPresenterTest {
    @Test
    public void testBaseAdapterLocationListener() {
        PowerMock.mockStatic(Log.class);
        BaseAdapter baseAdapter = PowerMock.createMock(BaseAdapter.class);

        EasyMock.expect(Log.v((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0);
        baseAdapter.notifyDataSetChanged();

        PowerMock.replayAll();
        BaseAdapterLocationListener baseAdapterLocationListener = new BaseAdapterLocationListener(
                baseAdapter);
        baseAdapterLocationListener.onLocationChanged(null);
        baseAdapterLocationListener.onProviderDisabled(null);
        baseAdapterLocationListener.onProviderEnabled(null);
        baseAdapterLocationListener.onStatusChanged(null, 0, null);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreate() throws Exception {
        ListActivity activity = PowerMock.createMock(ListActivity.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        CacheListOnCreateContextMenuListener listener = PowerMock
                .createMock(CacheListOnCreateContextMenuListener.class);
        ListView listView = PowerMock.createMock(ListView.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        UpdateGpsWidgetRunnable updateGpsWidgetRunnable = PowerMock
                .createMock(UpdateGpsWidgetRunnable.class);
        View gpsWidgetView = PowerMock.createMock(View.class);

        locationControlBuffered.onLocationChanged(null);
        activity.setContentView(R.layout.cache_list);
        PowerMock.expectNew(CacheListOnCreateContextMenuListener.class, geocacheVectors).andReturn(
                listener);
        expect(activity.getListView()).andReturn(listView);
        listView.addHeaderView(gpsWidgetView);
        listView.setOnCreateContextMenuListener(listener);
        updateGpsWidgetRunnable.run();

        PowerMock.replayAll();
        new GeocacheListPresenter(null, locationControlBuffered, null, gpsWidgetView,
                updateGpsWidgetRunnable, null, geocacheVectors, null, null, null, activity, null,
                null, null, null).onCreate();
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
                null, null, null, null, null, null, null, listActivity, null, null, null, null);
        assertTrue(geocacheListPresenter.onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testPause() throws InterruptedException {
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        LocationListener gpsStatusWidgetLocationListener = PowerMock
                .createMock(LocationListener.class);
        BaseAdapterLocationListener baseAdapterLocationListener = PowerMock
                .createMock(BaseAdapterLocationListener.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);

        combinedLocationManager.removeUpdates(baseAdapterLocationListener);
        combinedLocationManager.removeUpdates(gpsStatusWidgetLocationListener);
        combinedLocationManager.removeUpdates(locationControlBuffered);
        sqliteWrapper.close();

        PowerMock.replayAll();
        new GeocacheListPresenter(combinedLocationManager, locationControlBuffered,
                gpsStatusWidgetLocationListener, null, null, null, null, null,
                baseAdapterLocationListener, null, null, null, null, sqliteWrapper, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testResume() {
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        LocationListener gpsStatusWidgetLocationListener = PowerMock
                .createMock(LocationListener.class);
        BaseAdapterLocationListener baseAdapterLocationListener = PowerMock
                .createMock(BaseAdapterLocationListener.class);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        CacheListData cacheListData = PowerMock.createMock(CacheListData.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Database database = PowerMock.createMock(Database.class);

        combinedLocationManager.requestLocationUpdates(0, 0, gpsStatusWidgetLocationListener);
        combinedLocationManager.requestLocationUpdates(0, 0, locationControlBuffered);
        combinedLocationManager.requestLocationUpdates(0, 10, baseAdapterLocationListener);
        ArrayList<Geocache> locations = new ArrayList<Geocache>(0);
        sqliteWrapper.openWritableDatabase(database);
        geocachesSql.loadNearestCaches(locationControlBuffered);
        expect(geocachesSql.getGeocaches()).andReturn(locations);
        cacheListData.add(locations, locationControlBuffered);
        listActivity.setListAdapter(geocacheListAdapter);
        expect(geocachesSql.getCount()).andReturn(1000);
        expect(listActivity.getString(R.string.cache_list_title, 0, 1000)).andReturn(
                "0 caches out of 1000");
        listActivity.setTitle("0 caches out of 1000");

        PowerMock.replayAll();
        new GeocacheListPresenter(combinedLocationManager, locationControlBuffered,
                gpsStatusWidgetLocationListener, null, null, geocachesSql, null,
                geocacheListAdapter, baseAdapterLocationListener, cacheListData, listActivity,
                null, null, sqliteWrapper, database).onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void testResumeError() {
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        Exception e = new RuntimeException();
        combinedLocationManager.requestLocationUpdates(0, 0, locationControlBuffered);
        EasyMock.expectLastCall().andThrow(e);
        errorDisplayer.displayErrorAndStack(e);

        PowerMock.replayAll();
        new GeocacheListPresenter(combinedLocationManager, locationControlBuffered, null, null,
                null, null, null, null, null, null, null, null, errorDisplayer, null, null)
                .onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void testResumeRunnable() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);

        geocacheListPresenter.sort();

        PowerMock.replayAll();
        new SortRunnable(geocacheListPresenter).run();
        PowerMock.verifyAll();

    }

    @Test
    public void testSort() throws Exception {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        Handler handler = PowerMock.createMock(Handler.class);
        SortRunnable sortRunnable = PowerMock.createMock(SortRunnable.class);
        Toast toast = PowerMock.createMock(Toast.class);

        PowerMock.mockStatic(Toast.class);
        EasyMock.expect(Toast.makeText(listActivity, R.string.sorting, Toast.LENGTH_SHORT))
                .andReturn(toast);
        toast.show();
        PowerMock.expectNew(SortRunnable.class, EasyMock.isA(GeocacheListPresenter.class))
                .andReturn(sortRunnable);
        EasyMock.expect(handler.postDelayed(sortRunnable, 200)).andReturn(true);

        PowerMock.replayAll();
        new GeocacheListPresenter(null, null, null, null, null, null, null, null, null, null,
                listActivity, handler, null, null, null).doSort();
        PowerMock.verifyAll();
    }
}
