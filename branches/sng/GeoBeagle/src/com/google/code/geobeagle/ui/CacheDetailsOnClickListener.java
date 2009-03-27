/**
 * 
 */

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.R;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CacheDetailsOnClickListener implements View.OnClickListener {

    public static class CacheDetailsLoader {
        public String load(Env env, ErrorDisplayer mErrorDisplayer, CharSequence id) {
            final String path = "/sdcard/GeoBeagle/" + id + ".html";
            try {
                FileInputStream fileInputStream = env.createFileInputStream(path);
                byte[] buffer = env.createBuffer(fileInputStream.available());
                fileInputStream.read(buffer);
                fileInputStream.close();
                return new String(buffer);
            } catch (FileNotFoundException e) {
                return "Error opening cache details file '" + path
                        + "'.  Please try unmounting your sdcard or re-importing the cache file.";
            } catch (IOException e) {
                return "Error reading cache details file '" + path
                        + "'.  Please try unmounting your sdcard or re-importing the cache file.";
            }
        }
    }

    public static class Env {
        LayoutInflater mLayoutInflater;

        public Env(LayoutInflater layoutInflater) {
            mLayoutInflater = layoutInflater;
        }

        public byte[] createBuffer(int size) {
            return new byte[size];
        }

        public FileInputStream createFileInputStream(String path) throws FileNotFoundException {
            return new FileInputStream(path);
        }

        MockableView inflate(int resource, ViewGroup root) {
            return new MockableView(mLayoutInflater.inflate(R.layout.cache_details, null));
        }
    }

    public static class MockableView {
        private final View mView;

        public MockableView(View view) {
            mView = view;
        }

        public View findViewById(int id) {
            return mView.findViewById(id);
        }

        public View getView() {
            return mView;
        }
    }

    public static class OkListener implements DialogInterface.OnClickListener {
        // @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public static CacheDetailsOnClickListener create(GeoBeagle geoBeagle,
            Builder alertDialogBuilder, GeocacheViewer geocacheViewer,
            ErrorDisplayer errorDisplayer, Env env) {
        final CacheDetailsLoader cacheDetailsLoader = new CacheDetailsLoader();
        return new CacheDetailsOnClickListener(geoBeagle, alertDialogBuilder, geocacheViewer,
                errorDisplayer, env, cacheDetailsLoader);
    }

    private final Builder mAlertDialogBuilder;
    private final CacheDetailsLoader mCacheDetailsLoader;
    private final Env mEnv;
    private final ErrorDisplayer mErrorDisplayer;
    private GeoBeagle mGeoBeagle;

    public CacheDetailsOnClickListener(GeoBeagle geoBeagle, Builder alertDialogBuilder,
            GeocacheViewer geocacheViewer, ErrorDisplayer errorDisplayer, Env env,
            CacheDetailsLoader cacheDetailsLoader) {
        mAlertDialogBuilder = alertDialogBuilder;
        mErrorDisplayer = errorDisplayer;
        mEnv = env;
        mCacheDetailsLoader = cacheDetailsLoader;
        mGeoBeagle = geoBeagle;
    }

    public void onClick(View v) {
        try {
            MockableView detailsView = mEnv.inflate(R.layout.cache_details, null);

            CharSequence id = mGeoBeagle.getGeocache().getId();
            mAlertDialogBuilder.setTitle(id);
            mAlertDialogBuilder.setView(detailsView.getView());

            WebView webView = (WebView)detailsView.findViewById(R.id.webview);
            webView.loadDataWithBaseURL(null, mCacheDetailsLoader.load(mEnv, mErrorDisplayer, id),
                    "text/html", "utf-8", "about:blank");

            mAlertDialogBuilder.create().show();
        } catch (Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}
