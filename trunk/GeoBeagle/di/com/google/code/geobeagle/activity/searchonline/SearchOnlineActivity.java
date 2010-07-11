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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivityRestorer;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.searchonline.SearchOnlineActivityDelegate.SearchOnlineActivityDelegateFactory;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.inject.Inject;
import com.google.inject.Injector;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class SearchOnlineActivity extends GuiceActivity {

    @Inject
    private ActivityRestorer mActivityRestorer;

    private CombinedLocationListener mCombinedLocationListener;

    private InflatedGpsStatusWidget mInflatedGpsStatusWidget;

    @InjectView(R.id.help_contents)
    private WebView mHelpContentsView;

    @Inject
    private JsInterface mJsInterface;

    private SearchOnlineActivityDelegate mSearchOnlineActivityDelegate;

    private UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;

    public ActivityRestorer getActivityRestorer() {
        return mActivityRestorer;
    }

    InflatedGpsStatusWidget getGpsStatusWidget() {
        return mInflatedGpsStatusWidget;
    }

    WebView getHelpContentsView() {
        return mHelpContentsView;
    }

    JsInterface getJsInterface() {
        return mJsInterface;
    }

    SearchOnlineActivityDelegate getMSearchOnlineActivityDelegate() {
        return mSearchOnlineActivityDelegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Log.d("GeoBeagle", "SearchOnlineActivity onCreate");
        
        Injector injector = this.getInjector();
        mInflatedGpsStatusWidget = injector.getInstance(InflatedGpsStatusWidget.class);
        
        GpsStatusWidgetDelegate gpsStatusWidgetDelegate = injector
                .getInstance(GpsStatusWidgetDelegate.class);
        mUpdateGpsWidgetRunnable = injector.getInstance(UpdateGpsWidgetRunnable.class);
        mInflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);
        mInflatedGpsStatusWidget.setBackgroundColor(Color.BLACK);

        mCombinedLocationListener = injector.getInstance(CombinedLocationListener.class);
        
        mSearchOnlineActivityDelegate = injector.getInstance(
                SearchOnlineActivityDelegateFactory.class).create(
                mHelpContentsView, mCombinedLocationListener);

        mSearchOnlineActivityDelegate.configureWebView(mJsInterface);
        mActivityRestorer.restore(getIntent().getFlags());
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
    protected void onPause() {
        super.onPause();
        Log.d("GeoBeagle", "SearchOnlineActivity onPause");
        mSearchOnlineActivityDelegate.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeoBeagle", "SearchOnlineActivity onResume");
        mSearchOnlineActivityDelegate.onResume();
        mUpdateGpsWidgetRunnable.run();
    }
}
