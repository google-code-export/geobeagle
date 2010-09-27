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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.main.view.CheckDetailsButton;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.WebPageMenuEnabler;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class GeoBeagleDelegate {
    static class GeoBeagleSensors {
        private final SensorManager sensorManager;
        private final RadarView radarView;
        private final SharedPreferences sharedPreferences;
        private final CompassListener compassListener;

        @Inject
        GeoBeagleSensors(SensorManager sensorManager,
                RadarView radarView,
                SharedPreferences sharedPreferences,
                CompassListener compassListener) {
            this.sensorManager = sensorManager;
            this.radarView = radarView;
            this.sharedPreferences = sharedPreferences;
            this.compassListener = compassListener;
        }

        public void registerSensors() {
            radarView.handleUnknownLocation();
            radarView.setUseImperial(sharedPreferences.getBoolean("imperial", false));
            sensorManager.registerListener(radarView, SensorManager.SENSOR_ORIENTATION,
                    SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(compassListener, SensorManager.SENSOR_ORIENTATION,
                    SensorManager.SENSOR_DELAY_UI);
        }

        public void unregisterSensors() {
            sensorManager.unregisterListener(radarView);
            sensorManager.unregisterListener(compassListener);
        }
    }

    static int ACTIVITY_REQUEST_TAKE_PICTURE = 1;
    private final ActivitySaver mActivitySaver;
    private final AppLifecycleManager mAppLifecycleManager;
    private final Provider<DbFrontend> mDbFrontendProvider;
    private Geocache mGeocache;
    private final GeocacheFactory mGeocacheFactory;
    private final GeocacheFromParcelFactory mGeocacheFromParcelFactory;
    private final GeocacheViewer mGeocacheViewer;
    private final IncomingIntentHandler mIncomingIntentHandler;
    private final GeoBeagleActivityMenuActions mMenuActions;
    private final GeoBeagle mParent;
    private final CheckDetailsButton mCheckDetailsButton;
    private final GeoBeagleEnvironment mGeoBeagleEnvironment;
    private final WebPageMenuEnabler mWebPageMenuEnabler;
    private final LocationSaver mLocationSaver;
    private final GeoBeagleSensors mGeoBeagleSensors;

    public GeoBeagleDelegate(ActivitySaver activitySaver,
            AppLifecycleManager appLifecycleManager,
            Activity parent,
            GeocacheFactory geocacheFactory,
            GeocacheViewer geocacheViewer,
            IncomingIntentHandler incomingIntentHandler,
            GeoBeagleActivityMenuActions menuActions,
            GeocacheFromParcelFactory geocacheFromParcelFactory,
            Provider<DbFrontend> dbFrontendProvider,
            CheckDetailsButton checkDetailsButton,
            WebPageMenuEnabler webPageMenuEnabler,
            GeoBeagleEnvironment geoBeagleEnvironment,
            LocationSaver locationSaver,
            GeoBeagleSensors geoBeagleSensors) {
        mParent = (GeoBeagle)parent;
        mActivitySaver = activitySaver;
        mAppLifecycleManager = appLifecycleManager;
        mMenuActions = menuActions;
        mGeocacheViewer = geocacheViewer;
        mGeocacheFactory = geocacheFactory;
        mIncomingIntentHandler = incomingIntentHandler;
        mDbFrontendProvider = dbFrontendProvider;
        mGeocacheFromParcelFactory = geocacheFromParcelFactory;
        mGeoBeagleEnvironment = geoBeagleEnvironment;
        mCheckDetailsButton = checkDetailsButton;
        mWebPageMenuEnabler = webPageMenuEnabler;
        mLocationSaver = locationSaver;
        mGeoBeagleSensors = geoBeagleSensors;
    }

    @Inject
    public GeoBeagleDelegate(Injector injector) {
        mParent = (GeoBeagle)injector.getInstance(Activity.class);
        mActivitySaver = injector.getInstance(ActivitySaver.class);
        mAppLifecycleManager = injector.getInstance(AppLifecycleManager.class);
        mMenuActions = injector.getInstance(GeoBeagleActivityMenuActions.class);
        mGeocacheViewer = injector.getInstance(GeocacheViewer.class);
        mGeocacheFactory = injector.getInstance(GeocacheFactory.class);
        mIncomingIntentHandler = injector.getInstance(IncomingIntentHandler.class);
        mDbFrontendProvider = injector.getProvider(DbFrontend.class);
        mGeocacheFromParcelFactory = injector.getInstance(GeocacheFromParcelFactory.class);
        mGeoBeagleEnvironment = injector.getInstance(GeoBeagleEnvironment.class);
        mCheckDetailsButton = injector.getInstance(CheckDetailsButton.class);
        mWebPageMenuEnabler = injector.getInstance(WebPageMenuEnabler.class);
        mLocationSaver = injector.getInstance(LocationSaver.class);
        mGeoBeagleSensors = injector.getInstance(GeoBeagleSensors.class);
    }

    public Geocache getGeocache() {
        return mGeocache;
    }

    private void onCameraStart() {
        String filename = mGeoBeagleEnvironment.getExternalStorageDir() + "/GeoBeagle_"
                + mGeocache.getId()
                + DateFormat.format("_yyyy-MM-dd_kk.mm.ss.jpg", System.currentTimeMillis());
        Log.d("GeoBeagle", "capturing image to " + filename);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filename)));
        mParent.startActivityForResult(intent, GeoBeagleDelegate.ACTIVITY_REQUEST_TAKE_PICTURE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuActions.onCreateOptionsMenu(menu);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA && event.getRepeatCount() == 0) {
            onCameraStart();
            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

    public void onPause() {
        mAppLifecycleManager.onPause();
        mGeoBeagleSensors.unregisterSensors();
        mActivitySaver.save(ActivityType.VIEW_CACHE, mGeocache);
        mDbFrontendProvider.get().closeDatabase();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mGeocache = mGeocacheFromParcelFactory.createFromBundle(savedInstanceState);
        // Is this really needed???
        // mWritableDatabase =
        // mGeoBeagleSqliteOpenHelper.getWritableSqliteWrapper();
    }

    public void onResume() {
        mAppLifecycleManager.onResume();
        mGeoBeagleSensors.registerSensors();
        mGeocache = mIncomingIntentHandler.maybeGetGeocacheFromIntent(mParent.getIntent(),
                mGeocache, mLocationSaver);

        // Possible fix for issue 53.
        if (mGeocache == null) {
            mGeocache = mGeocacheFactory.create("", "", 0, 0, Source.MY_LOCATION, "",
                    CacheType.NULL, 0, 0, 0, true, false);
        }
        mGeocacheViewer.set(mGeocache);
        mCheckDetailsButton.check(mGeocache);
    }

    public void onSaveInstanceState(Bundle outState) {
        // apparently there are cases where getGeocache returns null, causing
        // crashes with 0.7.7/0.7.8.
        if (mGeocache != null)
            mGeocache.saveToBundle(outState);
    }

    public void setGeocache(Geocache geocache) {
        mGeocache = geocache;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.string.web_page);
        item.setVisible(mWebPageMenuEnabler.shouldEnable(getGeocache()));
        return true;
    }
}
