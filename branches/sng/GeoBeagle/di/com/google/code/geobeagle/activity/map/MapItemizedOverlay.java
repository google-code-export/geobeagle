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
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.database.GeocachesSql;
import com.google.code.geobeagle.database.WhereFactory;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class MapItemizedOverlay extends ItemizedOverlay<CacheItem> {

    private final CacheItemFactory mCacheItemFactory;
    private final Context mContext;
    private final ArrayList<CacheItem> mOverlays;

    public MapItemizedOverlay(Context context, Drawable defaultMarker,
            CacheItemFactory cacheItemFactory) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
        mCacheItemFactory = cacheItemFactory;
        mOverlays = new ArrayList<CacheItem>();
    }

    public void addCaches(Context context, double latitude, double longitude,
            GeocachesSql geocachesSql, WhereFactory whereFactory) {
        clearOverlays();

        geocachesSql.loadCaches(latitude, longitude, whereFactory);
        ArrayList<Geocache> list = geocachesSql.getGeocaches();

        for (Geocache cache : list) {
            CacheItem item = mCacheItemFactory.createCacheItem(cache);
            if (item != null)
                addOverlay(item);
        }
        populate();
    }

    public void addOverlay(CacheItem overlay) {
        mOverlays.add(overlay);
        // populate();
    }

    public void clearOverlays() {
        mOverlays.clear();
        populate();
    }

    @Override
    protected CacheItem createItem(int i) {
        return mOverlays.get(i);
    }

    public void doPopulate() {
        populate();
    }

    @Override
    protected boolean onTap(int i) {
        Geocache geocache = mOverlays.get(i).getGeocache();
        if (geocache == null)
            return false;

        // Intent intent = new Intent(GeocacheListController.SELECT_CACHE);

        Intent intent = new Intent(mContext, GeoBeagle.class);
        intent.setAction(GeocacheListController.SELECT_CACHE);
        intent.putExtra("geocache", geocache);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

        return true;
    }

    @Override
    public int size() {
        return mOverlays.size();
    }
}
