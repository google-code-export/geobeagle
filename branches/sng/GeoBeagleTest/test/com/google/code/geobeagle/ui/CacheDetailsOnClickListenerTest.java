
package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.ui.CacheDetailsOnClickListener.CacheDetailsLoader;
import com.google.code.geobeagle.ui.CacheDetailsOnClickListener.Env;
import com.google.code.geobeagle.ui.CacheDetailsOnClickListener.MockableView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.View;
import android.webkit.WebView;

import java.io.FileInputStream;
import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class CacheDetailsOnClickListenerTest {
    @Test
    public void testCacheDetailsLoaderLoad() throws IOException {
        Env env = PowerMock.createMock(Env.class);
        FileInputStream fileInputStream = PowerMock.createMock(FileInputStream.class);

        expect(env.createFileInputStream("/sdcard/GeoBeagle/GC1234.html")).andReturn(
                fileInputStream);
        String details = "cache details";
        byte[] detailsBuffer = details.getBytes();
        expect(fileInputStream.available()).andReturn(details.length());
        expect(env.createBuffer(details.length())).andReturn(detailsBuffer);
        expect(fileInputStream.read(detailsBuffer)).andReturn(details.length());
        fileInputStream.close();

        PowerMock.replay(env);
        PowerMock.replay(fileInputStream);
        CacheDetailsLoader cacheDetailsLoader = new CacheDetailsLoader();
        assertEquals("cache details", cacheDetailsLoader.load(env, null, "GC1234"));
        PowerMock.verifyAll();
    }

    @Test
    public void testOkListener() {
        DialogInterface dialog = PowerMock.createMock(DialogInterface.class);

        dialog.dismiss();

        PowerMock.replayAll();
        CacheDetailsOnClickListener.OkListener okListener = new CacheDetailsOnClickListener.OkListener();
        okListener.onClick(dialog, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnClick() {
        GeoBeagle geobeagle = PowerMock.createMock(GeoBeagle.class);
        Builder builder = PowerMock.createMock(Builder.class);
        AlertDialog alertDialog = PowerMock.createMock(AlertDialog.class);
        Env env = PowerMock.createMock(Env.class);
        GeocacheViewer geocacheViewer = PowerMock.createMock(GeocacheViewer.class);
        MockableView mockableDetailsView = PowerMock.createMock(MockableView.class);
        CacheDetailsLoader cacheDetailsLoader = PowerMock.createMock(CacheDetailsLoader.class);
        View detailsView = PowerMock.createMock(View.class);
        WebView webView = PowerMock.createMock(WebView.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        expect(env.inflate(R.layout.cache_details, null)).andReturn(mockableDetailsView);
        expect(geobeagle.getGeocache()).andReturn(geocache);
        expect(geocache.getId()).andReturn("GC1234");
        expect(builder.setTitle("GC1234")).andReturn(builder);
        expect(mockableDetailsView.findViewById(R.id.webview)).andReturn(webView);
        expect(mockableDetailsView.getView()).andReturn(detailsView);
        expect(builder.setView(detailsView)).andReturn(builder);
        expect(cacheDetailsLoader.load(env, null, "GC1234")).andReturn("details");
        webView.loadDataWithBaseURL(null, "details", "text/html", "utf-8", "about:blank");
        expect(builder.create()).andReturn(alertDialog);
        alertDialog.show();

        PowerMock.replayAll();
        CacheDetailsOnClickListener cacheDetailsOnClickListener = new CacheDetailsOnClickListener(
                geobeagle, builder, geocacheViewer, null, env, cacheDetailsLoader);
        cacheDetailsOnClickListener.onClick(null);
        PowerMock.verifyAll();
    }
}
