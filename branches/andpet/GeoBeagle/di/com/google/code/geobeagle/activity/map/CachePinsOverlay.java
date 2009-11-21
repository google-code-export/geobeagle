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
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class CachePinsOverlay extends ItemizedOverlay<CacheItem> {

    private final CacheItemFactory mCacheItemFactory;
    private final Context mContext;
    private final GeocacheList mCacheList;

    public CachePinsOverlay(CacheItemFactory cacheItemFactory, Context context,
            Drawable defaultMarker, GeocacheList list) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
        mCacheItemFactory = cacheItemFactory;
        mCacheList = list;
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

        final Intent intent = new Intent(mContext, GeoBeagle.class);
        intent.setAction(GeocacheListController.SELECT_CACHE);
        intent.putExtra("geocacheId", geocache.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

        return true;
    }

    @Override
    protected CacheItem createItem(int i) {
        return mCacheItemFactory.createCacheItem(mCacheList.get(i));
    }

    @Override
    public int size() {
        return mCacheList.size();
    }
}
