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
import com.google.code.geobeagle.activity.main.GeoBeagleModule.DefaultSharedPreferences;
import com.google.inject.Inject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ActivitySaver {
    private final SharedPreferences sharedPreferences;
    static final String LAST_ACTIVITY = "lastActivity2";

    @Inject
    ActivitySaver(@DefaultSharedPreferences SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void save(ActivityType activityType) {
        Editor editor = sharedPreferences.edit();
        editor.putString(LAST_ACTIVITY, activityType.name());
        editor.commit();
    }

    public void save(ActivityType activityType, Geocache geocache) {
        Editor editor = sharedPreferences.edit();
        editor.putString(LAST_ACTIVITY, activityType.name());
        geocache.writeToPrefs(editor);
        editor.commit();
    }
}
