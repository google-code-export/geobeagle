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

package com.google.code.geobeagle.activity.details;

import com.google.code.geobeagle.cacheloader.CacheLoader;
import com.google.code.geobeagle.cacheloader.CacheLoaderException;
import com.google.code.geobeagle.cacheloader.CacheLoaderFactory;
import com.google.code.geobeagle.xmlimport.CacheXmlTagsToDetails;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.content.Intent;
import android.content.res.Resources;
import android.webkit.WebView;

class DetailsWebView {
    private final CacheLoader cacheLoader;
    private final Resources resources;

    DetailsWebView(CacheLoader cacheLoader, Resources resources) {
        this.cacheLoader = cacheLoader;
        this.resources = resources;
    }

    @Inject
    DetailsWebView(Injector injector) {
        CacheLoaderFactory cacheLoaderFactory = injector.getInstance(CacheLoaderFactory.class);
        CacheXmlTagsToDetails cacheXmlTagsToDetails = injector.getInstance(CacheXmlTagsToDetails.class);
        cacheLoader = cacheLoaderFactory.create(cacheXmlTagsToDetails);
        resources = injector.getInstance(Resources.class);
    }

    String loadDetails(WebView webView, Intent intent) {
        webView.getSettings().setJavaScriptEnabled(true);
        String sourceName = intent.getStringExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_SOURCE);
        String id = intent.getStringExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_ID);
        String details;
        try {
            details = cacheLoader.load(sourceName, id);
        } catch (CacheLoaderException e) {
            details = resources.getString(e.getError(), e.getArgs());
        }
        webView.loadDataWithBaseURL(null, details, "text/html", "utf-8", null);
        return id + ": " + intent.getStringExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_NAME);
    }
}
