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

import com.google.code.geobeagle.R;
import com.google.inject.Inject;

import roboguice.inject.ContextScoped;
@ContextScoped
public class FilterNearestCaches {
    private boolean mIsFiltered = true;
    private final WhereFactory mWhereFactories[];

    @Inject
    public FilterNearestCaches(WhereFactoryAllCaches whereFactoryAllCaches,
            WhereFactoryNearestCaches whereFactoryNearestCaches) {
        mWhereFactories = new WhereFactory[] {
                whereFactoryAllCaches, whereFactoryNearestCaches
        };
    }

    public int getMenuString() {
        return mIsFiltered ? R.string.menu_show_all_caches : R.string.menu_show_nearest_caches;
    }

    public int getTitleText() {
        return mIsFiltered ? R.string.cache_list_title : R.string.cache_list_title_all;
    }

    public WhereFactory getWhereFactory() {
        return mWhereFactories[mIsFiltered ? 1 : 0];
    }

    public void toggle() {
        mIsFiltered = !mIsFiltered;
    }
}
