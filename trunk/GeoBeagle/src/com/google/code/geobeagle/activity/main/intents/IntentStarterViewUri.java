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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.CompassActivity;
import com.google.code.geobeagle.cacheloader.CacheLoaderException;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

public class IntentStarterViewUri implements IntentStarter {
    private final CompassActivity mGeoBeagle;
    private final GeocacheToUri mGeocacheToUri;
    private final ErrorDisplayer mErrorDisplayer;

    @Inject
    public IntentStarterViewUri(Activity geoBeagle, GeocacheToUri geocacheToUri,
            ErrorDisplayer errorDisplayer) {
        mGeoBeagle = (CompassActivity)geoBeagle;
        mGeocacheToUri = geocacheToUri;
        mErrorDisplayer = errorDisplayer;
    }

    @Override
    public void startIntent() {
        try {
            String uri = mGeocacheToUri.convert(mGeoBeagle.getGeocache());
            try {
                mGeoBeagle.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            } catch (ActivityNotFoundException e) {
                mErrorDisplayer.displayError(R.string.no_intent_handler, uri);
            }
        } catch (CacheLoaderException e) {
            mErrorDisplayer.displayError(e.getError(), e.getArgs());
        }
    }
}
