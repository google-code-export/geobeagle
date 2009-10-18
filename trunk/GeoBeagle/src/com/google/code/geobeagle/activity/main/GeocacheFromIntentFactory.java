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

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.LocationSaver;

import android.content.Intent;
import android.net.UrlQuerySanitizer;

public class GeocacheFromIntentFactory {
    private final GeocacheFactory mGeocacheFactory;

    GeocacheFromIntentFactory(GeocacheFactory geocacheFactory) {
        mGeocacheFactory = geocacheFactory;
    }

    Geocache viewCacheFromMapsIntent(Intent intent, LocationSaver locationSaver,
            Geocache defaultGeocache) {
        final String query = intent.getData().getQuery();
        CharSequence sanitizedQuery = Util.parseHttpUri(query, new UrlQuerySanitizer(),
                UrlQuerySanitizer.getAllButNulAndAngleBracketsLegal());
        if (sanitizedQuery == null)
            return defaultGeocache;
        final CharSequence[] latlon = Util.splitLatLonDescription(sanitizedQuery);
        final Geocache geocache = mGeocacheFactory.create(latlon[2], latlon[3], Util
                .parseCoordinate(latlon[0]), Util.parseCoordinate(latlon[1]), Source.WEB_URL, null,
                CacheType.NULL, 0, 0, 0);
        locationSaver.saveLocation(geocache);
        return geocache;
    }
}
