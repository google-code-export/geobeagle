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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender.FieldNoteResources;
import com.google.code.geobeagle.activity.main.view.CacheDetailsOnClickListener;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import java.util.HashMap;

public class GeoBeagleDelegate {

    private final ActivitySaver mActivitySaver;
    private final AppLifecycleManager mAppLifecycleManager;
    private final CacheDetailsOnClickListener mCacheDetailsOnClickListener;
    private final FieldNoteSender mFieldNoteSender;
    private final HashMap<Integer, MenuAction> mMenuActions;
    private final GeoBeagle mParent;
    private final Resources mResources;
    private Geocache mGeocache;
    private final SharedPreferences mSharedPreferences;
    private final RadarView mRadarView;

    public GeoBeagleDelegate(GeoBeagle parent, ActivitySaver activitySaver,
            AppLifecycleManager appLifecycleManager,
            CacheDetailsOnClickListener cacheDetailsOnClickListener,
            FieldNoteSender fieldNoteSender, HashMap<Integer, MenuAction> menuActions,
            Resources resources, SharedPreferences sharedPreferences, RadarView radarView) {
        mParent = parent;
        mActivitySaver = activitySaver;
        mAppLifecycleManager = appLifecycleManager;
        mCacheDetailsOnClickListener = cacheDetailsOnClickListener;
        mFieldNoteSender = fieldNoteSender;
        mMenuActions = menuActions;
        mResources = resources;
        mSharedPreferences = sharedPreferences;
        mRadarView = radarView;
    }

    public void onCreate() {
        ((Button)mParent.findViewById(R.id.cache_details))
                .setOnClickListener(mCacheDetailsOnClickListener);
    }

    public Dialog onCreateDialog(int id) {
        FieldNoteResources fieldNoteResources = new FieldNoteResources(mResources, id);
        return mFieldNoteSender.createDialog(mGeocache.getId(), fieldNoteResources, mParent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        mMenuActions.get(item.getItemId()).act();
        return true;
    }

    public void onPause() {
        mAppLifecycleManager.onPause();
        mActivitySaver.save(ActivityType.VIEW_CACHE, mGeocache);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        GeocacheFromParcelFactory geocacheFromParcelFactory = new GeocacheFromParcelFactory(
                new GeocacheFactory());
        mGeocache = geocacheFromParcelFactory.createFromBundle(savedInstanceState);
    }

    public void onResume() {
        mRadarView.setUseMetric(!mSharedPreferences.getBoolean("imperial", false));
        mAppLifecycleManager.onResume();
    }

    public void onSaveInstanceState(Bundle outState) {
        // apparently there are cases where getGeocache returns null, causing
        // crashes with 0.7.7/0.7.8.
        if (mGeocache != null)
            mGeocache.saveToBundle(outState);
    }

    public void setGeocache(Geocache geocache) {
        mGeocache = geocache;
    }

    public Geocache getGeocache() {
        return mGeocache;
    }
}
