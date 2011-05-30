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

import com.google.code.geobeagle.activity.compass.GeocacheFromPreferencesFactory;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ActivityRestorer {

    private final Restorer[] restorers;
    private final SharedPreferences sharedPreferences;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Inject
    public ActivityRestorer(Activity activity,
            GeocacheFromPreferencesFactory geocacheFromPreferencesFactory,
            SharedPreferences sharedPreferences,
            CacheListRestorer cacheListRestorer) {
        this.sharedPreferences = sharedPreferences;
        this.restorers = new Restorer[] {
                cacheListRestorer, cacheListRestorer, cacheListRestorer,
                new ViewCacheRestorer(geocacheFromPreferencesFactory, sharedPreferences, activity)
        };
    }

    public boolean restore(int flags, ActivityType currentActivityType) {
        if ((flags & Intent.FLAG_ACTIVITY_NEW_TASK) == 0) {
            return false;
        }
        final String lastActivity = sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY,
                ActivityType.NONE.name());
        final ActivityType activityType = ActivityType.valueOf(lastActivity);
        if (currentActivityType != activityType) {
            Log.d("GeoBeagle", "restoring: " + activityType);
            restorers[activityType.toInt()].restore();
            return true;
        }
        return false;
    }
}
