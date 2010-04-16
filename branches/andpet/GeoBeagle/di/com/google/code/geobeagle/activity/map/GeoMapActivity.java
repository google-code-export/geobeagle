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
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheListPrecomputed;
import com.google.code.geobeagle.GraphicsGenerator;
import com.google.code.geobeagle.IToaster;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Toaster;
import com.google.code.geobeagle.GraphicsGenerator.AttributePainter;
import com.google.code.geobeagle.Toaster.OneTimeToaster;
import com.google.code.geobeagle.actions.CacheFilterUpdater;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActionEditFilter;
import com.google.code.geobeagle.actions.MenuActionFilterListPopup;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.activity.map.DensityMatrix.DensityPatch;
import com.google.code.geobeagle.activity.map.OverlayManager.OverlaySelector;
import com.google.code.geobeagle.database.CachesProviderDb;
import com.google.code.geobeagle.database.CachesProviderLazyArea;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.PeggedCacheProvider;
import com.google.code.geobeagle.database.CachesProviderLazyArea.CoordinateManager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GeoMapActivity extends MapActivity {

    public static Toaster.ToasterFactory peggedCacheProviderToasterFactory = new OneTimeToaster.OneTimeToasterFactory();

    private static class NullOverlay extends Overlay {
    }

    private static final int DEFAULT_ZOOM_LEVEL = 14;

    private static boolean fZoomed = false;
    private DbFrontend mDbFrontend;
    private GeoMapActivityDelegate mGeoMapActivityDelegate;
    private GeoMapView mMapView;
    private MyLocationOverlay mMyLocationOverlay;
    private OverlayManager mOverlayManager;

    @Override
    protected boolean isRouteDisplayed() {
        // This application doesn't use routes
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // Set member variables first, in case anyone after this needs them.
        mMapView = (GeoMapView)findViewById(R.id.mapview);
        mDbFrontend = new DbFrontend(this, new GeocacheFactory());
        //mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay = new FixedMyLocationOverlay(this, mMapView);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setSatellite(false);

        final Resources resources = getResources();
        final Drawable defaultMarker = resources.getDrawable(R.drawable.pin_default);
        final GraphicsGenerator graphicsGenerator = new GraphicsGenerator(
                new GraphicsGenerator.RatingsGenerator(), new AttributePainter(new Paint(),
                        new Rect()));
        final CacheItemFactory cacheItemFactory = new CacheItemFactory(resources, graphicsGenerator, mDbFrontend);

        final FilterTypeCollection filterTypeCollection = new FilterTypeCollection(this);
        
        final List<Overlay> mapOverlays = mMapView.getOverlays();

        final Intent intent = getIntent();
        final MapController mapController = mMapView.getController();
        final double latitude = intent.getFloatExtra("latitude", 0);
        final double longitude = intent.getFloatExtra("longitude", 0);
        final Overlay nullOverlay = new GeoMapActivity.NullOverlay();
        final GeoPoint nullGeoPoint = new GeoPoint(0, 0);
        String geocacheId = intent.getStringExtra("geocacheId");
        if (geocacheId != null) {
            Geocache selected = mDbFrontend.loadCacheFromId(geocacheId);
            cacheItemFactory.setSelectedGeocache(selected);
        }

        mapOverlays.add(nullOverlay);
        mapOverlays.add(mMyLocationOverlay);

        final List<DensityPatch> densityPatches = new ArrayList<DensityPatch>();
        final Toaster toaster = new Toaster(this, R.string.too_many_caches, Toast.LENGTH_SHORT);
        final CachesProviderDb cachesProviderArea = new CachesProviderDb(mDbFrontend);
        final IToaster densityOverlayToaster = new OneTimeToaster(toaster);
        final PeggedCacheProvider peggedCacheProvider = new PeggedCacheProvider(
                peggedCacheProviderToasterFactory.getToaster(toaster));
        final CoordinateManager coordinateManager = new CoordinateManager(1.0);
        final CachesProviderLazyArea lazyArea = new CachesProviderLazyArea(
                cachesProviderArea, peggedCacheProvider, coordinateManager);
        final DensityOverlayDelegate densityOverlayDelegate = DensityOverlay.createDelegate(
                densityPatches, nullGeoPoint, lazyArea, densityOverlayToaster);
        final DensityOverlay densityOverlay = new DensityOverlay(densityOverlayDelegate);
        
        final CachePinsOverlay cachePinsOverlay = new CachePinsOverlay(cacheItemFactory, this,
                defaultMarker, GeocacheListPrecomputed.EMPTY);
        //Pin overlay and Density overlay can't share providers because the provider wouldn't report hasChanged() when switching between them
        CachesProviderDb cachesProviderAreaPins = new CachesProviderDb(mDbFrontend);
        final CoordinateManager coordinateManagerPins = new CoordinateManager(1.0);
        final CachesProviderLazyArea lazyAreaPins = new CachesProviderLazyArea(
                cachesProviderAreaPins, peggedCacheProvider,
                coordinateManagerPins);
        final CachePinsOverlayFactory cachePinsOverlayFactory = new CachePinsOverlayFactory(
                mMapView, this, defaultMarker, cacheItemFactory, cachePinsOverlay, lazyAreaPins);
        final GeoPoint center = new GeoPoint((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));
        mapController.setCenter(center);
        final OverlaySelector overlaySelector = new OverlaySelector();
        mOverlayManager = new OverlayManager(mMapView, mapOverlays,
                densityOverlay, cachePinsOverlayFactory, false,
                cachesProviderArea, filterTypeCollection, overlaySelector );
        mMapView.setScrollListener(mOverlayManager);

        // *** BUILD MENU ***
        final MenuActions menuActions = new MenuActions();
        menuActions.add(new GeoMapActivityDelegate.MenuActionToggleSatellite(mMapView));
        menuActions.add(new GeoMapActivityDelegate.MenuActionCenterLocation(resources, mapController, mMyLocationOverlay));
        menuActions.add(new MenuActionCacheList(this, resources));
        final List<CachesProviderDb> providers = new ArrayList<CachesProviderDb>();
        providers.add(cachesProviderArea);
        providers.add(cachesProviderAreaPins);
        final CacheFilterUpdater cacheFilterUpdater = 
            new CacheFilterUpdater(filterTypeCollection, providers);
        menuActions.add(new MenuActionEditFilter(this, cacheFilterUpdater, 
                mOverlayManager, filterTypeCollection, resources));
        menuActions.add(new MenuActionFilterListPopup(this, cacheFilterUpdater, 
                mOverlayManager, filterTypeCollection, resources));
        
        mGeoMapActivityDelegate = new GeoMapActivityDelegate(menuActions);

        if (!fZoomed) {
            mapController.setZoom(DEFAULT_ZOOM_LEVEL);
            fZoomed = true;
        }

        mOverlayManager.selectOverlay();
    }

    public OverlayManager getOverlayManager() {
        return mOverlayManager;
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
        mOverlayManager.forceRefresh();  //The cache filter might have changed
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableCompass();
        mDbFrontend.openDatabase();
    }
}
