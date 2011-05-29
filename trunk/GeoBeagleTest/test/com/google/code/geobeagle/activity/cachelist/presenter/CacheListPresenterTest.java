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

import com.google.code.geobeagle.Azimuth;
import com.google.code.geobeagle.CacheListCompassListener;
import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.database.filter.FilterCleanliness;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.shakewaker.ShakeWaker;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListPresenter.class, Log.class, PreferenceManager.class
})
public class CacheListPresenterTest extends GeoBeagleTest {
    private Azimuth azimuth;

    @Before
    public void setUp() {
        azimuth = PowerMock.createMock(Azimuth.class);
    }

    @Test
    public void testBaseAdapterLocationListener() {
        PowerMock.mockStatic(Log.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);

        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
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
        new CompassListener(null, null, null).onAccuracyChanged(null, 0);
    }

    @Test
    public void testCompassOnSensorChanged() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Refresher refresher = PowerMock.createMock(Refresher.class);

        azimuth.sensorChanged(null);
        EasyMock.expect(azimuth.getAzimuth()).andReturn(12.0);
        locationControlBuffered.setAzimuth(10);
        refresher.refresh();

        PowerMock.replayAll();
        final CompassListener compassListener = new CompassListener(refresher,
                locationControlBuffered, azimuth);
        compassListener.onSensorChanged(null);
        PowerMock.verifyAll();
    }

    @Test
    public void testCompassOnSensorUnchanged() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Refresher refresher = PowerMock.createMock(Refresher.class);
        float values[] = new float[] {
            4f
        };
        final CompassListener compassListener = new CompassListener(refresher,
                locationControlBuffered, azimuth);
        compassListener.onSensorChanged(null);
    }

    @Test
    public void testOnPause() {
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        SensorManagerWrapper sensorManagerWrapper = PowerMock
                .createMock(SensorManagerWrapper.class);
        ShakeWaker shakeWaker = PowerMock.createMock(ShakeWaker.class);

        combinedLocationManager.removeUpdates();
        sensorManagerWrapper.unregisterListener();
        shakeWaker.unregister();
        PowerMock.replayAll();
        new GeocacheListPresenter(null, combinedLocationManager, null, null, null, null, null,
                null, sensorManagerWrapper, null, null, null, null, null, shakeWaker, null, null)
                .onPause();
        PowerMock.verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnResume() throws Exception {
        CombinedLocationListener combinedLocationListener = PowerMock
                .createMock(CombinedLocationListener.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        CacheListRefreshLocationListener cacheListRefreshLocationListener = PowerMock
                .createMock(CacheListRefreshLocationListener.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        SensorManagerWrapper sensorManagerWrapper = PowerMock
                .createMock(SensorManagerWrapper.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        FilterCleanliness filterCleanliness = PowerMock.createMock(FilterCleanliness.class);
        Provider<CacheListCompassListener> cacheListCompassListenerProvider = PowerMock
                .createMock(Provider.class);
        GpsStatusListener gpsStatusListener = PowerMock.createMock(GpsStatusListener.class);
        UpdateGpsWidgetRunnable updateGpsRunnable = PowerMock
                .createMock(UpdateGpsWidgetRunnable.class);
        CacheListCompassListener cacheListCompassListener = PowerMock
                .createMock(CacheListCompassListener.class);
        ShakeWaker shakeWaker = PowerMock.createMock(ShakeWaker.class);

        expect(filterCleanliness.isDirty()).andReturn(false);
        PowerMock.expectNew(CacheListRefreshLocationListener.class, cacheListRefresh).andReturn(
                cacheListRefreshLocationListener);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        PowerMock.mockStatic(PreferenceManager.class);
        PowerMock.mockStatic(PreferenceManager.class);

        expect(cacheListCompassListenerProvider.get()).andReturn(cacheListCompassListener);
        updateGpsRunnable.run();
        combinedLocationManager.requestLocationUpdates(GeocacheListPresenter.UPDATE_DELAY, 0,
                locationControlBuffered);
        combinedLocationManager.requestLocationUpdates(GeocacheListPresenter.UPDATE_DELAY, 0,
                combinedLocationListener);
        combinedLocationManager.requestLocationUpdates(GeocacheListPresenter.UPDATE_DELAY, 0,
                cacheListRefreshLocationListener);
        combinedLocationManager.addGpsStatusListener(gpsStatusListener);
        sensorManagerWrapper.registerListener(cacheListCompassListener,
                SensorManager.SENSOR_DELAY_UI);
        shakeWaker.register();

        PowerMock.replayAll();
        new GeocacheListPresenter(combinedLocationListener, combinedLocationManager, null,
                cacheListCompassListenerProvider, null, null, listActivity,
                locationControlBuffered, sensorManagerWrapper, updateGpsRunnable, null,
                gpsStatusListener, null, filterCleanliness, shakeWaker, null, null)
                .onResume(cacheListRefresh);

        PowerMock.verifyAll();
    }
}
