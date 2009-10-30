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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GraphicsGenerator;
import com.google.code.geobeagle.R;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

class CacheItemFactory {
    private final Resources mResources;
    private Geocache mSelected;

    CacheItemFactory(Resources resources) {
        mResources = resources; 
    }

    void setSelectedGeocache(Geocache geocache) {
        mSelected = geocache;
    }
    
    CacheItem createCacheItem(Geocache geocache) {
        final CacheItem cacheItem = new CacheItem(geocache.getGeoPoint(), geocache);
        if (geocache == mSelected) {
            Drawable selected = GraphicsGenerator.superimpose(geocache.getIconMap(mResources),
                    mResources.getDrawable(R.drawable.glow_40px));
            cacheItem.setMarker(selected);
        } else {
            cacheItem.setMarker(geocache.getIconMap(mResources));
        }
        return cacheItem;
    }
}