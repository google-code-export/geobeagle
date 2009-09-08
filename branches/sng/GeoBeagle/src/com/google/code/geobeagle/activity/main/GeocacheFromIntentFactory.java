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
import com.google.code.geobeagle.database.ISQLiteDatabase;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.database.LocationSaverFactory;

import android.content.Intent;
import android.net.UrlQuerySanitizer;

public class GeocacheFromIntentFactory {
    private final GeocacheFactory mGeocacheFactory;
    private final LocationSaverFactory mLocationSaverFactory;

    GeocacheFromIntentFactory(GeocacheFactory geocacheFactory,
            LocationSaverFactory locationSaverFactory) {
        mGeocacheFactory = geocacheFactory;
        mLocationSaverFactory = locationSaverFactory;
    }

    static String[] getStrings() {
        return null;
    }

    Geocache viewCacheFromMapsIntent(Intent intent, ISQLiteDatabase writableDatabase) {
        final String query = intent.getData().getQuery();
        final CharSequence sanitizedQuery = Util.parseHttpUri(query, new UrlQuerySanitizer(),
                UrlQuerySanitizer.getAllButNulAndAngleBracketsLegal());
        final CharSequence[] latlon = Util.splitLatLonDescription(sanitizedQuery);
        final Geocache geocache = mGeocacheFactory.create(latlon[2], latlon[3], Util
                .parseCoordinate(latlon[0]), Util.parseCoordinate(latlon[1]), Source.WEB_URL, null,
                CacheType.NULL, 0, 0, 0);
        final LocationSaver locationSaver = mLocationSaverFactory
                .createLocationSaver(writableDatabase);
        locationSaver.saveLocation(geocache);
        return geocache;
    }
}
