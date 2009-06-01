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

package com.google.code.geobeagle.mainactivity;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.mainactivity.fieldnotes.FieldNoteSender;
import com.google.code.geobeagle.mainactivity.fieldnotes.FieldNoteSenderDI;
import com.google.code.geobeagle.mainactivity.view.CacheDetailsOnClickListener;
import com.google.code.geobeagle.mainactivity.view.EditCacheActivity;
import com.google.code.geobeagle.mainactivity.view.GeocacheViewer;
import com.google.code.geobeagle.mainactivity.view.Misc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Button;

public class GeoBeagleDelegate {

    static GeoBeagleDelegate buildGeoBeagleDelegate(GeoBeagle parent,
            AppLifecycleManager appLifecycleManager, GeocacheViewer geocacheViewer,
            ErrorDisplayer errorDisplayer) {
        final AlertDialog.Builder cacheDetailsBuilder = new AlertDialog.Builder(parent);
        final LayoutInflater layoutInflater = LayoutInflater.from(parent);
        final CacheDetailsOnClickListener cacheDetailsOnClickListener = Misc.create(parent,
                cacheDetailsBuilder, geocacheViewer, errorDisplayer, layoutInflater);
        final FieldNoteSender fieldNoteSender = FieldNoteSenderDI.build(parent, layoutInflater);
        return new GeoBeagleDelegate(parent, appLifecycleManager, cacheDetailsBuilder,
                cacheDetailsOnClickListener, fieldNoteSender, errorDisplayer);
    }

    private final AppLifecycleManager mAppLifecycleManager;
    private final Builder mCacheDetailsBuilder;
    private final CacheDetailsOnClickListener mCacheDetailsOnClickListener;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeoBeagle mParent;
    private final FieldNoteSender mFieldNoteSender;

    public GeoBeagleDelegate(GeoBeagle parent, AppLifecycleManager appLifecycleManager,
            Builder cacheDetailsBuilder, CacheDetailsOnClickListener cacheDetailsOnClickListener,
            FieldNoteSender fieldNoteSender, ErrorDisplayer errorDisplayer) {
        mParent = parent;
        mAppLifecycleManager = appLifecycleManager;
        mCacheDetailsBuilder = cacheDetailsBuilder;
        mCacheDetailsOnClickListener = cacheDetailsOnClickListener;
        mFieldNoteSender = fieldNoteSender;
        mErrorDisplayer = errorDisplayer;
    }

    public void onCreate() {
        mCacheDetailsBuilder.create();

        ((Button)mParent.findViewById(R.id.cache_details))
                .setOnClickListener(mCacheDetailsOnClickListener);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_edit_geocache == item.getItemId()) {
            Intent intent = new Intent(mParent, EditCacheActivity.class);
            intent.putExtra("geocache", mParent.getGeocache());
            mParent.startActivityForResult(intent, 0);
        } else {
            mParent.showDialog(item.getItemId());
        }
        return true;
    }

    public Dialog onCreateDialog(int id) {
        return mFieldNoteSender.createDialog(mParent.getGeocache().getId(),
                id == R.id.menu_log_dnf ? 0 : 1);
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

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        GeocacheFromParcelFactory geocacheFromParcelFactory = new GeocacheFromParcelFactory(
                new GeocacheFactory());
        mParent.setGeocache(geocacheFromParcelFactory.createFromBundle(savedInstanceState));
    }

    public void onSaveInstanceState(Bundle outState) {
        mParent.getGeocache().saveToBundle(outState);
    }
}
