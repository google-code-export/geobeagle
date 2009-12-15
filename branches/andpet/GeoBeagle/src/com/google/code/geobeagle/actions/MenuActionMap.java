package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.map.GeoMapActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;

/** Show the map, centered around the current location */
public class MenuActionMap extends ActionStaticLabel implements MenuAction  {
    private final GeoFixProvider mLocationControl;
    private final Activity mActivity;
    
    public MenuActionMap(Activity activity, 
            GeoFixProvider locationControl, Resources resources)  {
        super(resources, R.string.menu_map);
        mActivity = activity;
        mLocationControl = locationControl;
    }
    
    @Override
    public void act() {
        GeoFix location = mLocationControl.getLocation();
        final Intent intent = 
            new Intent(mActivity, GeoMapActivity.class);
        intent.putExtra("latitude", (float)location.getLatitude());
        intent.putExtra("longitude", (float)location.getLongitude());
        mActivity.startActivity(intent);
    }
}
