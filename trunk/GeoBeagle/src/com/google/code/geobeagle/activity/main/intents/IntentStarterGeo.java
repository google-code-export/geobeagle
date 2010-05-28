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

package com.google.code.geobeagle.activity.main.intents;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import android.content.Intent;

public class IntentStarterGeo implements IntentStarter {
    private final GeoBeagle mGeoBeagle;
    private final Intent mIntent;

    public IntentStarterGeo(GeoBeagle geoBeagle, Intent intent) {
        mGeoBeagle = geoBeagle;
        mIntent = intent;
    }

    public void startIntent() {
        final Geocache geocache = mGeoBeagle.getGeocache();
        mIntent.putExtra("latitude", (float)geocache.getLatitude());
        mIntent.putExtra("longitude", (float)geocache.getLongitude());
        mGeoBeagle.startActivity(mIntent);
    }
}
