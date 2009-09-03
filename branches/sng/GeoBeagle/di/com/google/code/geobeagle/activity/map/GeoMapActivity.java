
package com.google.code.geobeagle.activity.map;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GeoMapActivity extends MapActivity {
    GeoMapActivityDelegate mGeoMapActivityDelegate;

    @Override
    protected boolean isRouteDisplayed() {
        // This application doesn't use routes
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        MapView mapView = (MapView)findViewById(R.id.mapview);
        mGeoMapActivityDelegate = new GeoMapActivityDelegate(this, mapView, getApplicationContext());
        GeoBeagleSqliteOpenHelper open = new GeoBeagleSqliteOpenHelper(this);
        mGeoMapActivityDelegate.onCreate(savedInstanceState, open);
    }

    @Override
    public void onResume() {
        super.onResume();
        mGeoMapActivityDelegate.onResume();
    }

    @Override
    public void onPause() {
        mGeoMapActivityDelegate.onPause();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.map_menu, menu);
        // return mCacheListDelegate.onCreateOptionsMenu(menu);

        return true;

    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return mGeoMapActivityDelegate.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mGeoMapActivityDelegate.onOptionsItemSelected(item);
    }
}
