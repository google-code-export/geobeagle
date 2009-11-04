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
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;

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
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;

public class GeoBeagleDelegate {

    public static class LogFindClickListener implements OnClickListener {
        private final GeoBeagle mGeoBeagle;
        private final int mIdDialog;

        LogFindClickListener(GeoBeagle geoBeagle, int idDialog) {
            mGeoBeagle = geoBeagle;
            mIdDialog = idDialog;
        }

        public void onClick(View v) {
            mGeoBeagle.showDialog(mIdDialog);
        }
    }

    static int ACTIVITY_REQUEST_TAKE_PICTURE = 1;
    private final ActivitySaver mActivitySaver;
    private final AppLifecycleManager mAppLifecycleManager;
    private final CompassListener mCompassListener;
    private final DbFrontend mDbFrontend;
    private Geocache mGeocache;
    private final GeocacheFactory mGeocacheFactory;
    private final GeocacheFromParcelFactory mGeocacheFromParcelFactory;
    private final GeocacheViewer mGeocacheViewer;
    private final IncomingIntentHandler mIncomingIntentHandler;
    private final MenuActions mMenuActions;
    private final GeoBeagle mParent;
    private final RadarView mRadarView;
    private final SensorManager mSensorManager;
    private final SharedPreferences mSharedPreferences;
    private final WebPageAndDetailsButtonEnabler mWebPageButtonEnabler;

    public GeoBeagleDelegate(ActivitySaver activitySaver, AppLifecycleManager appLifecycleManager,
            CompassListener compassListener, GeoBeagle parent,
            GeocacheFactory geocacheFactory, GeocacheViewer geocacheViewer,
            IncomingIntentHandler incomingIntentHandler, MenuActions menuActions,
            GeocacheFromParcelFactory geocacheFromParcelFactory, DbFrontend dbFrontend,
            RadarView radarView, SensorManager sensorManager,
            SharedPreferences sharedPreferences, WebPageAndDetailsButtonEnabler webPageButtonEnabler) {
        mParent = parent;
        mActivitySaver = activitySaver;
        mAppLifecycleManager = appLifecycleManager;
        mMenuActions = menuActions;
        mSharedPreferences = sharedPreferences;
        mRadarView = radarView;
        mCompassListener = compassListener;
        mSensorManager = sensorManager;
        mGeocacheViewer = geocacheViewer;
        mWebPageButtonEnabler = webPageButtonEnabler;
        mGeocacheFactory = geocacheFactory;
        mIncomingIntentHandler = incomingIntentHandler;
        mDbFrontend = dbFrontend;
        mGeocacheFromParcelFactory = geocacheFromParcelFactory;
    }

    public Geocache getGeocache() {
        return mGeocache;
    }

    private void onCameraStart() {
        String filename = "/sdcard/GeoBeagle/" + mGeocache.getId()
                + DateFormat.format(" yyyy-MM-dd kk.mm.ss.jpg", System.currentTimeMillis());
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
        mActivitySaver.save(ActivityType.VIEW_CACHE, mGeocache);
        mSensorManager.unregisterListener(mRadarView);
        mSensorManager.unregisterListener(mCompassListener);
        mDbFrontend.closeDatabase();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mGeocache = mGeocacheFromParcelFactory.createFromBundle(savedInstanceState);
        // Is this really needed???
        // mWritableDatabase =
        // mGeoBeagleSqliteOpenHelper.getWritableSqliteWrapper();
    }

    public void onResume() {
        mRadarView.handleUnknownLocation();

        mRadarView.setUseImperial(mSharedPreferences.getBoolean("imperial", false));
        mAppLifecycleManager.onResume();
        mSensorManager.registerListener(mRadarView, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        mGeocache = mIncomingIntentHandler.maybeGetGeocacheFromIntent(mParent.getIntent(),
                mGeocache, new LocationSaver(mDbFrontend));

        // Possible fix for issue 53.
        if (mGeocache == null) {
            mGeocache = mGeocacheFactory.create("", "", 0, 0, Source.MY_LOCATION, "",
                    CacheType.NULL, 0, 0, 0);
        }
        mGeocacheViewer.set(mGeocache);
        mWebPageButtonEnabler.check();
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
}
