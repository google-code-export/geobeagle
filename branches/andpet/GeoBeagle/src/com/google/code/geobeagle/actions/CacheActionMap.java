package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.map.GeoMapActivity;

import android.app.Activity;
import android.content.Intent;

public class CacheActionMap implements CacheAction {
    Activity mActivity;
    
    public CacheActionMap(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void act(Geocache cache) {
        final Intent intent = 
            new Intent(mActivity, GeoMapActivity.class);
        intent.putExtra("latitude", (float)cache.getLatitude());
        intent.putExtra("longitude", (float)cache.getLongitude());
        intent.putExtra("geocacheId", cache.getId());
        mActivity.startActivity(intent);
    }

    @Override
    public String getLabel(Geocache geocache) {
        return mActivity.getResources().getString(R.string.menu_cache_map);
    }

}
