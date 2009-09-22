package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;

import android.app.Activity;
import android.content.Intent;

public class CacheActionRadar implements CacheAction {
    Activity mActivity;
    
    public CacheActionRadar(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void act(Geocache cache) {
        final Intent intent = 
            new Intent("com.google.android.radar.SHOW_RADAR");
        intent.putExtra("latitude", (float)cache.getLatitude());
        intent.putExtra("longitude", (float)cache.getLongitude());
        mActivity.startActivity(intent);
    }

    @Override
    public int getId() {
        return R.string.radar;
    }

}
