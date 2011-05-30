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
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Timing;
import com.google.code.geobeagle.activity.map.QueryManager.LoaderImpl;
import com.google.code.geobeagle.activity.map.click.MapClickIntentFactory;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;

public class CachePinsOverlayFactory {
    private final CacheItemFactory cacheItemFactory;
    private CachePinsOverlay cachePinsOverlay;
    private final Activity activity;
    private final QueryManager queryManager;
    private final Resources resources;
    private final LoaderImpl loaderImpl;
    private final MapClickIntentFactory mapClickIntentFactory;

    @Inject
    public CachePinsOverlayFactory(Activity activity,
            CacheItemFactory cacheItemFactory,
            QueryManager queryManager,
            Resources resources,
            LoaderImpl loaderImpl,
            MapClickIntentFactory mapClickIntentFactory) {
        this.resources = resources;
        this.activity = activity;
        this.cacheItemFactory = cacheItemFactory;
        this.mapClickIntentFactory = mapClickIntentFactory;
        this.cachePinsOverlay = new CachePinsOverlay(resources, cacheItemFactory, activity,
                new ArrayList<Geocache>(), mapClickIntentFactory);
        this.queryManager = queryManager;
        this.loaderImpl = loaderImpl;
    }

    public CachePinsOverlay getCachePinsOverlay() {
        Log.d("GeoBeagle", "refresh Caches");
        Timing timing = new Timing();

        GeoMapView geoMapView = (GeoMapView)activity.findViewById(R.id.mapview);

        Projection projection = geoMapView.getProjection();
        GeoPoint newTopLeft = projection.fromPixels(0, 0);
        GeoPoint newBottomRight = projection.fromPixels(geoMapView.getRight(),
                geoMapView.getBottom());

        timing.start();

        if (!queryManager.needsLoading(newTopLeft, newBottomRight))
            return cachePinsOverlay;

        ArrayList<Geocache> cacheList = queryManager.load(newTopLeft, newBottomRight, loaderImpl);

        timing.lap("Loaded caches");
        cachePinsOverlay = new CachePinsOverlay(resources, cacheItemFactory, activity, cacheList,
                mapClickIntentFactory);
        return cachePinsOverlay;
    }
}
