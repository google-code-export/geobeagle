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

package com.google.code.geobeagle.database;

import com.google.inject.Inject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FilterCleanliness {
    private static final String PREF_FILTER_DIRTY = "filter-dirty";
    private final SharedPreferences sharedPreferences;

    @Inject
    FilterCleanliness(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void markDirty(boolean dirty) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_FILTER_DIRTY, dirty);
        editor.commit();
    }

    public boolean isDirty() {
        return sharedPreferences.getBoolean(PREF_FILTER_DIRTY, false);
    }
}
