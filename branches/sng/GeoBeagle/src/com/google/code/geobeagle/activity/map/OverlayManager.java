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
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI;
import com.google.code.geobeagle.database.GeocachesLoader;
import com.google.code.geobeagle.database.WhereFactoryFixedArea;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class OverlayManager {
    static class DensityMatrix2 {

    }

    public class TestOverlay extends Overlay {
        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            super.draw(canvas, mapView, shadow);
            // Log.d("GeoBeagle", "testoverlay draw: " + shadow);
        }
    }

    private static final int DENSITY_MAP_ZOOM_THRESHOLD = 14;
    public static final double RESOLUTION_LATITUDE = 0.01;
    public static final double RESOLUTION_LONGITUDE = 0.02;
    private GeoPoint mBottomRight;
    private final CacheItemFactory mCacheItemFactory;
    private final Context mContext;
    private final Drawable mDefaultMarker;
    private final GeocachesLoader mGeocachesLoader;
    private final GeoMapView mGeoMapView;
    private final List<Overlay> mMapOverlays;
    private GeoPoint mTopLeft;

    private boolean mUsesDensityMap;
    private final DensityOverlay mDensityOverlay;
    private CachePinsOverlay mCachePinsOverlay;

    public OverlayManager(GeoPoint topLeft, GeoPoint bottomRight, GeoMapView geoMapView,
            GeocachesLoader geocachesLoader, Context context, Drawable defaultMarker,
            CacheItemFactory cacheItemFactory, List<Overlay> mapOverlays,
            Overlay myLocationOverlay, DensityOverlay densityOverlay,
            CachePinsOverlay cachePinsOverlay) {
        mTopLeft = topLeft;
        mBottomRight = bottomRight;
        mGeoMapView = geoMapView;
        mGeocachesLoader = geocachesLoader;
        mContext = context;
        mDefaultMarker = defaultMarker;
        mCacheItemFactory = cacheItemFactory;
        mMapOverlays = mapOverlays;
        mDensityOverlay = densityOverlay;
        mCachePinsOverlay = cachePinsOverlay;
    }

    public boolean selectOverlay() {
        Log.d("GeoBeagle", "Zoom: " + mGeoMapView.getZoomLevel());
        boolean newZoomUsesDensityMap = mGeoMapView.getZoomLevel() < DENSITY_MAP_ZOOM_THRESHOLD;
        if (newZoomUsesDensityMap == mUsesDensityMap)
            return false;
        mUsesDensityMap = newZoomUsesDensityMap;
        mMapOverlays.set(0, mUsesDensityMap ? mDensityOverlay : mCachePinsOverlay);
        return true;
    }

    public void refreshCaches() {
        Log.d("GeoBeagle", "refresh Caches");
        final CacheListDelegateDI.Timing timing = new CacheListDelegateDI.Timing();

        Projection projection = mGeoMapView.getProjection();
        GeoPoint newTopLeft = projection.fromPixels(0, 0);
        GeoPoint newBottomRight = projection.fromPixels(mGeoMapView.getRight(), mGeoMapView
                .getBottom());

        // Further top/North is bigger, further left/West is smaller.
        if (!selectOverlay() && newTopLeft.getLatitudeE6() <= mTopLeft.getLatitudeE6()
                && newTopLeft.getLongitudeE6() >= mTopLeft.getLongitudeE6()
                && newBottomRight.getLatitudeE6() >= mBottomRight.getLatitudeE6()
                && newBottomRight.getLongitudeE6() <= mBottomRight.getLongitudeE6()) {
            Log.d("GeoBeagle", "refresh caches punting");
            return;
        }
        mTopLeft = newTopLeft;
        mBottomRight = newBottomRight;

        timing.lap("Loaded caches");

        if (mUsesDensityMap) {
            return;
        }

        double latMin = newBottomRight.getLatitudeE6() / 1000000.0;
        double lonMin = newTopLeft.getLongitudeE6() / 1000000.0;
        double latMax = newTopLeft.getLatitudeE6() / 1000000.0;
        double lonMax = newBottomRight.getLongitudeE6() / 1000000.0;

        final WhereFactoryFixedArea where = new WhereFactoryFixedArea(latMin, lonMin, latMax,
                lonMax);

        timing.start();
        ArrayList<Geocache> list = mGeocachesLoader.loadCaches(0, 0, where);
        mCachePinsOverlay = new CachePinsOverlay(mCacheItemFactory, mContext, mDefaultMarker, list);
        mMapOverlays.set(0, mCachePinsOverlay);
    }
}
