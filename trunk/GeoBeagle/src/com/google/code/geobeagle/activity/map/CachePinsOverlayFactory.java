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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Timing;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;

public class CachePinsOverlayFactory {
    private final CacheItemFactory mCacheItemFactory;
    private CachePinsOverlay mCachePinsOverlay;
    private final Activity mActivity;
    private final QueryManager mQueryManager;
    private final Resources mResources;

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface CachePinsQueryManager {}

    @Inject
    public CachePinsOverlayFactory(Activity activity, CacheItemFactory cacheItemFactory,
            CachePinsOverlay cachePinsOverlay, @CachePinsQueryManager QueryManager queryManager,
            Resources resources) {
        mResources = resources;
        mActivity = activity;
        mCacheItemFactory = cacheItemFactory;
        mCachePinsOverlay = cachePinsOverlay;
        mQueryManager = queryManager;
    }

    public CachePinsOverlay getCachePinsOverlay() {
        Log.d("GeoBeagle", "refresh Caches");
        final Timing timing = new Timing();

        GeoMapView mGeoMapView = (GeoMapView)mActivity.findViewById(R.id.mapview);

        Projection projection = mGeoMapView .getProjection();
        GeoPoint newTopLeft = projection.fromPixels(0, 0);
        GeoPoint newBottomRight = projection.fromPixels(mGeoMapView.getRight(), mGeoMapView
                .getBottom());

        timing.start();

        if (!mQueryManager.needsLoading(newTopLeft, newBottomRight))
            return mCachePinsOverlay;

        ArrayList<Geocache> cacheList = mQueryManager.load(newTopLeft, newBottomRight);

        timing.lap("Loaded caches");
        mCachePinsOverlay = new CachePinsOverlay(mResources, mCacheItemFactory, mActivity,
                cacheList);
        return mCachePinsOverlay;
    }
}
