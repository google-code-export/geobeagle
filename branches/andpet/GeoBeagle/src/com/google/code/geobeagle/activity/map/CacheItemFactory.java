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
import com.google.code.geobeagle.database.DbFrontend;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

class CacheItemFactory {
    private final Resources mResources;
    private Geocache mSelected;
    private final GraphicsGenerator mGraphicsGenerator;
    private final DbFrontend mDbFrontend;

    CacheItemFactory(Resources resources, GraphicsGenerator graphicsGenerator,
            DbFrontend dbFrontend) {
        mResources = resources;
        mGraphicsGenerator = graphicsGenerator;
        mDbFrontend = dbFrontend;
    }

    void setSelectedGeocache(Geocache geocache) {
        mSelected = geocache;
    }

    CacheItem createCacheItem(Geocache geocache) {
        final CacheItem cacheItem = new CacheItem(geocache.getGeoPoint(), geocache);
        Drawable marker = geocache.getIconMap(mResources, mGraphicsGenerator, mDbFrontend);
        if (geocache == mSelected) {
            marker = mGraphicsGenerator.superimpose(marker, mResources
                    .getDrawable(R.drawable.glow_40px));
        }
        cacheItem.setMarker(marker);
        return cacheItem;
    }
}
