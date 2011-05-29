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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.location.LocationLifecycleManager;
import com.google.inject.Inject;

import android.content.SharedPreferences;
import android.location.LocationManager;

public class AppLifecycleManager {
    private final LifecycleManager[] lifecycleManagers;
    private final SharedPreferences sharedPreferences;

    @Inject
    AppLifecycleManager(SharedPreferences preferences,
            LocationControlBuffered locationControlBuffered,
            LocationManager locationManager,
            RadarView radarView) {
        this.lifecycleManagers = new LifecycleManager[] {
                new LocationLifecycleManager(locationControlBuffered, locationManager),
                new LocationLifecycleManager(radarView, locationManager)
        };

        sharedPreferences = preferences;
    }

    AppLifecycleManager(SharedPreferences sharedPreferences, LifecycleManager[] lifecycleManagers) {
        this.sharedPreferences = sharedPreferences;
        this.lifecycleManagers = lifecycleManagers;
    }

    public void onPause() {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        for (LifecycleManager lifecycleManager : lifecycleManagers) {
            lifecycleManager.onPause(editor);
        }
        editor.commit();
    }

    public void onResume() {
        for (LifecycleManager lifecycleManager : lifecycleManagers) {
            lifecycleManager.onResume(sharedPreferences);
        }
    }
}
