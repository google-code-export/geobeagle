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

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

@PrepareForTest( {
        View.class, CacheDetailsOnClickListener.class
})
@RunWith(PowerMockRunner.class)
public class CacheDetailsOnClickListenerTest {

    @Test
    public void testOnClick() {
        GeoBeagle geobeagle = PowerMock.createMock(GeoBeagle.class);
        Builder builder = PowerMock.createMock(Builder.class);
        AlertDialog alertDialog = PowerMock.createMock(AlertDialog.class);
        LayoutInflater env = PowerMock.createMock(LayoutInflater.class);
        CacheDetailsLoader cacheDetailsLoader = PowerMock.createMock(CacheDetailsLoader.class);
        View detailsView = PowerMock.createMock(View.class);
        WebView webView = PowerMock.createMock(WebView.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        expect(env.inflate(R.layout.cache_details, null)).andReturn(detailsView);
        expect(geobeagle.getGeocache()).andReturn(geocache);
        expect(geocache.getId()).andReturn("GC1234");
        expect(builder.setTitle("GC1234")).andReturn(builder);
        expect(detailsView.findViewById(R.id.webview)).andReturn(webView);
        expect(builder.setView(detailsView)).andReturn(builder);
        expect(cacheDetailsLoader.load("GC1234")).andReturn("details");
        webView.loadDataWithBaseURL(null, "details", "text/html", "utf-8", "about:blank");
        expect(builder.create()).andReturn(alertDialog);
        alertDialog.show();

        PowerMock.replayAll();
        new CacheDetailsOnClickListener(geobeagle, builder, env, cacheDetailsLoader).onClick(null);
        PowerMock.verifyAll();
    }
}
