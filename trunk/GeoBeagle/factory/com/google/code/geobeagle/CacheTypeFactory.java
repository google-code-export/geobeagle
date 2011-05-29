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

import java.util.Hashtable;

public class CacheTypeFactory {
    private final Hashtable<Integer, CacheType> mCacheTypes =
        new Hashtable<Integer, CacheType>(CacheType.values().length);

    public CacheTypeFactory() {
        for (CacheType cacheType : CacheType.values())
            mCacheTypes.put(cacheType.toInt(), cacheType);
    }

    public CacheType fromInt(int i) {
        return mCacheTypes.get(i);
    }

    public CacheType fromTag(String tag) {
        String tagLower = tag.toLowerCase();
        int longestMatch = 0;

        CacheType result = CacheType.NULL;
        for (CacheType cacheType : mCacheTypes.values()) {
            if (tagLower.contains(cacheType.getTag()) && cacheType.getTag().length() > longestMatch) {
                result = cacheType;
                longestMatch = cacheType.getTag().length();
                // Necessary to continue the search to find mega-events and
                // individual waypoint types.
            }
        }

        return result;
    }

    public int container(String container) {
        if (container.equals("Micro")) {
            return 1;
        } else if (container.equals("Small")) {
            return 2;
        } else if (container.equals("Regular")) {
            return 3;
        } else if (container.equals("Large")) {
            return 4;
        } else if (container.equals("Other")) {
            return 5;
        }
        return 0;
    }

    public int stars(String stars) {
        try {
            return Math.round(Float.parseFloat(stars) * 2);
        } catch (Exception ex) {
            return 0;
        }
    }
}
