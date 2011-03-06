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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.inject.Inject;

import android.content.SharedPreferences;

public class GeocacheFromPreferencesFactory {
    
    private final GeocacheFactory mGeocacheFactory;

    @Inject
    public GeocacheFromPreferencesFactory(GeocacheFactory geocacheFactory) {
        mGeocacheFactory = geocacheFactory;
    }

    public Geocache create(SharedPreferences preferences) {
        final int iSource = preferences.getInt(Geocache.SOURCE_TYPE, -1);
        final Source source = mGeocacheFactory.sourceFromInt(Math.max(Source.MIN_SOURCE, Math.min(
                iSource, Source.MAX_SOURCE)));
        final int iCacheType = preferences.getInt(Geocache.CACHE_TYPE, 0);
        final CacheType cacheType = mGeocacheFactory.cacheTypeFromInt(iCacheType);
        return mGeocacheFactory.create(preferences.getString(Geocache.ID, "GCMEY7"), preferences
                .getString(Geocache.NAME, "Google Falls"), preferences.getFloat(Geocache.LATITUDE, 0),
                preferences.getFloat(Geocache.LONGITUDE, 0), source, preferences.getString(
                        Geocache.SOURCE_NAME, ""), cacheType, preferences.getInt(
                        Geocache.DIFFICULTY, 0), preferences.getInt(Geocache.TERRAIN, 0),
                preferences.getInt(Geocache.CONTAINER, 0), preferences.getBoolean(Geocache.AVAILABLE, true),
                preferences.getBoolean(Geocache.ARCHIVED, false));
    }
}
