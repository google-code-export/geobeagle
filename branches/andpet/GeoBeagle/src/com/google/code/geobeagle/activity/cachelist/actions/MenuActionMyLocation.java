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

package com.google.code.geobeagle.activity.cachelist.actions;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheList;
import com.google.code.geobeagle.database.DbFrontend;

public class MenuActionMyLocation extends MenuActionBase {
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheFromMyLocationFactory mGeocacheFromMyLocationFactory;
    private final CacheList mListRefresher;
    private final DbFrontend mDbFrontend;

    public MenuActionMyLocation(CacheList cacheList, ErrorDisplayer errorDisplayer,
            GeocacheFromMyLocationFactory geocacheFromMyLocationFactory, DbFrontend dbFrontend) {
        super(R.string.menu_add_my_location);
        mGeocacheFromMyLocationFactory = geocacheFromMyLocationFactory;
        mErrorDisplayer = errorDisplayer;
        mListRefresher = cacheList;
        mDbFrontend = dbFrontend;
    }

    @Override
    public void act() {
        final Geocache myLocation = mGeocacheFromMyLocationFactory.create();
        if (myLocation == null) {
            mErrorDisplayer.displayError(R.string.current_location_null);
            return;
        }
        myLocation.saveLocation(mDbFrontend);
        mListRefresher.forceRefresh();
    }
    
    @Override
    public int getId() {
        return R.string.menu_add_my_location;
    }
}
