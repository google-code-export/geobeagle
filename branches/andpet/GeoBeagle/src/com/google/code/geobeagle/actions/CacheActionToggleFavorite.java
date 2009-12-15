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
package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.DbFrontend;

public class CacheActionToggleFavorite implements CacheAction {
    private final DbFrontend mDbFrontend;
    private final CacheListAdapter mCacheList;
    private final CacheFilterUpdater mCacheFilterUpdater;
    
    public CacheActionToggleFavorite(DbFrontend dbFrontend,
            CacheListAdapter cacheList, CacheFilterUpdater cacheFilterUpdater) {
        mDbFrontend = dbFrontend;
        mCacheList = cacheList;
        mCacheFilterUpdater = cacheFilterUpdater;
    }
    
    @Override
    public void act(Geocache geocache) {
        boolean isFavorite = mDbFrontend.geocacheHasTag(geocache.getId(), 
                Tags.FAVORITES);
        mDbFrontend.setGeocacheTag(geocache.getId(), Tags.FAVORITES, !isFavorite);
        mCacheFilterUpdater.loadActiveFilter();
        mCacheList.forceRefresh();
    }

    @Override
    public String getLabel(Geocache geocache) {
        boolean isFavorite = mDbFrontend.geocacheHasTag(geocache.getId(), 
                Tags.FAVORITES);
        return isFavorite ? "Remove from Favorites" : "Add to Favorites";
    }

}
