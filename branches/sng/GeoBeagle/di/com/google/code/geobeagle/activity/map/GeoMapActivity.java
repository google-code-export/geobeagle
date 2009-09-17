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
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.MenuActions;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.activity.map.DensityMatrix.DensityPatch;
import com.google.code.geobeagle.database.GeocachesLoader;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class GeoMapActivity extends MapActivity {
    private static final int DEFAULT_ZOOM_LEVEL = 12;
    private static boolean fZoomed = false;
    private static final int menuIdArray[] = {
            R.id.menu_toggle_satellite, R.id.menu_cache_list
    };
    private GeocachesLoader mGeocachesLoader;
    private GeoMapActivityDelegate mGeoMapActivityDelegate;
    private GeoMapView mMapView;
    private MyLocationOverlay mMyLocationOverlay;

    @Override
    protected boolean isRouteDisplayed() {
        // This application doesn't use routes
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        mMapView = (GeoMapView)findViewById(R.id.mapview);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setSatellite(false);

        final Resources resources = getResources();
        final Drawable defaultMarker = resources.getDrawable(R.drawable.map_pin2_others);
        final CacheDrawables cacheDrawables = new CacheDrawables(resources);
        final CacheItemFactory cacheItemFactory = new CacheItemFactory(cacheDrawables);

        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        final List<Overlay> mapOverlays = mMapView.getOverlays();
        final MenuAction menuActionArray[] = {
                new GeoMapActivityDelegate.MenuActionToggleSatellite(mMapView),
                new GeoMapActivityDelegate.MenuActionCacheList(this)
        };
        final MenuActions menuActions = new MenuActions(menuActionArray, menuIdArray);

        mGeocachesLoader = new GeocachesLoader(this);
        final Intent intent = getIntent();
        final MapController mapController = mMapView.getController();
        final double latitude = intent.getFloatExtra("latitude", 0);
        final double longitude = intent.getFloatExtra("longitude", 0);
        final Overlay nullOverlay = new GeoMapActivityDelegate.NullOverlay();

        // South Pole, as east as possible
        final GeoPoint topLeft = new GeoPoint(-90 * GeoUtils.MILLION, 360 * GeoUtils.MILLION);
        // North Pole, as west as possible
        final GeoPoint bottomRight = new GeoPoint(90 * GeoUtils.MILLION, 0 * GeoUtils.MILLION);

        mapOverlays.add(nullOverlay);
        mapOverlays.add(mMyLocationOverlay);
        final List<DensityPatch> densityPatches = new ArrayList<DensityPatch>();

        final DensityOverlayDelegate densityOverlayDelegate = DensityOverlay.createDelegate(
                densityPatches, mGeocachesLoader, topLeft, bottomRight);
        final DensityOverlay densityOverlay = new DensityOverlay(densityOverlayDelegate);
        final ArrayList<Geocache> geocacheList = new ArrayList<Geocache>();
        final CachePinsOverlay cachePinsOverlay = new CachePinsOverlay(cacheItemFactory, this,
                defaultMarker, geocacheList);
        final OverlayManager overlayManager = new OverlayManager(topLeft, bottomRight, mMapView,
                mGeocachesLoader, this, defaultMarker, cacheItemFactory, mapOverlays, nullOverlay,
                densityOverlay, cachePinsOverlay);
        mGeoMapActivityDelegate = new GeoMapActivityDelegate(mMapView, menuActions);

        final GeoPoint center = new GeoPoint((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));

        mapController.setCenter(center);
        mMapView.setScrollListener(overlayManager);

        if (!fZoomed) {
            mapController.setZoom(DEFAULT_ZOOM_LEVEL);
            fZoomed = true;
        }

        overlayManager.refreshCaches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.map_menu, menu);

        return true;
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
        mGeocachesLoader.closeDatabase();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableCompass();
        mGeocachesLoader.openDatabase();
    }
}
