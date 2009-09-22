
package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.GeoBeagle;

//Could be changed into a CacheAction but then a "CacheAction as MenuAction" 
//wrapper is needed to launch from a button
public class MenuActionGoogleMaps implements MenuAction {
    private final GeoBeagle mGeoBeagle;
    private final CacheActionViewUri mCacheActionViewUri;    

    public MenuActionGoogleMaps(GeoBeagle geoBeagle,
            CacheActionViewUri cacheActionViewUri) {
        mGeoBeagle = geoBeagle;
        mCacheActionViewUri = cacheActionViewUri;
    }

    @Override
    public void act() {
        mCacheActionViewUri.act(mGeoBeagle.getGeocache());
    }

    @Override
    public int getId() {
        return R.string.menu_google_maps;
    }
}
