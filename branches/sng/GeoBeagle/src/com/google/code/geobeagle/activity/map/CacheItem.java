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

import com.google.android.maps.OverlayItem;
import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class CacheItem extends OverlayItem {
    private static Drawable mTraditionalDrawable;
    private static Drawable mMysteryDrawable;
    private static Drawable mMultiDrawable;
    private static Drawable mOthersDrawable;
    private Geocache mGeocache;

    private static Drawable loadCentralDrawable(Resources resources, int res) {
        Drawable result = resources.getDrawable(res);
        int width = result.getIntrinsicWidth();
        int height = result.getIntrinsicHeight();
        result.setBounds(-width / 2, -height / 2, width / 2, height / 2);
        return result;
    }

    public static CacheItem Create(Resources resources, Geocache geocache) {
        if (mTraditionalDrawable == null) {
            mTraditionalDrawable = loadCentralDrawable(resources, R.drawable.map_tradi);
            mMysteryDrawable = loadCentralDrawable(resources, R.drawable.map_mystery);
            mMultiDrawable = loadCentralDrawable(resources, R.drawable.map_multi);
            mOthersDrawable = loadCentralDrawable(resources, R.drawable.map_others);
        }

        CacheItem cache = new CacheItem(geocache);

        if (geocache.getCacheType() == CacheType.TRADITIONAL)
            cache.setMarker(mTraditionalDrawable);
        else if (geocache.getCacheType() == CacheType.UNKNOWN)
            cache.setMarker(mMysteryDrawable);
        else if (geocache.getCacheType() == CacheType.MULTI)
            cache.setMarker(mMultiDrawable);
        else
            cache.setMarker(mOthersDrawable);

        return cache;
    }

    private CacheItem(Geocache geocache) {
        super(geocache.getGeoPoint(), (String)geocache.getId(), "");
        mGeocache = geocache;
    }

    public Geocache getGeocache() {
        return mGeocache;
    }

}
