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

package com.google.code.geobeagle.bcaching;

import com.google.code.geobeagle.activity.main.GeoBeagleModule.DefaultSharedPreferences;
import com.google.inject.Inject;

import android.content.SharedPreferences;

public class BCachingLastUpdated {
    static final String BCACHING_LAST_READ = "bcaching-last-read";
    static final String BCACHING_LAST_UPDATE = "bcaching-last-update";
    private final SharedPreferences sharedPreferences;

    @Inject
    BCachingLastUpdated(@DefaultSharedPreferences SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void clearLastUpdateTime() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(BCACHING_LAST_UPDATE, 0);
        editor.putInt(BCachingLastUpdated.BCACHING_LAST_READ, 0);
        editor.commit();
    }

    long getLastUpdateTime() {
        return sharedPreferences.getLong(BCACHING_LAST_UPDATE, 0);
    }

    void putLastUpdateTime(long lastUpdateTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(BCACHING_LAST_UPDATE, lastUpdateTime);
        editor.commit();
    }
}
