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

import android.content.SharedPreferences.Editor;

public class ActivitySaver {
    private final Editor mEditor;
    static final String LAST_ACTIVITY = "lastActivity";

    ActivitySaver(Editor editor) {
        mEditor = editor;
    }

    public void save(ActivityType activityType) {
        mEditor.putInt("lastActivity", activityType.toInt());
        mEditor.commit();
    }

    public void save(ActivityType activityType, Geocache geocache) {
        mEditor.putInt("lastActivity", activityType.toInt());
        mEditor.putString(Geocache.ID, geocache.getId().toString());
        mEditor.commit();
    }
}
