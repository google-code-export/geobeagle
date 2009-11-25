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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.RefreshAction;
import com.google.code.geobeagle.database.DbFrontend;

import android.content.res.Resources;

public class CacheActionDelete implements CacheAction {
    private final CacheListAdapter mCacheList;
    private final DbFrontend mDbFrontend;
    private final RefreshAction mCacheListRefresher;
    private final Resources mResources;

    public CacheActionDelete(CacheListAdapter cacheList, RefreshAction cacheListRefresh,
            DbFrontend dbFrontend, Resources resources) {
        mCacheList = cacheList;
        mCacheListRefresher = cacheListRefresh;
        mDbFrontend = dbFrontend;
        mResources = resources;
    }

    @Override
    public void act(Geocache cache) {
        mDbFrontend.getCacheWriter().deleteCache(cache.getId());
        mCacheListRefresher.refresh();  //Reload the cache list from the database
        mCacheList.forceRefresh();
    }

    @Override
    public String getLabel(Geocache geocache) {
        return mResources.getString(R.string.menu_delete_cache);
    }

}
