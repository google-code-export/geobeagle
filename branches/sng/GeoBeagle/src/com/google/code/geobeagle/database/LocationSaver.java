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

package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

public class LocationSaver {
    private final CacheWriter mCacheWriter;

    public LocationSaver(CacheWriter cacheWriter) {
        mCacheWriter = cacheWriter;
    }

    public void saveLocation(Geocache geocache) {
        CharSequence id = geocache.getId();
        // TODO: catch errors on open
        mCacheWriter.startWriting();
        mCacheWriter.insertAndUpdateCache(id, geocache.getName(), geocache.getLatitude(), geocache
                .getLongitude(), geocache.getSourceType(), geocache.getSourceName(), geocache
                .getCacheType(), 0, 0, 0);
        mCacheWriter.stopWriting();
    }
}
