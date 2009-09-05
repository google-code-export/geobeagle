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

import com.google.code.geobeagle.CacheType;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.util.HashMap;

class CacheDrawables {
    private static Drawable loadAndSizeDrawable(Resources resources, int res) {
        Drawable drawable = resources.getDrawable(res);
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(-width / 2, -height / 2, width / 2, height / 2);
        return drawable;
    }

    private final HashMap<CacheType, Drawable> mCacheDrawables;

    CacheDrawables(Resources resources) {
        final CacheType[] cacheTypes = CacheType.values();
        mCacheDrawables = new HashMap<CacheType, Drawable>(cacheTypes.length);
        for (CacheType cacheType : cacheTypes) {
            final Drawable loadAndSizeDrawable = loadAndSizeDrawable(resources, cacheType
                    .iconMap());
            mCacheDrawables.put(cacheType, loadAndSizeDrawable);
        }
    }

    Drawable get(CacheType cacheType) {
        return mCacheDrawables.get(cacheType);
    }
}