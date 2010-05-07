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

import com.google.android.maps.Overlay;
import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection;
import com.google.code.geobeagle.database.CachesProviderDb;

import java.util.List;

public class OverlayManager implements Refresher {
    static final int DENSITY_MAP_ZOOM_THRESHOLD = 12;
    private final CachePinsOverlayFactory mCachePinsOverlayFactory;
    private final DensityOverlay mDensityOverlay;
    private final GeoMapView mGeoMapView;
    private final List<Overlay> mMapOverlays;
    private boolean mUsesDensityMap;
    private final CachesProviderDb mCachesProviderDb;
    private final FilterTypeCollection mFilterTypeCollection;
    private final OverlaySelector mOverlaySelector;

    static class OverlaySelector {
        boolean selectOverlay(OverlayManager overlayManager, int zoomLevel,
                boolean fUsesDensityMap, List<Overlay> mapOverlays,
                Overlay densityOverlay,
                CachePinsOverlayFactory cachePinsOverlayFactory) {
            // Log.d("GeoBeagle", "selectOverlay Zoom: " + zoomLevel);
            boolean newZoomUsesDensityMap = zoomLevel < DENSITY_MAP_ZOOM_THRESHOLD;
            if (newZoomUsesDensityMap && fUsesDensityMap)
                return newZoomUsesDensityMap;
            if (newZoomUsesDensityMap) {
                mapOverlays.set(0, densityOverlay);
            } else {
                mapOverlays.set(0, cachePinsOverlayFactory
                        .getCachePinsOverlay());
            }
            return newZoomUsesDensityMap;
        }
    }

    public OverlayManager(GeoMapView geoMapView, List<Overlay> mapOverlays,
            DensityOverlay densityOverlay,
            CachePinsOverlayFactory cachePinsOverlayFactory,
            boolean usesDensityMap, CachesProviderDb cachesProviderArea,
            FilterTypeCollection filterTypeCollection,
            OverlaySelector overlaySelector) {
        mGeoMapView = geoMapView;
        mMapOverlays = mapOverlays;
        mDensityOverlay = densityOverlay;
        mCachePinsOverlayFactory = cachePinsOverlayFactory;
        mUsesDensityMap = usesDensityMap;
        mCachesProviderDb = cachesProviderArea;
        mFilterTypeCollection = filterTypeCollection;
        mOverlaySelector = overlaySelector;
    }

    public void selectOverlay() {
        mUsesDensityMap = mOverlaySelector.selectOverlay(this, mGeoMapView
                .getZoomLevel(), mUsesDensityMap, mMapOverlays,
                mDensityOverlay, mCachePinsOverlayFactory);
    }

    public boolean usesDensityMap() {
        return mUsesDensityMap;
    }

    @Override
    public void forceRefresh() {
        CacheFilter cacheFilter = mFilterTypeCollection.getActiveFilter();
        mCachesProviderDb.setFilter(cacheFilter);
        selectOverlay();
        // Must be called from the GUI thread:
        mGeoMapView.invalidate();
    }

    @Override
    public void refresh() {
        selectOverlay();
    }
}
