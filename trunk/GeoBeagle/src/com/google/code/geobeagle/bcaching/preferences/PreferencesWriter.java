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

class PreferencesWriter {
    private final SharedPreferences sharedPreferences;

    @Inject
    PreferencesWriter(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    void putInt(String pref, int i) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(pref, i);
        editor.commit();
    }

    void putLong(String pref, long l) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(pref, l);
        editor.commit();
    }
}
