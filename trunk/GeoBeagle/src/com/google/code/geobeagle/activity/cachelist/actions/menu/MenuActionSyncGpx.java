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

package com.google.code.geobeagle.activity.cachelist.actions.menu;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.xmlimport.GpxImporter;
import com.google.inject.Provider;

import android.util.Log;

public class MenuActionSyncGpx extends MenuActionBase {
    private Abortable mAbortable;
    private final CacheListRefresh mCacheListRefresh;
    private final GpxImporterFactory mGpxImporterFactory;
    private final Provider<CacheWriter> mCacheWriterProvider;

    public MenuActionSyncGpx(Abortable abortable, CacheListRefresh cacheListRefresh,
            GpxImporterFactory gpxImporterFactory, Provider<CacheWriter> cacheWriterProvider) {
        super(R.string.menu_sync);
        mAbortable = abortable;
        mCacheListRefresh = cacheListRefresh;
        mGpxImporterFactory = gpxImporterFactory;
        mCacheWriterProvider = cacheWriterProvider;
    }

    public void abort() {
        Log.d("GeoBeagle", "MenuActionSyncGpx aborting");
        mAbortable.abort();
    }

    public void act() {
        final GpxImporter gpxImporter = mGpxImporterFactory.create(mCacheWriterProvider.get());
        mAbortable = gpxImporter;
        gpxImporter.importGpxs(mCacheListRefresh);
    }
}
