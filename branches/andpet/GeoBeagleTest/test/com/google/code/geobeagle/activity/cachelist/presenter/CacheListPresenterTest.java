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

package com.google.code.geobeagle.activity.cachelist.presenter;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import com.google.code.geobeagle.LocationAndDirection;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.cachelist.CacheListView;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListPresenter.class, Log.class, PreferenceManager.class
})
public class CacheListPresenterTest {
    @Test
    public void testBaseAdapterLocationListener() {
        PowerMock.mockStatic(Log.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);

        expect(Log.d(isA(String.class), isA(String.class))).andReturn(0).anyTimes();
        cacheListRefresh.refresh();

        PowerMock.replayAll();
        CacheListRefreshLocationListener cacheListRefreshLocationListener = new CacheListRefreshLocationListener(
                cacheListRefresh);
        cacheListRefreshLocationListener.onLocationChanged(null);
        cacheListRefreshLocationListener.onProviderDisabled(null);
        cacheListRefreshLocationListener.onProviderEnabled(null);
        cacheListRefreshLocationListener.onStatusChanged(null, 0, null);
        PowerMock.verifyAll();
    }

    @Test
    public void testCompassOnAccuracyChanged() {
        new CompassListener(null, null, 0).onAccuracyChanged(0, 0);
    }

    @Test
    public void testCompassOnSensorChanged() {
        LocationAndDirection locationAndDirection = PowerMock
                .createMock(LocationAndDirection.class);
        Refresher refresher = PowerMock.createMock(Refresher.class);

        float values[] = new float[] {
            6f
        };
        locationAndDirection.setAzimuth(5);
        refresher.refresh();

        PowerMock.replayAll();
        final CompassListener compassListener = new CompassListener(refresher,
                locationAndDirection, 0);
        compassListener.onSensorChanged(0, values);
        PowerMock.verifyAll();
    }

    // @Test
    // public void testCompassListener() {
    // new CompassListener(null, null, 0).onAccuracyChanged(null, 0);
    // }

    @Test
    public void testCompassOnSensorUnchanged() {
        float values[] = new float[] {
            4f
        };
        final CompassListener compassListener = new CompassListener(null, null, 0);
        compassListener.onSensorChanged(0, values);
    }

    @Test
    public void testOnCreate() throws Exception {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        LocationAndDirection locationAndDirection = PowerMock
                .createMock(LocationAndDirection.class);
        CacheListOnCreateContextMenuListener listener = PowerMock
                .createMock(CacheListOnCreateContextMenuListener.class);
        ListView listView = PowerMock.createMock(ListView.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        UpdateGpsWidgetRunnable updateGpsWidgetRunnable = PowerMock
                .createMock(UpdateGpsWidgetRunnable.class);
        GpsStatusWidget gpsStatusWidget = PowerMock.createMock(GpsStatusWidget.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        CacheListView.ScrollListener scrollListener = PowerMock
                .createMock(CacheListView.ScrollListener.class);

        listActivity.setContentView(R.layout.cache_list);
        PowerMock.expectNew(CacheListOnCreateContextMenuListener.class, geocacheVectors).andReturn(
                listener);
        expect(listActivity.getListView()).andReturn(listView);
        listView.addHeaderView(gpsStatusWidget);
        listView.setOnCreateContextMenuListener(listener);
        updateGpsWidgetRunnable.run();
        listActivity.setListAdapter(geocacheListAdapter);
        listView.setOnScrollListener(scrollListener);

        PowerMock.replayAll();
        new GeocacheListPresenter(locationAndDirection, null, null, null, geocacheListAdapter,
                null, geocacheVectors, gpsStatusWidget, listActivity, locationAndDirection,
                null, updateGpsWidgetRunnable, scrollListener).onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() {
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        SensorManagerWrapper sensorManagerWrapper = PowerMock
                .createMock(SensorManagerWrapper.class);

        combinedLocationManager.removeUpdates();
        sensorManagerWrapper.unregisterListener();

        PowerMock.replayAll();
        new GeocacheListPresenter(null, combinedLocationManager, null, null, null, null, null,
                null, null, null, sensorManagerWrapper, null, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() throws Exception {
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        LocationListener gpsStatusWidgetLocationListener = PowerMock
                .createMock(LocationListener.class);
        CacheListRefreshLocationListener cacheListRefreshLocationListener = PowerMock
                .createMock(CacheListRefreshLocationListener.class);
        LocationAndDirection locationAndDirection = PowerMock
                .createMock(LocationAndDirection.class);
        SensorManagerWrapper sensorManagerWrapper = PowerMock
                .createMock(SensorManagerWrapper.class);
        CompassListenerFactory compassListenerFactory = PowerMock
                .createMock(CompassListenerFactory.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);

        PowerMock.expectNew(CacheListRefreshLocationListener.class, cacheListRefresh).andReturn(
                cacheListRefreshLocationListener);
        CompassListener compassListener = PowerMock.createMock(CompassListener.class);
        EasyMock.expect(compassListenerFactory.create(cacheListRefresh)).andReturn(compassListener);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        PowerMock.mockStatic(PreferenceManager.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        GpsStatusWidget gpsStatusWidget = PowerMock.createMock(GpsStatusWidget.class);
        DistanceFormatterManager distanceFormatterManager = PowerMock
                .createMock(DistanceFormatterManager.class);
        PowerMock.mockStatic(PreferenceManager.class);
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        GeocacheSummaryRowInflater geocacheSummaryRowInflater = PowerMock
                .createMock(GeocacheSummaryRowInflater.class);

        combinedLocationManager.requestLocationUpdates(GeocacheListPresenter.UPDATE_DELAY, 0,
                gpsStatusWidgetLocationListener);
        combinedLocationManager.requestLocationUpdates(GeocacheListPresenter.UPDATE_DELAY, 0,
                locationAndDirection);
        combinedLocationManager.requestLocationUpdates(GeocacheListPresenter.UPDATE_DELAY, 0,
                cacheListRefreshLocationListener);
        sensorManagerWrapper.registerListener(compassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        distanceFormatterManager.setFormatter();
        expect(PreferenceManager.getDefaultSharedPreferences(listActivity)).andReturn(
                sharedPreferences);
        expect(sharedPreferences.getBoolean("absolute-bearing", false)).andReturn(true);
        geocacheSummaryRowInflater.setBearingFormatter(true);

        PowerMock.replayAll();
        new GeocacheListPresenter(gpsStatusWidgetLocationListener, combinedLocationManager,
                compassListenerFactory, distanceFormatterManager, geocacheListAdapter,
                geocacheSummaryRowInflater, null, gpsStatusWidget, listActivity,
                locationAndDirection, sensorManagerWrapper, null, null)
                .onResume(cacheListRefresh);

        PowerMock.verifyAll();
    }
}
