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

package com.google.code.geobeagle.activity.compass;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.compass.AppLifecycleManager;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.compass.GeoBeagleActivityMenuActions;
import com.google.code.geobeagle.activity.compass.CompassActivityDelegate;
import com.google.code.geobeagle.activity.compass.GeoBeagleSensors;
import com.google.code.geobeagle.activity.compass.GeocacheFromIntentFactory;
import com.google.code.geobeagle.activity.compass.GeocacheFromParcelFactory;
import com.google.code.geobeagle.activity.compass.IncomingIntentHandler;
import com.google.code.geobeagle.activity.compass.LogFindClickListener;
import com.google.code.geobeagle.activity.compass.RadarView;
import com.google.code.geobeagle.activity.compass.SatelliteCountListener;
import com.google.code.geobeagle.activity.compass.Util;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteStringsFVsDnf;
import com.google.code.geobeagle.activity.compass.view.CheckDetailsButton;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer;
import com.google.code.geobeagle.activity.compass.view.WebPageMenuEnabler;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.shakewaker.ShakeWaker;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

@PrepareForTest({
        KeyEvent.class, DateFormat.class, Intent.class, Bundle.class, FieldnoteStringsFVsDnf.class,
        FieldnoteLogger.class, CompassActivityDelegate.class, Log.class, Uri.class, DatabaseDI.class,
        UrlQuerySanitizer.class, GeocacheFromIntentFactory.class, Util.class, Activity.class
})
@RunWith(PowerMockRunner.class)
public class CompassActivityDelegateTest extends GeoBeagleTest {

