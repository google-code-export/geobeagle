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

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.map.CacheItemFactory;
import com.google.code.geobeagle.activity.map.click.MapClickIntentFactory;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;

import java.util.ArrayList;

public class CachePinsOverlay extends ItemizedOverlay<CacheItem> {

    private final CacheItemFactory cacheItemFactory;
    private final Context context;
    private final ArrayList<Geocache> cacheList;
    private final MapClickIntentFactory mapClickIntentFactory;

    public CachePinsOverlay(Resources resources,
            CacheItemFactory cacheItemFactory,
            Context context,
            ArrayList<Geocache> cacheList,
            MapClickIntentFactory mapClickIntentFactory) {
        super(boundCenterBottom(resources.getDrawable(R.drawable.map_pin2_others)));
        this.context = context;
        this.cacheItemFactory = cacheItemFactory;
        this.cacheList = cacheList;
        this.mapClickIntentFactory = mapClickIntentFactory;
        populate();
    }

    /* (non-Javadoc)
     * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean, long)
     */
    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        return super.draw(canvas, mapView, shadow, when);
    }

    @Override
    protected boolean onTap(int i) {
        Geocache geocache = getItem(i).getGeocache();
        if (geocache == null)
            return false;
        Intent intent = mapClickIntentFactory.createIntent(context, geocache);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return true;
    }

    @Override
    protected CacheItem createItem(int i) {
        return cacheItemFactory.createCacheItem(cacheList.get(i));
    }

    @Override
    public int size() {
        return cacheList.size();
    }
}
