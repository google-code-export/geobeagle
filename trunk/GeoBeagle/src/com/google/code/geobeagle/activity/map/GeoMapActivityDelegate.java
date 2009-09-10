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
import com.google.code.geobeagle.database.WhereFactoryFixedArea;

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
    private CachePinsOverlay mCachesOverlay;
    private GeocachesLoader mGeocachesLoader;
    private static boolean fZoomed = false;
    private DensityOverlay mDensityOverlay;

    public GeoMapActivityDelegate(GeoMapView mapView, MenuActions menuActions) {
        mMapView = mapView;
        mMenuActions = menuActions;
    }

	public void initialize(Intent intent, 
	                       GeocachesLoader geocachesLoader,
	                       CachePinsOverlay cachesOverlay,
	                       MapController mapController,
	                       DensityOverlay densityOverlay) {
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
        mDensityOverlay = densityOverlay;
    }

    /**
     * @param featureId
     */
    public boolean onMenuOpened(int featureId, Menu menu) {
        menu.findItem(R.id.menu_toggle_satellite).setTitle(
                mMapView.isSatellite() ? R.string.map_view : R.string.satellite_view);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

    public void refreshCaches() {
		//GeoPoint center = mMapView.getMapCenter();
		//double lat = center.getLatitudeE6() / 1000000.0;
		//double lon = center.getLongitudeE6() / 1000000.0;
        //int zoomLevel = mMapView.getZoomLevel();
		//Projection proj = mMapView.getProjection();
        //int latSpanE6Proj = lowerRight.getLatitudeE6() - upperLeft.getLatitudeE6();
        //int lonSpanE6Proj = lowerRight.getLongitudeE6() - upperLeft.getLongitudeE6();

        //WhereStringFactory whereStringFactory = new WhereStringFactory();
    	//GeoPoint pt2 = proj.fromPixels(35, 35);  //grid size, in pixels
        /*
        double latResolution = 
        	Math.abs(pt2.getLatitudeE6()-pt1.getLatitudeE6()) / 1000000.0;
        double lonResolution = 
        	Math.abs(pt2.getLongitudeE6()-pt1.getLongitudeE6()) / 1000000.0;
        	*/
        
    	Projection proj = mMapView.getProjection();
    	GeoPoint pt1 = proj.fromPixels(0, 0);
        GeoPoint pt3 = proj.fromPixels(mMapView.getRight(), mMapView.getBottom());
        
        double latMin = Math.min(pt1.getLatitudeE6(), pt3.getLatitudeE6()) / 1000000.0;
        double lonMin = Math.min(pt1.getLongitudeE6(), pt3.getLongitudeE6()) / 1000000.0;
        double latMax = Math.max(pt1.getLatitudeE6(), pt3.getLatitudeE6()) / 1000000.0;
        double lonMax = Math.max(pt1.getLongitudeE6(), pt3.getLongitudeE6()) / 1000000.0;
        
    	//TODO: Adjust to look square on the screen, no matter the latitude
        double lonResolution = 0.02;  //((int)(lonResolution*1E4)) / 1.0E4;
        double latResolution = 0.01;   //lonResolution * Math.cos(latMin/90.0 * Math.PI/2.0);
        
        //Expand area to cover whole density patches:
        latMin = Math.floor(latMin / latResolution) * latResolution;
        lonMin = Math.floor(lonMin / lonResolution) * lonResolution;
        latMax = Math.ceil(latMax / latResolution) * latResolution;
        lonMax = Math.ceil(lonMax / lonResolution) * lonResolution;
        
		WhereFactoryFixedArea where = 
			new WhereFactoryFixedArea(latMin, lonMin, latMax, lonMax);
		
        ArrayList<Geocache> list = mGeocachesLoader.loadCaches(0, 0, where);
        Log.d("GeoBeagle", "GeoMapActivityDelegate.refreshCaches will load " 
              + list.size() + " caches");

        if (list.size() > 50) {
            DensityMatrix densityMatrix = new DensityMatrix(latResolution,
                                                            lonResolution);
            densityMatrix.addCaches(list);
            mDensityOverlay.setCacheListUsingGuiThread(densityMatrix);
            ArrayList<Geocache> empty = new ArrayList<Geocache>();
            mCachesOverlay.setCacheListUsingGuiThread(empty);
        } else {
            mDensityOverlay.setCacheListUsingGuiThread(null);
            mCachesOverlay.setCacheListUsingGuiThread(list);
        }
    }

    /** Also call this when the layout is first determined */
    public void onLayoutChange() {
    	//Log.d("GeoBeagle", "onLayoutChange");
    	refreshCaches();
    }

    public void onScrollChange() {
    	//Log.d("GeoBeagle", "onScrollChange");
    	refreshCaches();
    }

    /**
     * @param prevZoom
     */
    public void onZoomChange(int prevZoom, int newZoom) {
    	//Log.d("GeoBeagle", "New zoom level: " + newZoom);
    	refreshCaches();
    }

}