    private GeoBeagleSensors geoBeagleSensors;
    private SensorManager sensorManager;
    private RadarView radarView;
    private SharedPreferences sharedPreferences;
    private CompassListener compassListener;
    private GeoBeagleEnvironment geoBeagleEnvironment;
    private ShakeWaker shakeWaker;
    private Provider<LocationManager> locationManagerProvider;
    private SatelliteCountListener satelliteCountListener;
    private LocationManager locationManager;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        geoBeagleSensors = createMock(GeoBeagleSensors.class);
        sensorManager = createMock(SensorManager.class);
        radarView = createMock(RadarView.class);
        sharedPreferences = createMock(SharedPreferences.class);
        compassListener = createMock(CompassListener.class);
        geoBeagleEnvironment = createMock(GeoBeagleEnvironment.class);
        shakeWaker = createMock(ShakeWaker.class);
        locationManager = createMock(LocationManager.class);
        locationManagerProvider = createMock(Provider.class);
        satelliteCountListener = createMock(SatelliteCountListener.class);
    }

    @Test
    public void testGeoBeagleSensorsRegisterSensors() {
        expect(locationManagerProvider.get()).andReturn(locationManager);
        expect(locationManager.addGpsStatusListener(satelliteCountListener)).andReturn(true);
        radarView.handleUnknownLocation();
        expect(sharedPreferences.getBoolean("imperial", false)).andReturn(true);
        radarView.setUseImperial(true);
        expect(
                sensorManager.registerListener(radarView, SensorManager.SENSOR_ORIENTATION,
                        SensorManager.SENSOR_DELAY_UI)).andReturn(true);
        expect(
                sensorManager.registerListener(compassListener, SensorManager.SENSOR_ORIENTATION,
                        SensorManager.SENSOR_DELAY_UI)).andReturn(true);
        shakeWaker.register();
        replayAll();

        new GeoBeagleSensors(sensorManager, radarView, sharedPreferences, compassListener,
                shakeWaker, locationManagerProvider, satelliteCountListener).registerSensors();
        verifyAll();
    }

    @Test
    public void testGeoBeagleSensorsUnregister() {
        sensorManager.unregisterListener(radarView);
        sensorManager.unregisterListener(compassListener);
        shakeWaker.unregister();
        expect(locationManagerProvider.get()).andReturn(locationManager);
        locationManager.removeGpsStatusListener(satelliteCountListener);
        replayAll();

        new GeoBeagleSensors(sensorManager, radarView, sharedPreferences, compassListener,
                shakeWaker, locationManagerProvider, satelliteCountListener).unregisterSensors();
        verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void onResume() throws Exception {
        AppLifecycleManager appLifecycleManager = createMock(AppLifecycleManager.class);
        CompassActivity geobeagle = createMock(CompassActivity.class);
        IncomingIntentHandler incomingIntentHandler = createMock(IncomingIntentHandler.class);
        Intent intent = createMock(Intent.class);
        Geocache geocache = createMock(Geocache.class);
        GeocacheViewer geocacheViewer = createMock(GeocacheViewer.class);
        WebPageMenuEnabler webPageButtonEnabler = createMock(WebPageMenuEnabler.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);
        mockStatic(DatabaseDI.class);
        Provider<DbFrontend> dbFrontEndProvider = createMock(Provider.class);
        CheckDetailsButton checkDetailsButton = createMock(CheckDetailsButton.class);

        appLifecycleManager.onResume();
        expect(geobeagle.getIntent()).andReturn(intent);
        expect(incomingIntentHandler.maybeGetGeocacheFromIntent(intent, geocache, locationSaver))
                .andReturn(geocache);
        geocacheViewer.set(geocache);
        checkDetailsButton.check(geocache);
        geoBeagleSensors.registerSensors();
        replayAll();

        CompassActivityDelegate compassActivityDelegate = new CompassActivityDelegate(null, appLifecycleManager,
                geobeagle, null, geocacheViewer, incomingIntentHandler, null, null,
                dbFrontEndProvider, checkDetailsButton, webPageButtonEnabler, null, locationSaver,
                geoBeagleSensors);
        compassActivityDelegate.setGeocache(geocache);
        compassActivityDelegate.onResume();
        verifyAll();
    }

    @Test
    public void testIncomingIntentHandler_Maps() {
        Intent intent = createMock(Intent.class);
        GeocacheFromIntentFactory geocacheFromIntentFactory = createMock(GeocacheFromIntentFactory.class);

        expect(intent.getAction()).andReturn(Intent.ACTION_VIEW);
        expect(intent.getType()).andReturn(null);
        expect(geocacheFromIntentFactory.viewCacheFromMapsIntent(intent, null, null)).andReturn(
                null);

        replayAll();
        assertNull(new IncomingIntentHandler(null, geocacheFromIntentFactory)
                .maybeGetGeocacheFromIntent(intent, null, null));
        verifyAll();
    }

    @Test
    public void testIncomingIntentHandler_NullCache() {
        Intent intent = createMock(Intent.class);
        Geocache geocache = createMock(Geocache.class);
        GeocacheFactory geocacheFactory = createMock(GeocacheFactory.class);

        expect(intent.getAction()).andReturn(GeocacheListController.SELECT_CACHE);
        expect(intent.getParcelableExtra("geocache")).andReturn(null);
        expect(
                geocacheFactory.create("", "", 0, 0, Source.MY_LOCATION, "", CacheType.NULL, 0, 0,
                        0, true, false)).andReturn(geocache);

        replayAll();
        assertEquals(geocache,
                new IncomingIntentHandler(geocacheFactory, null).maybeGetGeocacheFromIntent(intent,
                        null, null));
        verifyAll();
    }

    @Test
    public void testIncomingIntentHandler_NullIntent() {
        Geocache geocache = createMock(Geocache.class);

        assertEquals(geocache, new IncomingIntentHandler(null, null).maybeGetGeocacheFromIntent(
                null, geocache, null));
    }

    @Test
    public void testIncomingIntentHandler_Select() {
        Intent intent = createMock(Intent.class);
        Geocache geocache = createMock(Geocache.class);

        expect(intent.getAction()).andReturn(GeocacheListController.SELECT_CACHE);
        expect(intent.getParcelableExtra("geocache")).andReturn(geocache);

        replayAll();
        assertEquals(geocache, new IncomingIntentHandler(null, null).maybeGetGeocacheFromIntent(
                intent, geocache, null));
        verifyAll();
    }

    @Test
    public void testLogFindClickListener() {
        CompassActivity compassActivity = createMock(CompassActivity.class);
        View view = createMock(View.class);

        compassActivity.showDialog(17);

        replayAll();
        new LogFindClickListener(compassActivity, 17).onClick(view);
        verifyAll();
    }

    @Test
    public void testOnKeyDown_Camera() throws Exception {
        Intent intent = createMock(Intent.class);
        CompassActivity compassActivity = createMock(CompassActivity.class);
        KeyEvent keyEvent = createMock(KeyEvent.class);
        Geocache geocache = createMock(Geocache.class);
        File file = createMock(File.class);
        Uri uri = createMock(Uri.class);

        mockStatic(Uri.class);
        mockStatic(Log.class);
        mockStatic(DateFormat.class);
        mockStatic(System.class);

        expect(Log.d((String)anyObject(), (String)anyObject())).andReturn(0);
        expect(System.currentTimeMillis()).andReturn(1000L);
        expect(DateFormat.format("_yyyy-MM-dd_kk.mm.ss.jpg", 1000L)).andReturn(
                "_2008-09-12_12.32.12.jpg");
        expect(geoBeagleEnvironment.getExternalStorageDir()).andReturn("/sdcard");
        expect(geocache.getId()).andReturn("GCABC");
        expectNew(Intent.class, MediaStore.ACTION_IMAGE_CAPTURE).andReturn(intent);
        expect(intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)).andReturn(intent);
        expect(keyEvent.getRepeatCount()).andReturn(0);
        expectNew(File.class, "/sdcard/GeoBeagle_GCABC_2008-09-12_12.32.12.jpg").andReturn(file);
        expect(Uri.fromFile(file)).andReturn(uri);
        compassActivity.startActivityForResult(intent,
                CompassActivityDelegate.ACTIVITY_REQUEST_TAKE_PICTURE);

        replayAll();
        final CompassActivityDelegate compassActivityDelegate = new CompassActivityDelegate(null, null,
                compassActivity, null, null, null, null, null, null, null, null,
                geoBeagleEnvironment, null, null);
        compassActivityDelegate.setGeocache(geocache);
        assertTrue(compassActivityDelegate.onKeyDown(KeyEvent.KEYCODE_CAMERA, keyEvent));
        verifyAll();
    }

    @Test
    public void testOnKeyDown_NotCamera() throws Exception {
        KeyEvent keyEvent = createMock(KeyEvent.class);

        replayAll();
        final CompassActivityDelegate compassActivityDelegate = new CompassActivityDelegate(null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        assertFalse(compassActivityDelegate.onKeyDown(KeyEvent.KEYCODE_A, keyEvent));
        verifyAll();
    }

    @Test
    public void testOnOptionsItemSelected() {
        GeoBeagleActivityMenuActions menuActions = createMock(GeoBeagleActivityMenuActions.class);
        MenuItem item = createMock(MenuItem.class);

        expect(item.getItemId()).andReturn(12);
        expect(menuActions.act(12)).andReturn(true);
        replayAll();

        assertTrue(new CompassActivityDelegate(null, null, null, null, null, null, menuActions, null,
                null, null, null, geoBeagleEnvironment, null, null).onOptionsItemSelected(item));
        verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnPause() {
        AppLifecycleManager appLifecycleManager = createMock(AppLifecycleManager.class);
        ActivitySaver activitySaver = createMock(ActivitySaver.class);
        Geocache geocache = createMock(Geocache.class);
        DbFrontend dbFrontend = createMock(DbFrontend.class);
        Provider<DbFrontend> dbFrontEndProvider = createMock(Provider.class);

        geoBeagleSensors.unregisterSensors();
        appLifecycleManager.onPause();
        activitySaver.save(ActivityType.VIEW_CACHE, geocache);
        expect(dbFrontEndProvider.get()).andReturn(dbFrontend);
        dbFrontend.closeDatabase();

        replayAll();
        final CompassActivityDelegate compassActivityDelegate = new CompassActivityDelegate(activitySaver,
                appLifecycleManager, null, null, null, null, null, null, dbFrontEndProvider, null,
                null, null, null, geoBeagleSensors);
        compassActivityDelegate.setGeocache(geocache);
        compassActivityDelegate.onPause();
        verifyAll();
    }

    @Test
    public void testOnRestoreInstanceState() throws Exception {
        GeocacheFromParcelFactory geocacheFromParcelFactory = createMock(GeocacheFromParcelFactory.class);
        Bundle bundle = createMock(Bundle.class);
        Geocache geocache = createMock(Geocache.class);

        expect(geocacheFromParcelFactory.createFromBundle(bundle)).andReturn(geocache);

        replayAll();
        new CompassActivityDelegate(null, null, null, null, null, null, null, geocacheFromParcelFactory,
                null, null, null, null, null, null).onRestoreInstanceState(bundle);
        verifyAll();
    }

    @Test
    public void testSaveInstanceState() {
        Bundle bundle = createMock(Bundle.class);
        Geocache geocache = createMock(Geocache.class);

        geocache.saveToBundle(bundle);
        replayAll();
        final CompassActivityDelegate compassActivityDelegate = new CompassActivityDelegate(null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        compassActivityDelegate.setGeocache(geocache);
        compassActivityDelegate.onSaveInstanceState(bundle);
        verifyAll();
    }

    @Test
    public void testSetGet() {
        Geocache geocache = createMock(Geocache.class);

        final CompassActivityDelegate compassActivityDelegate = new CompassActivityDelegate(null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        compassActivityDelegate.setGeocache(geocache);
        assertEquals(geocache, compassActivityDelegate.getGeocache());
    }
}
