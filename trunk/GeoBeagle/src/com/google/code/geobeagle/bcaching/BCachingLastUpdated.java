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

import com.google.inject.Inject;

import android.content.SharedPreferences;

public class BCachingLastUpdated {
    static final String BCACHING_LASTUPDATE = "bcaching_lastupdate";
    private final SharedPreferences sharedPreferences;

    @Inject
    BCachingLastUpdated(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    String getLastUpdateTime() {
        return sharedPreferences.getString(BCACHING_LASTUPDATE, "0");
    }

    void putLastUpdateTime(long now) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BCACHING_LASTUPDATE, Long.toString(now));
        editor.commit();
    }

    public void clearLastUpdateTime() {
        putLastUpdateTime(0);
    }
}