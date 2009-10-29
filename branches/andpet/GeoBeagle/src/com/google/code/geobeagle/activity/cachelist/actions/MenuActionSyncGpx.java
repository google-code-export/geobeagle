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
import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheList;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxImporter;

import android.content.res.Resources;

public class MenuActionSyncGpx implements MenuAction {
    private Abortable mAbortable;
    private final CacheList mCacheList;
    private final GpxImporterFactory mGpxImporterFactory;
    private final DbFrontend mDbFrontend;
    private final Resources mResources;

    public MenuActionSyncGpx(Abortable abortable, CacheList cacheList,
            GpxImporterFactory gpxImporterFactory, DbFrontend dbFrontend,
            Resources resources) {
        mAbortable = abortable;
        mCacheList = cacheList;
        mGpxImporterFactory = gpxImporterFactory;
        mDbFrontend = dbFrontend;
        mResources = resources;
    }

    public void abort() {
        mAbortable.abort();
    }

    @Override
    public void act() {
        final GpxImporter gpxImporter = mGpxImporterFactory.create(mDbFrontend.getCacheWriter());
        mAbortable = gpxImporter;
        gpxImporter.importGpxs(mCacheList);
    }
    
    @Override
    public String getLabel() {
        return mResources.getString(R.string.menu_sync);
    }
}
