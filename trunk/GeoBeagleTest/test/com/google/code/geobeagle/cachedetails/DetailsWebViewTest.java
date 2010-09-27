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

package com.google.code.geobeagle.cachedetails;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.cacheloader.CacheDetailsLoader;
import com.google.code.geobeagle.cacheloader.CacheLoaderException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Intent;
import android.webkit.WebSettings;
import android.webkit.WebView;

@RunWith(PowerMockRunner.class)
public class DetailsWebViewTest {

    @Test
    public void testDetailsWebView() throws CacheLoaderException {
        CacheDetailsLoader cacheDetailsLoader = createMock(CacheDetailsLoader.class);
        WebView webView = createMock(WebView.class);
        Intent intent = createMock(Intent.class);
        WebSettings settings = createMock(WebSettings.class);

        expect(cacheDetailsLoader.load("bcaching.com", "GC123")).andReturn("<html>details</html>");
        expect(webView.getSettings()).andReturn(settings);
        settings.setJavaScriptEnabled(true);
        expect(intent.getStringExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_SOURCE)).andReturn(
                "bcaching.com");
        expect(intent.getStringExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_ID)).andReturn("GC123");
        expect(intent.getStringExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_NAME)).andReturn(
                "An easy cache");
        webView.loadDataWithBaseURL(null, "<html>details</html>", "text/html", "utf-8", null);

        replayAll();
        DetailsWebView detailsWebView = new DetailsWebView(cacheDetailsLoader, null);
        detailsWebView.loadDetails(webView, intent);
        verifyAll();
    }
}
