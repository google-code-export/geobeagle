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

package com.google.code.geobeagle.activity.compass.intents;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.compass.fieldnotes.HasGeocache;

import android.app.Activity;
import android.content.Intent;

public class IntentStarterGeo implements IntentStarter {
    private final Activity geoBeagle;
    private final Intent intent;
    private final HasGeocache hasGeocache;

    public IntentStarterGeo(Activity geoBeagle, Intent intent, HasGeocache hasGeocache) {
        this.geoBeagle = geoBeagle;
        this.intent = intent;
        this.hasGeocache = hasGeocache;
    }

    @Override
    public void startIntent() {
        Geocache geocache = hasGeocache.get(geoBeagle);
        intent.putExtra("latitude", (float)geocache.getLatitude());
        intent.putExtra("longitude", (float)geocache.getLongitude());
        geoBeagle.startActivity(intent);
    }
}
