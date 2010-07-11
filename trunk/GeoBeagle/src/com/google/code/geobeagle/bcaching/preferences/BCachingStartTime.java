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

public class BCachingStartTime {
    static final String BCACHING_NEXT_START = "bcaching-next-start";
    static final String BCACHING_START = "bcaching-start";

    private final LastReadPosition lastReadPosition;
    private final SharedPreferences sharedPreferences;
    private final PreferencesWriter preferencesWriter;

    @Inject
    BCachingStartTime(SharedPreferences sharedPreferences,
            PreferencesWriter preferencesWriter, LastReadPosition lastReadPosition) {
        this.sharedPreferences = sharedPreferences;
        this.preferencesWriter = preferencesWriter;
        this.lastReadPosition = lastReadPosition;
    }

    public void clearStartTime() {
        preferencesWriter.putLong(BCACHING_START, 0);
        lastReadPosition.put(0);
    }

    public long getLastUpdateTime() {
        return sharedPreferences.getLong(BCACHING_START, 0);
    }

    public void putNextStartTime(long serverTime) {
        preferencesWriter.putLong(BCACHING_NEXT_START, serverTime);
    }

    public void resetStartTime() {
        preferencesWriter
                .putLong(BCACHING_START, sharedPreferences.getLong(BCACHING_NEXT_START, 0));
    }
}
