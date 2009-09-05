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

package com.google.code.geobeagle.activity.cachelist.actions.context;

import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.CacheWriterFactory;
import com.google.code.geobeagle.database.ISQLiteDatabase;

import android.widget.BaseAdapter;

public class ContextActionDelete implements ContextAction {
    private final CacheWriterFactory mCacheWriterFactory;
    private final BaseAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;
    private final TitleUpdater mTitleUpdater;
    private ISQLiteDatabase mWritableDatabase;

    public ContextActionDelete(CacheWriterFactory cacheWriterFactory,
            BaseAdapter geocacheListAdapter, GeocacheVectors geocacheVectors,
            TitleUpdater titleUpdater, ISQLiteDatabase writableDatabase) {
        mCacheWriterFactory = cacheWriterFactory;
        mGeocacheListAdapter = geocacheListAdapter;
        mGeocacheVectors = geocacheVectors;
        mTitleUpdater = titleUpdater;
        mWritableDatabase = writableDatabase;
    }

    public void act(int position) {
        final CacheWriter cacheWriter = mCacheWriterFactory.create(mWritableDatabase);

        cacheWriter.deleteCache(mGeocacheVectors.get(position).getId());

        mGeocacheVectors.remove(position);
        mGeocacheListAdapter.notifyDataSetChanged();
        mTitleUpdater.update();
    }
}
