/**
 * 
 */

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.CacheDetailsLoader;
import com.google.code.geobeagle.mainactivity.GeoBeagle;

import android.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

public class CacheDetailsOnClickListener implements View.OnClickListener {

    private final Builder mAlertDialogBuilder;
    private final CacheDetailsLoader mCacheDetailsLoader;
    private final LayoutInflater mEnv;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeoBeagle mGeoBeagle;

    public CacheDetailsOnClickListener(GeoBeagle geoBeagle, Builder alertDialogBuilder,
            GeocacheViewer geocacheViewer, LayoutInflater env,
            CacheDetailsLoader cacheDetailsLoader, ErrorDisplayer errorDisplayer) {
        mAlertDialogBuilder = alertDialogBuilder;
        mErrorDisplayer = errorDisplayer;
        mEnv = env;
        mCacheDetailsLoader = cacheDetailsLoader;
        mGeoBeagle = geoBeagle;
    }

    public void onClick(View v) {
        try {
            View detailsView = mEnv.inflate(R.layout.cache_details, null);

            CharSequence id = mGeoBeagle.getGeocache().getId();
            mAlertDialogBuilder.setTitle(id);
            mAlertDialogBuilder.setView(detailsView);

            WebView webView = (WebView)detailsView.findViewById(R.id.webview);
            webView.loadDataWithBaseURL(null, mCacheDetailsLoader.load(id), "text/html", "utf-8",
                    "about:blank");

            mAlertDialogBuilder.create().show();
        } catch (Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}
