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
import com.google.code.geobeagle.activity.compass.Util;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;

public class GeocacheFromIntentFactory {
    static final String GEO_BEAGLE_SAVED_IN_DATABASE = "com.google.code.geobeagle.SavedInDatabase";
    private final GeocacheFactory mGeocacheFactory;
    private final Provider<DbFrontend> mDbFrontendProvider;

    @Inject
    GeocacheFromIntentFactory(GeocacheFactory geocacheFactory, Provider<DbFrontend> dbFrontendProvider) {
        mGeocacheFactory = geocacheFactory;
        mDbFrontendProvider = dbFrontendProvider;
    }

    Geocache viewCacheFromMapsIntent(Intent intent, LocationSaver locationSaver,
            Geocache defaultGeocache) {
        Log.d("GeoBeagle", "viewCacheFromMapsIntent: " + intent);

        final String query = intent.getData().getQuery();
        CharSequence sanitizedQuery = Util.parseHttpUri(query, new UrlQuerySanitizer(),
                UrlQuerySanitizer.getAllButNulAndAngleBracketsLegal());
        if (sanitizedQuery == null)
            return defaultGeocache;
        final CharSequence[] latlon = Util
                .splitLatLonDescription(sanitizedQuery);
        CharSequence cacheId = latlon[2];
        Bundle extras = intent.getExtras();
        if (extras != null && extras.getBoolean(GEO_BEAGLE_SAVED_IN_DATABASE)) {
            Log.d("GeoBeagle", "Loading from database: " + cacheId);
            return mDbFrontendProvider.get().getCache(cacheId);
        }
        final Geocache geocache = mGeocacheFactory.create(cacheId, latlon[3],
                Util.parseCoordinate(latlon[0]), Util
                        .parseCoordinate(latlon[1]), Source.WEB_URL, null,
                CacheType.NULL, 0, 0, 0, true, false);
        locationSaver.saveLocation(geocache);
        intent.putExtra("com.google.code.geobeagle.SavedInDatabase", true);

        return geocache;
    }
}
