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

package com.google.code.geobeagle.database.filter;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.activity.preferences.Preferences;
import com.google.code.geobeagle.database.Tag;
import com.google.code.geobeagle.database.TagReader;
import com.google.inject.Inject;

import android.content.SharedPreferences;

public class Filter {
    private final SharedPreferences sharedPreferences;
    private final TagReader tagReader;

    @Inject
    public Filter(SharedPreferences sharedPreferences, TagReader tagReader) {
        this.sharedPreferences = sharedPreferences;
        this.tagReader = tagReader;
    }

    public boolean showBasedOnFoundState(boolean found) {
        boolean showFoundCaches = sharedPreferences.getBoolean(Preferences.SHOW_FOUND_CACHES,
                false);
        return showFoundCaches || !found;
    }

    public boolean showBasedOnDnfState(CharSequence geocacheId) {
        boolean showDnfCaches = sharedPreferences.getBoolean(Preferences.SHOW_DNF_CACHES, true);
        return showDnfCaches || !tagReader.hasTag(geocacheId, Tag.DNF);
    }

    public boolean showBasedOnAvailableState(boolean available) {
        boolean showUnavailableCaches = sharedPreferences.getBoolean(
                Preferences.SHOW_UNAVAILABLE_CACHES, false);
        return showUnavailableCaches || available;
    }

    public boolean showBasedOnCacheType(CacheType cacheType) {
        boolean showWaypoints = sharedPreferences.getBoolean(Preferences.SHOW_WAYPOINTS, false);
        return showWaypoints || cacheType.toInt() < CacheType.WAYPOINT.toInt();
    }
}
