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

package com.google.code.geobeagle.activity.searchonline;

import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivityRestorer;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityDI.ActivityTypeFactory;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManagerDi;
import com.google.code.geobeagle.activity.main.GeocacheFromPreferencesFactory;
import com.google.code.geobeagle.activity.searchonline.JsInterface.JsInterfaceHelperFactory;
import com.google.code.geobeagle.activity.searchonline.JsInterface.JsInterfaceHelper;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.xmlimport.GpxImporterDI;
import com.google.inject.Inject;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import java.util.ArrayList;

public class SearchOnlineActivity extends GuiceActivity {

    private SearchOnlineActivityDelegate mSearchOnlineActivityDelegate;
    
    @InjectView(R.id.gps_widget_view)
    private InflatedGpsStatusWidget gpsStatusWidget;

    @Inject
    LocationManager locationManager;

    @Inject
    GeocacheFromPreferencesFactory geocacheFromPreferencesFactory;

    @Inject
    Refresher refresher;

    @Inject
    SensorManager sensorManager;

    @Inject
    ActivityTypeFactory activityTypeFactory;

    JsInterfaceHelper jsInterfaceHelper;

    @Inject
    GpxImporterDI.ToastFactory toastFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("GeoBeagle", "SearchOnlineActivity onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);
        
        final LocationControlBuffered mLocationControlBuffered = LocationControlDi
                .create(locationManager);
        final ArrayList<LocationListener> locationListeners = new ArrayList<LocationListener>(3);
        final CombinedLocationManager mCombinedLocationManager = new CombinedLocationManager(
                locationManager, locationListeners);

        final DistanceFormatterManager distanceFormatterManager = DistanceFormatterManagerDi
                .create(this);
        final GpsWidgetAndUpdater gpsWidgetAndUpdater = new GpsWidgetAndUpdater(this,
                gpsStatusWidget, mLocationControlBuffered, mCombinedLocationManager,
                distanceFormatterManager.getFormatter());
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = gpsWidgetAndUpdater
                .getGpsStatusWidgetDelegate();
        gpsWidgetAndUpdater.getUpdateGpsWidgetRunnable().run();
        gpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);
        gpsStatusWidget.setBackgroundColor(Color.BLACK);

        final CompassListener mCompassListener = new CompassListener(refresher,
                mLocationControlBuffered, 720);
        final CombinedLocationListener mCombinedLocationListener = new CombinedLocationListener(
                mLocationControlBuffered, gpsStatusWidgetDelegate);
        distanceFormatterManager.addHasDistanceFormatter(gpsStatusWidgetDelegate);
        final ActivitySaver activitySaver = ActivityDI.createActivitySaver(this);

        final ActivityRestorer activityRestorer = new ActivityRestorer(this,
                geocacheFromPreferencesFactory, activityTypeFactory, getSharedPreferences(
                        "GeoBeagle", Context.MODE_PRIVATE));

        mSearchOnlineActivityDelegate = new SearchOnlineActivityDelegate(
                ((WebView)findViewById(R.id.help_contents)), sensorManager, mCompassListener,
                mCombinedLocationManager, mCombinedLocationListener, mLocationControlBuffered,
                distanceFormatterManager, activitySaver);

        jsInterfaceHelper = this.getInjector().getInstance(
                JsInterfaceHelperFactory.class).create(this);

        final JsInterface jsInterface = new JsInterface(
                mLocationControlBuffered, jsInterfaceHelper,
                toastFactory, this);

        mSearchOnlineActivityDelegate.configureWebView(jsInterface);
        activityRestorer.restore(getIntent().getFlags());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_online_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        startActivity(new Intent(this, CacheListActivity.class));
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeoBeagle", "SearchOnlineActivity onResume");

        mSearchOnlineActivityDelegate.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("GeoBeagle", "SearchOnlineActivity onPause");

        mSearchOnlineActivityDelegate.onPause();
    }
}
