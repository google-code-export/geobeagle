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
import com.google.android.maps.Projection;
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI;
import com.google.code.geobeagle.database.CachesProviderLazyArea;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class CachePinsOverlayFactory {
    private final CacheItemFactory mCacheItemFactory;
    private CachePinsOverlay mCachePinsOverlay;
    private final Context mContext;
    private final Drawable mDefaultMarker;
    private final GeoMapView mGeoMapView;
    private CachesProviderLazyArea mLazyArea;

    public CachePinsOverlayFactory(GeoMapView geoMapView, Context context,
            Drawable defaultMarker, CacheItemFactory cacheItemFactory,
            CachePinsOverlay cachePinsOverlay, CachesProviderLazyArea lazyArea) {
        mGeoMapView = geoMapView;
        mContext = context;
        mDefaultMarker = defaultMarker;
        mCacheItemFactory = cacheItemFactory;
        mCachePinsOverlay = cachePinsOverlay;
        mLazyArea = lazyArea;
    }

    public CachePinsOverlay getCachePinsOverlay() {
        //Log.d("GeoBeagle", "CachePinsOverlayFactory.getCachePinsOverlay()");
        final CacheListDelegateDI.Timing timing = new CacheListDelegateDI.Timing();

        Projection projection = mGeoMapView.getProjection();
        GeoPoint newTopLeft = projection.fromPixels(0, 0);
        GeoPoint newBottomRight = projection.fromPixels(mGeoMapView.getRight(), mGeoMapView
                .getBottom());

        timing.start();
        double latLow = newBottomRight.getLatitudeE6() / 1.0E6;
        double latHigh = newTopLeft.getLatitudeE6() / 1.0E6;
        double lonLow = newTopLeft.getLongitudeE6() / 1.0E6;
        double lonHigh = newBottomRight.getLongitudeE6() / 1.0E6;
        mLazyArea.setBounds(latLow, lonLow, latHigh, lonHigh);
        if (!mLazyArea.hasChanged())
            return mCachePinsOverlay;
        mLazyArea.showToastIfTooManyCaches();
        GeocacheList list = mLazyArea.getCaches();
        mLazyArea.resetChanged();
        mCachePinsOverlay = new CachePinsOverlay(mCacheItemFactory, mContext, mDefaultMarker, list);
        timing.lap("getCachePinsOverlay took");
        return mCachePinsOverlay;
    }
}
