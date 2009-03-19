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

package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.ui.LocationSetter;

import android.content.Context;
import android.content.Intent;

public class IntentStarterRadar implements IntentStarter {
    private final Context mContext;
    private final IntentFactory mIntentFactory;
    private final LocationSetter mLocationSetter;

    public IntentStarterRadar(Context context, IntentFactory intentFactory,
            LocationSetter locationSetter) {
        mIntentFactory = intentFactory;
        mContext = context;
        mLocationSetter = locationSetter;
    }

    public void startIntent() {
        final Geocache geocache = mLocationSetter.getGeocache();
        final Intent intent = mIntentFactory.createIntent("com.google.android.radar.SHOW_RADAR");
        intent.putExtra("latitude", (float)geocache.getLatitude());
        intent.putExtra("longitude", (float)geocache.getLongitude());
        mContext.startActivity(intent);
    }
}
