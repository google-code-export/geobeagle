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

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.inject.Inject;
import com.google.inject.Provider;

import roboguice.util.RoboThread;

import android.content.SharedPreferences;

public class ImportThread extends RoboThread {

    private GpxSyncer gpxSyncer;
    private final GpxSyncerFactory gpxSyncerFactory;
    private final SharedPreferences sharedPreferences;
    private final Provider<ImportBCachingWorker> importBCachingWorkerProvider;
    private ImportBCachingWorker importBCachingWorker;

    @Inject
    ImportThread(GpxSyncerFactory gpxSyncerFactory,
            SharedPreferences sharedPreferences,
            Provider<ImportBCachingWorker> importBCachingWorkerProvider) {
        this.gpxSyncerFactory = gpxSyncerFactory;
        this.sharedPreferences = sharedPreferences;
        this.importBCachingWorkerProvider = importBCachingWorkerProvider;
    }

    @Override
    public void run() {
        gpxSyncer.sync();

        if (sharedPreferences.getBoolean(BCachingModule.BCACHING_ENABLED, false)) {
            importBCachingWorker.run();
        }
    }

    public boolean isAliveHack() {
        return gpxSyncer.isAliveHack();
    }

    public void init() {
        gpxSyncer = gpxSyncerFactory.create();
        importBCachingWorker = importBCachingWorkerProvider.get();
    }
}
