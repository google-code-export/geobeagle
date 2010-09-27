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
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.xmlimport.GpxImporter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import android.util.Log;

@Singleton
public class MenuActionSyncGpx implements Action {
    private Abortable mBCachingWorkerAborter;
    private GpxImporter mGpxImporter;
    private final Provider<GpxImporter> mGpxImporterProvider;
    private final Provider<ImportBCachingWorker> mImportBCachingWorkerProvider;
    private boolean mSyncInProgress;

    @Inject
    public MenuActionSyncGpx(Injector injector) {
        mGpxImporterProvider = injector.getProvider(GpxImporter.class);
        mImportBCachingWorkerProvider = injector.getProvider(ImportBCachingWorker.class);
        mSyncInProgress = false;
    }

    // For testing.
    public MenuActionSyncGpx(Provider<ImportBCachingWorker> importBCachingWorkerProvider,
            Provider<GpxImporter> gpxImporterProvider) {
        mImportBCachingWorkerProvider = importBCachingWorkerProvider;
        mGpxImporterProvider = gpxImporterProvider;
        mSyncInProgress = false;
    }

    public void abort() {
        Log.d("GeoBeagle", "MenuActionSyncGpx aborting: " + mSyncInProgress);
        if (!mSyncInProgress)
            return;
        mGpxImporter.abort();
        mBCachingWorkerAborter.abort();
        mSyncInProgress = false;
    }

    @Override
    public void act() {
        Log.d("GeoBeagle", "MenuActionSync importing");
        mGpxImporter = mGpxImporterProvider.get();
        mBCachingWorkerAborter = mImportBCachingWorkerProvider.get();
        mGpxImporter.importGpxs();
        mSyncInProgress = true;
    }
}
