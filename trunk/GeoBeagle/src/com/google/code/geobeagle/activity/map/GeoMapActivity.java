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

package com.google.code.geobeagle.activity.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.GeoUtils;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.inject.Inject;
import com.google.inject.Injector;

import roboguice.activity.GuiceMapActivity;
import roboguice.inject.ContextScoped;
import roboguice.inject.InjectView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GeoMapActivity extends GuiceMapActivity {

    private static final int DEFAULT_ZOOM_LEVEL = 14;

    private static boolean fZoomed = false;

    @Inject
    @ContextScoped
    private DbFrontend mDbFrontend;

    private GeoMapActivityDelegate mGeoMapActivityDelegate;

    @InjectView(R.id.mapview)
    private GeoMapView mMapView;

    private FixedMyLocationOverlay mMyLocationOverlay;

    public MyLocationOverlay getMyLocationOverlay() {
        return mMyLocationOverlay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mMapView is built inside setContentView, therefore any objects which
        // depend on mapView must be created after setContentView.
        setContentView(R.layout.map);
        final Injector injector = getInjector();

        mMyLocationOverlay = new FixedMyLocationOverlay(this, mMapView);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setSatellite(false);

        mGeoMapActivityDelegate = injector.getInstance(GeoMapActivityDelegate.class);

        final Intent intent = getIntent();
        final GeoPoint center = new GeoPoint(
                (int)(intent.getFloatExtra("latitude", 0) * GeoUtils.MILLION),
                (int)(intent.getFloatExtra("longitude", 0) * GeoUtils.MILLION));

        final MapController mapController = mMapView.getController();
        mapController.setCenter(center);
        final OverlayManager overlayManager = injector.getInstance(OverlayManager.class);
        mMapView.setScrollListener(overlayManager);

        if (!fZoomed) {
            mapController.setZoom(DEFAULT_ZOOM_LEVEL);
            fZoomed = true;
        }

        overlayManager.selectOverlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mGeoMapActivityDelegate.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return mGeoMapActivityDelegate.onMenuOpened(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mGeoMapActivityDelegate.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        mMyLocationOverlay.disableMyLocation();
        mMyLocationOverlay.disableCompass();
        mDbFrontend.closeDatabase();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableCompass();
        // Is this necessary? Or should we remove it and make openDatabase
        // private?
        mDbFrontend.openDatabase();
    }

    @Override
    protected boolean isRouteDisplayed() {
        // This application doesn't use routes
        return false;
    }
}
