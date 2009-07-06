
package com.google.code.geobeagle.activity.searchonline;

import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.ActivityRestorer;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class SearchOnlineActivityDelegate {

    private final ActivityRestorer mActivityRestorer;
    private final ActivitySaver mActivitySaver;
    private final CombinedLocationListener mCombinedLocationListener;
    private final CombinedLocationManager mCombinedLocationManager;
    private final CompassListener mCompassListener;
    private final DistanceFormatterManager mDistanceFormatterManager;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SensorManager mSensorManager;
    private final WebView mWebView;

    public SearchOnlineActivityDelegate(WebView webView, SensorManager sensorManager,
            CompassListener compassListener, CombinedLocationManager combinedLocationManager,
            CombinedLocationListener combinedLocationListener,
            LocationControlBuffered locationControlBuffered,
            DistanceFormatterManager distanceFormatterManager, ActivitySaver activitySaver,
            ActivityRestorer activityRestorer) {
        mSensorManager = sensorManager;
        mCompassListener = compassListener;
        mCombinedLocationListener = combinedLocationListener;
        mCombinedLocationManager = combinedLocationManager;
        mLocationControlBuffered = locationControlBuffered;
        mWebView = webView;
        mDistanceFormatterManager = distanceFormatterManager;
        mActivityRestorer = activityRestorer;
        mActivitySaver = activitySaver;
    }

    public void configureWebView(JsInterface jsInterface) {
        mWebView.loadUrl("file:///android_asset/search.html");
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        mWebView.setBackgroundColor(Color.BLACK);
        mWebView.addJavascriptInterface(jsInterface, "gb");
    }

    public void onPause() {
        mCombinedLocationManager.removeUpdates(mLocationControlBuffered);
        mCombinedLocationManager.removeUpdates(mCombinedLocationListener);
        mSensorManager.unregisterListener(mCompassListener);
        mActivitySaver.save(ActivityType.SEARCH_ONLINE);
    }

    public void onResume(Intent intent) {
        mCombinedLocationManager.requestLocationUpdates(1000, 0, mLocationControlBuffered);
        mCombinedLocationManager.requestLocationUpdates(1000, 0, mCombinedLocationListener);
        mSensorManager.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        mDistanceFormatterManager.setFormatter();
    }
}
