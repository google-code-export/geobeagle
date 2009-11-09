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

package com.google.code.geobeagle.activity.cachelist.actions;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.CachesProviderToggler;

import android.content.res.Resources;

public class MenuActionToggleFilter implements MenuAction {
    private final CachesProviderToggler mCachesProviderToggler;
    private final CacheListAdapter mListRefresher;
    private final Resources mResources;

    public MenuActionToggleFilter(CachesProviderToggler cachesProviderToggler,
            CacheListAdapter cacheListRefresh, Resources resources) {
        mCachesProviderToggler = cachesProviderToggler;
        mListRefresher = cacheListRefresh;
        mResources = resources;
    }

    public void act() {
        mCachesProviderToggler.toggle();
        mListRefresher.forceRefresh();
    }

    @Override
    public String getLabel() {
        if (mCachesProviderToggler.isShowingNearest())
            return mResources.getString(R.string.menu_show_all_caches);
        else
            return mResources.getString(R.string.menu_show_nearest_caches);
    }
}
