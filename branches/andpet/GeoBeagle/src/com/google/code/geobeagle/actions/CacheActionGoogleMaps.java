
package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;

import android.content.res.Resources;

public class CacheActionGoogleMaps implements CacheAction {
    private final Resources mResources;
    private final CacheActionViewUri mCacheActionViewUri;    

    public CacheActionGoogleMaps(CacheActionViewUri cacheActionViewUri,
            Resources resources) {
        mResources = resources;
        mCacheActionViewUri = cacheActionViewUri;
    }

    @Override
    public void act(Geocache cache) {
        mCacheActionViewUri.act(cache);
    }

    @Override
    public String getLabel(Geocache geocache) {
        return mResources.getString(R.string.menu_google_maps);
    }

}
