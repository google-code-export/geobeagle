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

package com.google.code.geobeagle;

public enum CacheType {
    NULL(0, R.drawable.blank, R.drawable.blank, R.drawable.blank, false), MULTI(2,
            R.drawable.cache_multi, R.drawable.cache_multi_big, R.drawable.map_multi), TRADITIONAL(
            1, R.drawable.cache_tradi, R.drawable.cache_tradi_big, R.drawable.map_tradi), UNKNOWN(
            3, R.drawable.cache_mystery, R.drawable.cache_mystery_big, R.drawable.map_mystery), MY_LOCATION(
            4, R.drawable.blue_dot, R.drawable.blue_dot, R.drawable.blue_dot);

    public static class CacheTypeFactory {
        private final CacheType mCacheTypes[] = new CacheType[values().length];

        public CacheTypeFactory() {
            for (CacheType cacheType : values())
                mCacheTypes[cacheType.mIx] = cacheType;
        }

        public CacheType fromInt(int i) {
            return mCacheTypes[i];
        }
    }

    private final int mIconId;
    private final int mIconIdBig;
    private final int mIx;
    private final boolean mVisible;
    private final int mIconIdMap;

    CacheType(int ix, int drawableId, int drawableIdBig, int drawableIdMap) {
        mIx = ix;
        mIconId = drawableId;
        mIconIdBig = drawableIdBig;
        mIconIdMap = drawableIdMap;
        mVisible = true;
    }

    CacheType(int ix, int drawableId, int drawableIdBig, int drawableIdMap, boolean visible) {
        mIx = ix;
        mIconId = drawableId;
        mIconIdBig = drawableIdBig;
        mIconIdMap = drawableIdMap;
        mVisible = visible;
    }

    public int icon() {
        return mIconId;
    }

    public int iconBig() {
        return mIconIdBig;
    }

    public int toInt() {
        return mIx;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public int iconMap() {
        return mIconIdMap;
    }
}
