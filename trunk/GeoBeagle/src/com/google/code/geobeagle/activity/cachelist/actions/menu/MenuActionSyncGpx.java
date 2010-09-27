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

import com.google.code.geobeagle.actions.Action;
import com.google.code.geobeagle.activity.cachelist.GpxImporterFactory;
import com.google.code.geobeagle.activity.cachelist.NullAbortable;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.xmlimport.GpxImporter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import roboguice.inject.ContextScoped;

import android.util.Log;

@ContextScoped
public class MenuActionSyncGpx implements Action {
    private Abortable mSdcardImportAbortable;
    private final GpxImporterFactory mGpxImporterFactory;
    private final Provider<ImportBCachingWorker> mImportBCachingWorkerProvider;
    private Abortable mBCachingWorkerAborter;
    private final Abortable mNullAbortable;

    public MenuActionSyncGpx(Provider<ImportBCachingWorker> importBCachingWorkerProvider,
            NullAbortable nullAbortable,
            GpxImporterFactory gpxImporterFactory) {
        mNullAbortable = nullAbortable;
        mSdcardImportAbortable = nullAbortable;
        mBCachingWorkerAborter = nullAbortable;
        mGpxImporterFactory = gpxImporterFactory;
        mImportBCachingWorkerProvider = importBCachingWorkerProvider;
    }

    @Inject
    public MenuActionSyncGpx(Injector injector) {
        mNullAbortable = injector.getInstance(NullAbortable.class);
        mSdcardImportAbortable = mNullAbortable;
        mBCachingWorkerAborter = mNullAbortable;
        mGpxImporterFactory = injector.getInstance(GpxImporterFactory.class);
        mImportBCachingWorkerProvider = injector.getProvider(ImportBCachingWorker.class);
    }

    public void abort() {
        Log.d("GeoBeagle", "MenuActionSyncGpx aborting");
        mSdcardImportAbortable.abort();
        mBCachingWorkerAborter.abort();
        mSdcardImportAbortable = mNullAbortable;
        mBCachingWorkerAborter = mNullAbortable;
    }

    @Override
    public void act() {
        Log.d("GeoBeagle", "MenuActionSync importing");
        final GpxImporter gpxImporter = mGpxImporterFactory.create(mCacheWriterProvider.get(),
                mGpxWriterProvider.get());
        mSdcardImportAbortable = gpxImporter;
        mBCachingWorkerAborter = mImportBCachingWorkerProvider.get();
    }
}
