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
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.compass.GeocacheFromIntentFactory;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.inject.Inject;

import android.content.Intent;

public class IncomingIntentHandler {
    private GeocacheFactory mGeocacheFactory;
    private GeocacheFromIntentFactory mGeocacheFromIntentFactory;

    @Inject
    IncomingIntentHandler(GeocacheFactory geocacheFactory,
            GeocacheFromIntentFactory geocacheFromIntentFactory) {
        mGeocacheFactory = geocacheFactory;
        mGeocacheFromIntentFactory = geocacheFromIntentFactory;
    }

    Geocache maybeGetGeocacheFromIntent(Intent intent, Geocache defaultGeocache,
            LocationSaver locationSaver) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                if (action.equals(Intent.ACTION_VIEW) && intent.getType() == null) {
                    return mGeocacheFromIntentFactory.viewCacheFromMapsIntent(intent,
                            locationSaver, defaultGeocache);
                } else if (action.equals(GeocacheListController.SELECT_CACHE)) {
                    Geocache geocache = intent.<Geocache> getParcelableExtra("geocache");
                    if (geocache == null)
                        geocache = mGeocacheFactory.create("", "", 0, 0, Source.MY_LOCATION, "",
                                CacheType.NULL, 0, 0, 0, true, false);
                    return geocache;
                }
            }
        }
        return defaultGeocache;
    }
}
