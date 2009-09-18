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
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.database.DbFrontend;

public class GeoMapActivity extends MapActivity {
    GeoMapActivityDelegate mGeoMapActivityDelegate;
	private ZoomSupervisor mZoomSupervisor;
	private MyLocationOverlay mMyLocationOverlay;
	//private DensityMatrix mDensityMatrix;
	private DensityOverlay mDensityOverlay;
	private DbFrontend mDbFrontend;

    @Override
    protected boolean isRouteDisplayed() {
        // This application doesn't use routes
        return false;
    }

    GeoMapView mMapView;
    CachePinsOverlay mCachePinsOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mMapView = (GeoMapView)findViewById(R.id.mapview);

        final Resources resources = getResources();
        final Drawable defaultMarker = resources.getDrawable(R.drawable.map_pin2_others);
        final CacheDrawables cacheDrawables = new CacheDrawables(resources);
        final CacheItemFactory cacheItemFactory = new CacheItemFactory(cacheDrawables);
        mCachePinsOverlay = new CachePinsOverlay(this, defaultMarker,
                                                     cacheItemFactory);

        //mDensityMatrix = new DensityMatrix(0.01, 0.01);
        mDensityOverlay = new DensityOverlay();
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        final List<Overlay> mapOverlays = mMapView.getOverlays();

        MenuActions menuActions = new MenuActions(getResources());
        menuActions.add(new GeoMapActivityDelegate.MenuActionToggleSatellite(mMapView));
        menuActions.add(new MenuActionCacheList(this));

        //Add the overlays in the intended z-order:
        mapOverlays.add(mDensityOverlay);
        mapOverlays.add(mCachePinsOverlay);
        mapOverlays.add(mMyLocationOverlay);
        
        mGeoMapActivityDelegate = new GeoMapActivityDelegate(mMapView, menuActions);

        mDbFrontend = new DbFrontend(this);
        final Intent intent = this.getIntent();
        final MapController mapController = mMapView.getController();
        mGeoMapActivityDelegate.initialize(intent, mDbFrontend,
                                           mCachePinsOverlay, mapController,
                                           mDensityOverlay);
        mZoomSupervisor = new ZoomSupervisor(mMapView, mGeoMapActivityDelegate);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableCompass();
        mZoomSupervisor.start();
    }

    @Override
    public void onPause() {
        mZoomSupervisor.stop();
        mMyLocationOverlay.disableMyLocation();
        mMyLocationOverlay.disableCompass();
        mDbFrontend.closeDatabase();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.map_menu, menu);
        return mGeoMapActivityDelegate.onCreateOptionsMenu(menu);
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
