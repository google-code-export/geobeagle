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

package com.google.code.geobeagle.bcaching.preferences;

import com.google.inject.Inject;

import android.content.SharedPreferences;
import android.util.Log;

public class LastReadPosition {
    static final String BCACHING_LAST_READ_POSITION = "bcaching-last-read-position";
    private int lastRead;
    private final PreferencesWriter preferencesWriter;
    private final SharedPreferences sharedPreferences;

    @Inject
    LastReadPosition(SharedPreferences sharedPreferences,
            PreferencesWriter preferencesWriter) {
        this.preferencesWriter = preferencesWriter;
        this.sharedPreferences = sharedPreferences;
    }

    public int get() {
        return lastRead;
    }

    public void load() {
        lastRead = sharedPreferences.getInt(BCACHING_LAST_READ_POSITION, 0);
        Log.d("GeoBeagle", "Load last read: " + lastRead);
    }

    public void put(int lastRead) {
        Log.d("GeoBeagle", "Put last read: " + lastRead);
        preferencesWriter.putInt(BCACHING_LAST_READ_POSITION, lastRead);
        this.lastRead = lastRead;
    }
}
