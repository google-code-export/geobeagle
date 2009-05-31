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

package com.google.code.geobeagle.mainactivity.intents;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.mainactivity.ui.ContentSelector;
import com.google.code.geobeagle.mainactivity.ui.MyLocationProvider;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import java.util.Locale;

public class IntentStarterLocation implements IntentStarter {
    private final Activity mActivity;
    private final ContentSelector mContentSelector;
    private final Toast mGetCoordsToast;
    private final IntentFactory mIntentFactory;
    private final MyLocationProvider mMyLocationProvider;
    private final ResourceProvider mResourceProvider;
    private final int mUriId;

    public IntentStarterLocation(Activity activity, ResourceProvider resourceProvider,
            IntentFactory intentFactory, MyLocationProvider myLocationProvider,
            ContentSelector contentSelector, int uriId, Toast getCoordsToast) {
        mActivity = activity;
        mGetCoordsToast = getCoordsToast;
        mIntentFactory = intentFactory;
        mMyLocationProvider = myLocationProvider;
        mResourceProvider = resourceProvider;
        mContentSelector = contentSelector;
        mUriId = uriId;
    }

    public void startIntent() {
        final Location location = mMyLocationProvider.getLocation();
        if (location == null)
            return;

        mGetCoordsToast.show();
        mActivity.startActivity(mIntentFactory.createIntent(Intent.ACTION_VIEW, String.format(
                Locale.US, mResourceProvider.getStringArray(mUriId)[mContentSelector.getIndex()],
                location.getLatitude(), location.getLongitude())));
    }
}
