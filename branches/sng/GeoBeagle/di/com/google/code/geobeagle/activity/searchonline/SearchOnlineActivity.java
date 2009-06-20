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
import com.google.code.geobeagle.activity.searchonline.JsInterface.JsInterfaceHelper;
import com.google.code.geobeagle.formatting.DistanceFormatterMetric;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;

public class SearchOnlineActivity extends Activity {

    private SearchOnlineActivityDelegate mSearchOnlineActivityDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);
        final LocationManager locationManager = (LocationManager)this
                .getSystemService(Context.LOCATION_SERVICE);
        LocationControlBuffered mLocationControlBuffered = LocationControlDi
                .create(locationManager);
        final CombinedLocationManager mCombinedLocationManager = new CombinedLocationManager(
                locationManager);
        final DistanceFormatterMetric distanceFormatterMetric = new DistanceFormatterMetric();
        final View gpsWidget = this.findViewById(R.id.gps_widget);
        final GpsStatusWidget gpsStatusWidget = GpsStatusWidget.CreateStatusWidget(this,
                mLocationControlBuffered, mCombinedLocationManager, distanceFormatterMetric,
                gpsWidget);
        gpsStatusWidget.setBackgroundColor(Color.BLACK);

        final Refresher refresher = new NullRefresher();
        final CompassListener mCompassListener = new CompassListener(refresher,
                mLocationControlBuffered, 720);
        final SensorManager mSensorManager = (SensorManager)this
                .getSystemService(Context.SENSOR_SERVICE);
        final CombinedLocationListener mCombinedLocationListener = new CombinedLocationListener(
                mLocationControlBuffered, gpsStatusWidget);

        mSearchOnlineActivityDelegate = new SearchOnlineActivityDelegate(
                ((WebView)findViewById(R.id.help_contents)), mSensorManager, mCompassListener,
                mCombinedLocationManager, mCombinedLocationListener, mLocationControlBuffered);

        final JsInterfaceHelper jsInterfaceHelper = new JsInterfaceHelper(this);
        final JsInterface jsInterface = new JsInterface(mLocationControlBuffered, jsInterfaceHelper);
        mSearchOnlineActivityDelegate.configureWebView(jsInterface);
        GpsStatusWidget.CreateUpdateGpsWidgetRunnable(gpsStatusWidget, mLocationControlBuffered)
                .run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_online_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchOnlineActivityDelegate.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSearchOnlineActivityDelegate.onPause();
    }
}
