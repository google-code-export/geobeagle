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

import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivityRestorer;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityDI.ActivityTypeFactory;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManagerDi;
import com.google.code.geobeagle.activity.main.GeocacheFromPreferencesFactory;
import com.google.code.geobeagle.activity.searchonline.JsInterface.JsInterfaceHelper;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class SearchOnlineActivity extends Activity {

    private SearchOnlineActivityDelegate mSearchOnlineActivityDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GeoBeagle", "SearchOnlineActivity onCreate");

        setContentView(R.layout.search);
        
        final GeoFixProvider mGeoFixProvider = 
            LocationControlDi.create(this);

        final InflatedGpsStatusWidget mGpsStatusWidget = (InflatedGpsStatusWidget)this
                .findViewById(R.id.gps_widget_view);
        final DistanceFormatterManager distanceFormatterManager = DistanceFormatterManagerDi
                .create(this);
        final GpsWidgetAndUpdater gpsWidgetAndUpdater = new GpsWidgetAndUpdater(this,
                mGpsStatusWidget, mGeoFixProvider,
                distanceFormatterManager.getFormatter());
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = gpsWidgetAndUpdater
                .getGpsStatusWidgetDelegate();
        gpsWidgetAndUpdater.getUpdateGpsWidgetRunnable().run();
        mGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);
        mGpsStatusWidget.setBackgroundColor(Color.BLACK);

        distanceFormatterManager.addHasDistanceFormatter(gpsStatusWidgetDelegate);
        final ActivitySaver activitySaver = ActivityDI.createActivitySaver(this);

        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        final GeocacheFromPreferencesFactory geocacheFromPreferencesFactory = new GeocacheFromPreferencesFactory(
                geocacheFactory);
        final ActivityTypeFactory activityTypeFactory = new ActivityTypeFactory();
        final ActivityRestorer activityRestorer = new ActivityRestorer(this,
                geocacheFromPreferencesFactory, activityTypeFactory, getSharedPreferences(
                        "GeoBeagle", Context.MODE_PRIVATE));
        final SharedPreferences defaultSharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(this);
        
        mSearchOnlineActivityDelegate = new SearchOnlineActivityDelegate(
                ((WebView)findViewById(R.id.help_contents)),
                mGeoFixProvider,
                distanceFormatterManager, activitySaver,
                defaultSharedPreferences);

        final JsInterfaceHelper jsInterfaceHelper = new JsInterfaceHelper(this);
        final JsInterface jsInterface = new JsInterface(mGeoFixProvider, jsInterfaceHelper);

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
