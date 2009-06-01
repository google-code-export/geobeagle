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

package com.google.code.geobeagle.cachelistactivity.actions.menu;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachelistactivity.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.cachelistactivity.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.LocationSaver;

public class MenuActionMyLocation implements MenuAction {
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheFromMyLocationFactory mGeocacheFromMyLocationFactory;
    private final LocationSaver mLocationSaver;
    private final CacheListRefresh mMenuActionRefresh;

    public MenuActionMyLocation(LocationSaver locationSaver,
            GeocacheFromMyLocationFactory geocacheFromMyLocationFactory,
            CacheListRefresh cacheListRefresh, ErrorDisplayer errorDisplayer) {
        mLocationSaver = locationSaver;
        mGeocacheFromMyLocationFactory = geocacheFromMyLocationFactory;
        mErrorDisplayer = errorDisplayer;
        mMenuActionRefresh = cacheListRefresh;
    }

    public void act() {
        Geocache myLocation = mGeocacheFromMyLocationFactory.create();
        if (myLocation == null) {
            mErrorDisplayer.displayError(R.string.current_location_null);
            return;
        }
        mLocationSaver.saveLocation(myLocation);
        mMenuActionRefresh.forceRefresh();
    }
}
