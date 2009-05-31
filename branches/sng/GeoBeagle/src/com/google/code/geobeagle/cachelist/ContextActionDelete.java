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

package com.google.code.geobeagle.cachelist;

import com.google.code.geobeagle.cachelist.CacheListRefresh.TitleUpdater;
import com.google.code.geobeagle.xmlimport.CacheWriter;

import android.widget.BaseAdapter;

public class ContextActionDelete implements ContextAction {
    private final CacheWriter mCacheWriter;
    private final BaseAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;
    private final TitleUpdater mTitleUpdater;

    ContextActionDelete(BaseAdapter geocacheListAdapter, CacheWriter cacheWriter,
            GeocacheVectors geocacheVectors, TitleUpdater titleUpdater) {
        mGeocacheVectors = geocacheVectors;
        mCacheWriter = cacheWriter;
        mGeocacheListAdapter = geocacheListAdapter;
        mTitleUpdater = titleUpdater;
    }

    public void act(int position) {
        mCacheWriter.deleteCache(mGeocacheVectors.get(position).getId());

        mGeocacheVectors.remove(position);
        mGeocacheListAdapter.notifyDataSetChanged();
        mTitleUpdater.update();
    }
}
