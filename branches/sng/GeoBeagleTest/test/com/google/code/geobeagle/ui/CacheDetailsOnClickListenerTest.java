
package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ui.CacheDetailsOnClickListener.CacheDetailsLoader;
import com.google.code.geobeagle.ui.CacheDetailsOnClickListener.Env;
import com.google.code.geobeagle.ui.CacheDetailsOnClickListener.MockableView;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.View;
import android.webkit.WebView;

import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class CacheDetailsOnClickListenerTest extends TestCase {

    public void testCacheDetailsLoaderLoad() throws IOException {
        Env env = createMock(Env.class);
        WebView webView = createMock(WebView.class);
        FileInputStream fileInputStream = createMock(FileInputStream.class);

        expect(env.createFileInputStream("/sdcard/GeoBeagle/GC1234.html")).andReturn(
                fileInputStream);
        String details = "cache details";
        byte[] detailsBuffer = details.getBytes();
        expect(fileInputStream.available()).andReturn(details.length());
        expect(env.createBuffer(details.length())).andReturn(detailsBuffer);
        expect(fileInputStream.read(detailsBuffer)).andReturn(details.length());
        fileInputStream.close();

        replay(env);
        replay(webView);
        replay(fileInputStream);
        CacheDetailsLoader cacheDetailsLoader = new CacheDetailsLoader();
        assertEquals("cache details", cacheDetailsLoader.load(env, null, "GC1234"));
        verify(env);
        verify(webView);
        verify(fileInputStream);
    }

    public void testOkListener() {
        DialogInterface dialog = createMock(DialogInterface.class);

        dialog.dismiss();

        replay(dialog);
        CacheDetailsOnClickListener.OkListener okListener = new CacheDetailsOnClickListener.OkListener();
        okListener.onClick(dialog, 0);
        verify(dialog);
    }

    public void testOnClick() {
        Builder builder = createMock(Builder.class);
        AlertDialog alertDialog = createMock(AlertDialog.class);
        Env env = createMock(Env.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        MockableView mockableDetailsView = createMock(MockableView.class);
        CacheDetailsLoader cacheDetailsLoader = createMock(CacheDetailsLoader.class);
        View detailsView = createMock(View.class);
        WebView webView = createMock(WebView.class);

        expect(env.inflate(R.layout.cache_details, null)).andReturn(mockableDetailsView);
        expect(locationSetter.getId()).andReturn("GC1234");
        expect(builder.setTitle("GC1234")).andReturn(builder);
        expect(mockableDetailsView.findViewById(R.id.webview)).andReturn(webView);
        expect(mockableDetailsView.getView()).andReturn(detailsView);
        expect(builder.setView(detailsView)).andReturn(builder);
        expect(cacheDetailsLoader.load(env, null, "GC1234")).andReturn("details");
        webView.loadDataWithBaseURL(null, "details", "text/html", "utf-8", "about:blank");
        expect(builder.create()).andReturn(alertDialog);
        alertDialog.show();

        replay(webView);
        replay(alertDialog);
        replay(mockableDetailsView);
        replay(env);
        replay(locationSetter);
        replay(builder);
        replay(cacheDetailsLoader);
        CacheDetailsOnClickListener cacheDetailsOnClickListener = new CacheDetailsOnClickListener(
                builder, locationSetter, null, env, cacheDetailsLoader);
        cacheDetailsOnClickListener.onClick(null);
        verify(locationSetter);
        verify(env);
        verify(builder);
        verify(mockableDetailsView);
        verify(webView);
        verify(cacheDetailsLoader);
        verify(alertDialog);
    }
}
