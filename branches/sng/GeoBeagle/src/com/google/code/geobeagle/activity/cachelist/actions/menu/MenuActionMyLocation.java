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
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.ISQLiteDatabase;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.database.LocationSaverFactory;

public class MenuActionMyLocation implements MenuAction {
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheFromMyLocationFactory mGeocacheFromMyLocationFactory;
    private final LocationSaverFactory mLocationSaverFactory;
    private final CacheListRefresh mMenuActionRefresh;
    private final ISQLiteDatabase mWritableDatabase;

    public MenuActionMyLocation(CacheListRefresh cacheListRefresh,
            ErrorDisplayer errorDisplayer, GeocacheFromMyLocationFactory geocacheFromMyLocationFactory,
            LocationSaverFactory locationSaverFactory, ISQLiteDatabase writableDatabase) {
        mGeocacheFromMyLocationFactory = geocacheFromMyLocationFactory;
        mErrorDisplayer = errorDisplayer;
        mMenuActionRefresh = cacheListRefresh;
        mLocationSaverFactory = locationSaverFactory;
        mWritableDatabase = writableDatabase;
    }

    public void act() {
        final Geocache myLocation = mGeocacheFromMyLocationFactory.create();
        if (myLocation == null) {
            mErrorDisplayer.displayError(R.string.current_location_null);
            return;
        }
        final LocationSaver mLocationSaver = mLocationSaverFactory
                .createLocationSaver(mWritableDatabase);
        mLocationSaver.saveLocation(myLocation);
        mMenuActionRefresh.forceRefresh();
    }
}
