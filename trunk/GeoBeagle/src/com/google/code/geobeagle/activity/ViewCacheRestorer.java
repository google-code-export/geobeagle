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

package com.google.code.geobeagle.activity;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.compass.GeocacheFromPreferencesFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

class ViewCacheRestorer implements Restorer {
    private final Activity activity;
    private final GeocacheFromPreferencesFactory geocacheFromPreferencesFactory;
    private final SharedPreferences sharedPreferences;

    public ViewCacheRestorer(GeocacheFromPreferencesFactory geocacheFromPreferencesFactory,
            SharedPreferences sharedPreferences,
            Activity activity) {
        this.geocacheFromPreferencesFactory = geocacheFromPreferencesFactory;
        this.sharedPreferences = sharedPreferences;
        this.activity = activity;
    }

    @Override
    public void restore() {
        final Geocache geocache = geocacheFromPreferencesFactory.create(sharedPreferences);
        final Intent intent = new Intent(activity, CompassActivity.class);
        intent.putExtra("geocache", geocache).setAction(GeocacheListController.SELECT_CACHE);
        activity.startActivity(intent);
    }
}
