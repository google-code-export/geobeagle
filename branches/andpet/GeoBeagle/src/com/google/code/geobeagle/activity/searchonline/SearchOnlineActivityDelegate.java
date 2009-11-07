
package com.google.code.geobeagle.activity.searchonline;

import com.google.code.geobeagle.LocationAndDirection;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class SearchOnlineActivityDelegate {

    private final ActivitySaver mActivitySaver;
    private final DistanceFormatterManager mDistanceFormatterManager;
    private final LocationAndDirection mLocationAndDirection;
    private final WebView mWebView;
    private final SharedPreferences mSharedPreferences;

    public SearchOnlineActivityDelegate(WebView webView,
            LocationAndDirection locationAndDirection,
            DistanceFormatterManager distanceFormatterManager, 
            ActivitySaver activitySaver,
            SharedPreferences sharedPreferences) {
        mLocationAndDirection = locationAndDirection;
        mWebView = webView;
        mDistanceFormatterManager = distanceFormatterManager;
        mActivitySaver = activitySaver;
        mSharedPreferences = sharedPreferences;
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
        mLocationAndDirection.onPause();
        mActivitySaver.save(ActivityType.SEARCH_ONLINE);
    }

    public void onResume() {
        mLocationAndDirection.onResume(mSharedPreferences);
        mDistanceFormatterManager.setFormatter();
    }
}
