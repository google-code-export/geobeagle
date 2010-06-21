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

package com.google.code.geobeagle;

import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.NullAttributesPainter;
import com.google.code.geobeagle.activity.cachelist.presenter.GeoBeaglePackageAnnotations.DifficultyAndTerrainPainterAnnotation;
import com.google.code.geobeagle.activity.cachelist.presenter.GeoBeaglePackageAnnotations.NullAttributesPainterAnnotation;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.webkit.WebSettings;
import android.webkit.WebView;

// TODO rename to GeoBeagleModule
public class GeoBeaglePackageModule extends AbstractAndroidModule {
    @Override
    protected void configure() {
    }

    @Provides
    @NullAttributesPainterAnnotation
    public IconRenderer providesNullAttributesIconRenderer(Resources resources,
            NullAttributesPainter nullAttributesPainter) {
        return new IconRenderer(resources, nullAttributesPainter);
    }

    @Provides
    @DifficultyAndTerrainPainterAnnotation
    public IconRenderer providesDifficultyAndTerrainIconRenderer(Resources resources,
            DifficultyAndTerrainPainter difficultyAndTerrainPainter) {
        return new IconRenderer(resources, difficultyAndTerrainPainter);
    }

    @Provides
    public WebView providesCacheListWebView(Activity activity) {
        WebView webView = (WebView)activity.findViewById(android.R.id.empty);
        webView.loadUrl("file:///android_asset/no_caches.html");
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setSupportZoom(false);
        webView.setBackgroundColor(Color.BLACK);
        return webView;
    }
}
