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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.CacheWriterFactory;
import com.google.code.geobeagle.database.ISQLiteDatabase;

public class ContextActionDeleteFactory {

    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocacheVectors mGeocacheVectors;
    private final CacheWriterFactory mCacheWriterFactory;

    public ContextActionDeleteFactory(CacheWriterFactory cacheWriterFactory,
            GeocacheListAdapter geocacheListAdapter, GeocacheVectors geocacheVectors) {
        mGeocacheListAdapter = geocacheListAdapter;
        mGeocacheVectors = geocacheVectors;
        mCacheWriterFactory = cacheWriterFactory;
    }

    public ContextActionDelete create(TitleUpdater titleUpdater, ISQLiteDatabase writableDatabase) {
        return new ContextActionDelete(mCacheWriterFactory, mGeocacheListAdapter, mGeocacheVectors,
                titleUpdater, writableDatabase);
    }
}
