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

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.MenuActions;
import com.google.code.geobeagle.database.GeocachesLoader;

public class GeoMapActivity extends MapActivity {
    GeoMapActivityDelegate mGeoMapActivityDelegate;
	private ZoomSupervisor mZoomSupervisor;
	private MyLocationOverlay mMyLocationOverlay;
	private GeocachesLoader mGeocachesLoader;

    @Override
    protected boolean isRouteDisplayed() {
        // This application doesn't use routes
        return false;
    }

    GeoMapView mMapView;
    CachePinsOverlay mMapItemizedOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mMapView = (GeoMapView)findViewById(R.id.mapview);

        final Resources resources = getResources();
        final Drawable defaultMarker = resources.getDrawable(R.drawable.map_pin2_others);
        final CacheDrawables cacheDrawables = new CacheDrawables(resources);
        final CacheItemFactory cacheItemFactory = new CacheItemFactory(cacheDrawables);
        mMapItemizedOverlay = new CachePinsOverlay(this, defaultMarker,
                                                     cacheItemFactory);
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        final List<Overlay> mapOverlays = mMapView.getOverlays();
        final MenuAction menuActionArray[] = {
                new GeoMapActivityDelegate.MenuActionToggleSatellite(mMapView),
                new GeoMapActivityDelegate.MenuActionCacheList(this)
        };
        final int menuIdArray[] = {
                R.id.menu_toggle_satellite, R.id.menu_cache_list
        };
        final MenuActions menuActions = new MenuActions(menuActionArray, menuIdArray);

        mapOverlays.add(mMapItemizedOverlay);
        mapOverlays.add(mMyLocationOverlay);
        
        mGeoMapActivityDelegate = new GeoMapActivityDelegate(mMapView, menuActions);

        mGeocachesLoader = new GeocachesLoader(this);
        final Intent intent = this.getIntent();
        final MapController mapController = mMapView.getController();
        mGeoMapActivityDelegate.initialize(intent, mGeocachesLoader,
                                           mMapItemizedOverlay, mapController);

        mZoomSupervisor = new ZoomSupervisor(mMapView, mGeoMapActivityDelegate);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableCompass();
        mGeocachesLoader.openDatabase();
        mZoomSupervisor.start();
    }

    @Override
    public void onPause() {
        mZoomSupervisor.stop();
        mMyLocationOverlay.disableMyLocation();
        mMyLocationOverlay.disableCompass();
        mGeocachesLoader.closeDatabase();
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
