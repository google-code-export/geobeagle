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

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationAndDirection;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.DbFrontend;

import android.content.res.Resources;

public class MenuActionMyLocation implements MenuAction {
    private final ErrorDisplayer mErrorDisplayer;
    private final CacheListAdapter mListRefresher;
    private final DbFrontend mDbFrontend;
    private final Resources mResources;
    private final GeocacheFactory mGeocacheFactory;
    private final LocationAndDirection mLocationControl;
    private final CacheAction mCacheActionEdit;

    public MenuActionMyLocation(CacheListAdapter cacheList, ErrorDisplayer errorDisplayer,
            GeocacheFactory geocacheFactory,
            LocationAndDirection locationControl, DbFrontend dbFrontend,
            Resources resources, CacheAction cacheActionEdit) {
        mErrorDisplayer = errorDisplayer;
        mListRefresher = cacheList;
        mGeocacheFactory = geocacheFactory;
        mLocationControl = locationControl;
        mDbFrontend = dbFrontend;
        mResources = resources;
        mCacheActionEdit = cacheActionEdit;
    }

    @Override
    public void act() {
        GeoFix location = mLocationControl.getLocation();
        if (location == null)
            return;

        long time = location.getTime();
        final Geocache newCache = mGeocacheFactory.create(String.format("ML%1$tk%1$tM%1$tS", time), String.format(
                "[%1$tk:%1$tM] My Location", time), location.getLatitude(),
                location.getLongitude(), Source.MY_LOCATION, null, CacheType.MY_LOCATION, 0, 0, 0);

        if (newCache == null) {
            mErrorDisplayer.displayError(R.string.current_location_null);
            return;
        }
        newCache.saveToDb(mDbFrontend);
        mCacheActionEdit.act(newCache);
        //mListRefresher.forceRefresh();
    }
    
    @Override
    public String getLabel() {
        return mResources.getString(R.string.menu_add_my_location);
    }
}
