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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.MenuActions;
import com.google.code.geobeagle.activity.cachelist.CacheList;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.database.GeocachesLoader;
import com.google.code.geobeagle.database.WhereFactoryWithinRange;

public class GeoMapActivityDelegate {
    static class MenuActionCacheList implements MenuAction {
        private final Activity mActivity;

        MenuActionCacheList(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void act() {
            mActivity.startActivity(new Intent(mActivity, CacheList.class));
        }
    }

    public static class MenuActionToggleSatellite implements MenuAction {
        private final MapView mMapView;

        public MenuActionToggleSatellite(MapView mapView) {
            mMapView = mapView;
        }

        @Override
        public void act() {
            mMapView.setSatellite(!mMapView.isSatellite());
        }
    }

    private final GeoMapView mMapView;
    private final MenuActions mMenuActions;
	private MapItemizedOverlay mCachesOverlay;
	private GeocachesLoader mGeocachesLoader;
    private static boolean fZoomed = false;

	public GeoMapActivityDelegate(GeoMapView mapView,
	                              MenuActions menuActions) {
        mMapView = mapView;
        mMenuActions = menuActions;
    }

	public void initialize(Intent intent, 
	                       GeocachesLoader geocachesLoader,
	                       MapItemizedOverlay cachesOverlay,
	                       MapController mapController) {
    	mGeocachesLoader = geocachesLoader;
        mMapView.setBuiltInZoomControls(true);
        // mMapView.setOnLongClickListener()
        mMapView.setSatellite(false);
		mMapView.setScrollListener(this);
        
        double latitude = intent.getFloatExtra("latitude", 0);
        double longitude = intent.getFloatExtra("longitude", 0);
        GeoPoint center = new GeoPoint((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));

        mapController.setCenter(center);
        if (!fZoomed) {
            mapController.setZoom(14);
            fZoomed = true;
        }

        mCachesOverlay = cachesOverlay;
    }
    
    public boolean onMenuOpened(int featureId, Menu menu) {
        menu.findItem(R.id.menu_toggle_satellite).setTitle(
                mMapView.isSatellite() ? R.string.map_view : R.string.satellite_view);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

    public void refreshCaches() {
		GeoPoint center = mMapView.getMapCenter();
		double lat = center.getLatitudeE6() / 1000000.0;
		double lon = center.getLongitudeE6() / 1000000.0;
        
        int latSpanE6 = mMapView.getLatitudeSpan();
        int lonSpanE6 = mMapView.getLongitudeSpan();
        int zoomLevel = mMapView.getZoomLevel();
		Projection proj = mMapView.getProjection();
		GeoPoint upperLeft = proj.fromPixels(0, 0);
		GeoPoint lowerRight = proj.fromPixels(mMapView.getRight(), mMapView.getBottom());
        int latSpanE6Proj = lowerRight.getLatitudeE6() - upperLeft.getLatitudeE6();
        int lonSpanE6Proj = lowerRight.getLongitudeE6() - upperLeft.getLongitudeE6();
        
        Log.d("GeoBeagle", "getMeasuredWidth " + mMapView.getMeasuredWidth());
        Log.d("GeoBeagle", "Lower right is " + mMapView.getRight() + " " + mMapView.getBottom());
        Log.d("GeoBeagle", "refreshCaches area " + latSpanE6 + " " + lonSpanE6 + ", zoom = " + zoomLevel);
        Log.d("GeoBeagle", "  projection says " + latSpanE6Proj + " " + lonSpanE6Proj);
        //Minimum size of area to fetch geocaches. (The user is likely to zoom out and request more caches)
        latSpanE6 = Math.max(latSpanE6, 100);
        lonSpanE6 = Math.max(lonSpanE6, 100);
        //mWhereFactory.setSpan(latSpanE6/1000000.0, lonSpanE6/1000000.0);
        
        //WhereStringFactory whereStringFactory = new WhereStringFactory();
        //SearchFactory searchFactory = new SearchFactory();
        ArrayList<Geocache> list = mGeocachesLoader.loadCaches(lat, lon, new WhereFactoryWithinRange(latSpanE6/1000000.0, lonSpanE6/1000000.0));
        Log.d("GeoBeagle", "refreshCaches will load " + list.size() + " caches");
        mCachesOverlay.setCacheListUsingGuiThread(list);
    }

    /** Also call this when the layout is first determined */
    public void onLayoutChange() {
    	refreshCaches();    	
    }
    
    public void onScrollChange() {
    	refreshCaches();
    }
    
    public void onZoomChange(int prevZoom, int newZoom) {
    	Log.d("GeoBeagle", "New zoom level: " + newZoom);
    	refreshCaches();
    }
    
}
