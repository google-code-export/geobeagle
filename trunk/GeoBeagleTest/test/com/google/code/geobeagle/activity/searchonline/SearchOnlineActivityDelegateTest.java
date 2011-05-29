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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

@RunWith(PowerMockRunner.class)
public class SearchOnlineActivityDelegateTest extends GeoBeagleTest {
    @Test
    public void configureWebVew() {
        WebView webView = PowerMock.createMock(WebView.class);
        JsInterface jsInterface = PowerMock.createMock(JsInterface.class);
        WebSettings webSettings = PowerMock.createMock(WebSettings.class);
        Activity activity = PowerMock.createMock(Activity.class);

        EasyMock.expect(activity.findViewById(R.id.help_contents)).andReturn(webView);
        webView.loadUrl("file:///android_asset/search.html");
        EasyMock.expect(webView.getSettings()).andReturn(webSettings);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webView.setBackgroundColor(Color.BLACK);
        webView.addJavascriptInterface(jsInterface, "gb");

        PowerMock.replayAll();
        new SearchOnlineActivityDelegate(activity, null, null, null, null, null, null, null,
                jsInterface).configureWebView();
        PowerMock.verifyAll();

    }

    @Test
    @SuppressWarnings("unchecked")
    public void onPause() {
        SensorManager sensorManager = PowerMock.createMock(SensorManager.class);
        CompassListener compassListener = PowerMock.createMock(CompassListener.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        CombinedLocationListener combinedLocationListener = PowerMock
                .createMock(CombinedLocationListener.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        ActivitySaver activitySaver = PowerMock.createMock(ActivitySaver.class);
        ActivityVisible activityVisible = PowerMock.createMock(ActivityVisible.class);
        Activity activity = PowerMock.createMock(Activity.class);
        Provider<CompassListener> compassListenerProvider = PowerMock.createMock(Provider.class);

        EasyMock.expect(activity.findViewById(R.id.help_contents)).andReturn(null);
        combinedLocationManager.removeUpdates();
        EasyMock.expect(compassListenerProvider.get()).andReturn(compassListener);
        sensorManager.unregisterListener(compassListener);
        activitySaver.save(ActivityType.SEARCH_ONLINE);
        activityVisible.setVisible(false);

        PowerMock.replayAll();
        new SearchOnlineActivityDelegate(activity, sensorManager, compassListenerProvider,
                combinedLocationManager,
                combinedLocationListener, locationControlBuffered, activitySaver, activityVisible,
                null).onPause();
        PowerMock.verifyAll();
    }
}
