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

package com.google.code.geobeagle.activity.cachelist.actions.menu;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.Action;
import com.google.code.geobeagle.activity.EditCacheActivity;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.Intent;

public class MenuActionMyLocation implements Action {
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheFromMyLocationFactory mGeocacheFromMyLocationFactory;
    private final LocationSaver mLocationSaver;
    private final Activity mActivity;

    @Inject
    public MenuActionMyLocation(Activity activity, ErrorDisplayer errorDisplayer,
            GeocacheFromMyLocationFactory geocacheFromMyLocationFactory, LocationSaver locationSaver) {
        mGeocacheFromMyLocationFactory = geocacheFromMyLocationFactory;
        mErrorDisplayer = errorDisplayer;
        mLocationSaver = locationSaver;
        mActivity = activity;
    }

    @Override
    public void act() {
        final Geocache myLocation = mGeocacheFromMyLocationFactory.create();
        if (myLocation == null) {
            mErrorDisplayer.displayError(R.string.current_location_null);
            return;
        }
        mLocationSaver.saveLocation(myLocation);
        final Intent intent = new Intent(mActivity, EditCacheActivity.class);
        intent.putExtra("geocache", myLocation);
        mActivity.startActivityForResult(intent, 0);
    }
}
