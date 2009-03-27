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

import com.google.code.geobeagle.ui.CacheDetailsOnClickListener;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GeocacheViewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.widget.Button;

public class GeoBeagleDelegate {

    static GeoBeagleDelegate buildGeoBeagleDelegate(GeoBeagle parent,
            AppLifecycleManager appLifecycleManager, GeocacheViewer geocacheViewer,
            ErrorDisplayer errorDisplayer) {
        final AlertDialog.Builder cacheDetailsBuilder = new AlertDialog.Builder(parent);
        final DialogInterface.OnClickListener cacheDetailsOkListener = new CacheDetailsOnClickListener.OkListener();
        final CacheDetailsOnClickListener.Env env = new CacheDetailsOnClickListener.Env(
                LayoutInflater.from(parent));
        final CacheDetailsOnClickListener cacheDetailsOnClickListener = CacheDetailsOnClickListener
                .create(parent, cacheDetailsBuilder, geocacheViewer, errorDisplayer, env);

        return new GeoBeagleDelegate(parent, appLifecycleManager, cacheDetailsBuilder,
                cacheDetailsOkListener, cacheDetailsOnClickListener, errorDisplayer);
    }

    private final AppLifecycleManager mAppLifecycleManager;
    private final Builder mCacheDetailsBuilder;
    private final OnClickListener mCacheDetailsOkListener;
    private final CacheDetailsOnClickListener mCacheDetailsOnClickListener;
    private final ErrorDisplayer mErrorDisplayer;
    private final Activity mParent;

    public GeoBeagleDelegate(Activity parent, AppLifecycleManager appLifecycleManager,
            Builder cacheDetailsBuilder, OnClickListener cacheDetailsOkListener,
            CacheDetailsOnClickListener cacheDetailsOnClickListener, ErrorDisplayer errorDisplayer) {
        mParent = parent;
        mAppLifecycleManager = appLifecycleManager;
        mCacheDetailsBuilder = cacheDetailsBuilder;
        mCacheDetailsOkListener = cacheDetailsOkListener;
        mCacheDetailsOnClickListener = cacheDetailsOnClickListener;
        mErrorDisplayer = errorDisplayer;
    }

    public void onCreate() {
        mCacheDetailsBuilder.setPositiveButton("Ok", mCacheDetailsOkListener);
        mCacheDetailsBuilder.create();

        ((Button)mParent.findViewById(R.id.cache_details))
                .setOnClickListener(mCacheDetailsOnClickListener);
    }

    public void onPause() {
        try {
            mAppLifecycleManager.onPause();
        } catch (Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    public void onResume() {
        try {
            mAppLifecycleManager.onResume();
        } catch (Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }
}
