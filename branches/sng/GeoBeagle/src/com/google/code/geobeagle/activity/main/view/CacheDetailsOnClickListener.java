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

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader;

import android.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

public class CacheDetailsOnClickListener implements View.OnClickListener {

    private final Builder mAlertDialogBuilder;
    private final CacheDetailsLoader mCacheDetailsLoader;
    private final LayoutInflater mEnv;
    private final GeoBeagle mGeoBeagle;

    public CacheDetailsOnClickListener(GeoBeagle geoBeagle, Builder alertDialogBuilder,
            LayoutInflater env, CacheDetailsLoader cacheDetailsLoader) {
        mAlertDialogBuilder = alertDialogBuilder;
        mEnv = env;
        mCacheDetailsLoader = cacheDetailsLoader;
        mGeoBeagle = geoBeagle;
    }

    public void onClick(View v) {
        View detailsView = mEnv.inflate(R.layout.cache_details, null);

        CharSequence id = mGeoBeagle.getGeocache().getId();
        mAlertDialogBuilder.setTitle(id);
        mAlertDialogBuilder.setView(detailsView);

        WebView webView = (WebView)detailsView.findViewById(R.id.webview);
        webView.loadDataWithBaseURL(null, mCacheDetailsLoader.load(id), "text/html", "utf-8",
                "about:blank");

        mAlertDialogBuilder.create().show();
    }
}
