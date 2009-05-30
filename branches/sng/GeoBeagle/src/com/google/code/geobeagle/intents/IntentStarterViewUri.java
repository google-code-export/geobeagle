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

import com.google.code.geobeagle.mainactivity.GeoBeagle;
import com.google.code.geobeagle.ui.GeocacheViewer;

import android.content.Intent;

public class IntentStarterViewUri implements IntentStarter {
    private final GeoBeagle mGeoBeagle;
    private final GeocacheToUri mGeocacheToUri;
    private final IntentFactory mIntentFactory;

    public IntentStarterViewUri(GeoBeagle geoBeagle, IntentFactory intentFactory,
            GeocacheViewer geocacheViewer, GeocacheToUri geocacheToUri) {
        mGeoBeagle = geoBeagle;
        mGeocacheToUri = geocacheToUri;
        mIntentFactory = intentFactory;
    }

    public void startIntent() {
        mGeoBeagle.startActivity(mIntentFactory.createIntent(Intent.ACTION_VIEW, mGeocacheToUri
                .convert(mGeoBeagle.getGeocache())));
    }
}
