
package com.google.code.geobeagle.activity.searchonline;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;

import android.graphics.Color;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class SearchOnlineActivityDelegate {

    private final ActivitySaver mActivitySaver;
    private final DistanceFormatterManager mDistanceFormatterManager;
    private final LocationControlBuffered mLocationControlBuffered;
    private final WebView mWebView;

    public SearchOnlineActivityDelegate(WebView webView,
            LocationControlBuffered locationControlBuffered,
            DistanceFormatterManager distanceFormatterManager, ActivitySaver activitySaver) {
        mLocationControlBuffered = locationControlBuffered;
        mWebView = webView;
        mDistanceFormatterManager = distanceFormatterManager;
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
        mLocationControlBuffered.onPause();
        mActivitySaver.save(ActivityType.SEARCH_ONLINE);
    }

    public void onResume() {
        mLocationControlBuffered.onResume();
        mDistanceFormatterManager.setFormatter();
    }
}
