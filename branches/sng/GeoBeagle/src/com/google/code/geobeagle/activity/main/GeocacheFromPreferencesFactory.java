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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;

import android.content.SharedPreferences;

public class GeocacheFromPreferencesFactory {
    private final GeocacheFactory mGeocacheFactory;

    public GeocacheFromPreferencesFactory(GeocacheFactory geocacheFactory) {
        mGeocacheFactory = geocacheFactory;
    }

    public Geocache create(SharedPreferences preferences) {
        final int iSource = preferences.getInt("sourceType", -1);
        Source source = mGeocacheFactory.sourceFromInt(iSource);
        return mGeocacheFactory.create(preferences.getString("id", null), preferences.getString(
                "name", null), preferences.getFloat("latitude", 0), preferences.getFloat(
                "longitude", 0), source, preferences.getString("sourceName", null));
    }
}
