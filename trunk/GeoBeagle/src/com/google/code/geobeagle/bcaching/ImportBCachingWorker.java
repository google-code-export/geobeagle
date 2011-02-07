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

package com.google.code.geobeagle.bcaching;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.inject.Inject;
import com.google.inject.Injector;

import roboguice.inject.ContextScoped;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * Thread class that does the work of importing caches from the bcaching.com
 * site.
 *
 * @author sng
 */
@ContextScoped
public class ImportBCachingWorker {
    private final CacheImporter cacheImporter;
    private final CacheListCursor cursor;
    private boolean inProgress;
    private final ProgressHandler progressHandler;
    private final ProgressManager progressManager;
    private final SharedPreferences sharedPreferences;
    private final UpdateFlag updateFlag;

    @Inject
    public ImportBCachingWorker(Injector injector) {
        this.progressHandler = injector.getInstance(ProgressHandler.class);
        injector.getInstance(ErrorDisplayer.class);
        this.progressManager = injector.getInstance(ProgressManager.class);
        this.cacheImporter = injector.getInstance(CacheImporter.class);
        this.cursor = injector.getInstance(CacheListCursor.class);
        this.sharedPreferences = injector.getInstance(SharedPreferences.class);
        this.updateFlag = injector.getInstance(UpdateFlag.class);
    }

    public ImportBCachingWorker(ProgressHandler progressHandler,
            ProgressManager progressManager,
            CacheImporter cacheImporter,
            CacheListCursor cacheListCursor,
            SharedPreferences sharedPreferences,
            UpdateFlag updateFlag) {
        this.progressHandler = progressHandler;
        this.progressManager = progressManager;
        this.cacheImporter = cacheImporter;
        this.cursor = cacheListCursor;
        this.sharedPreferences = sharedPreferences;
        this.updateFlag = updateFlag;
    }

    /*
     * Abort the import thread. This call will block
     */

    public synchronized boolean inProgress() {
        return inProgress;
    }

    public void sync() throws CancelException, BCachingException {
        if (!sharedPreferences.getBoolean(BCachingModule.BCACHING_ENABLED, false))
            return;

        updateFlag.setUpdatesEnabled(false);
        Log.d("GeoBeagle", "Starting import");
        inProgress = true;
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        try {
            if (!cursor.open())
                return;
            while (cursor.readCaches()) {
                cacheImporter.load(cursor.getCacheIds());
                cursor.increment();
            }
            cursor.close();
        } finally {
            updateFlag.setUpdatesEnabled(true);
            progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
            progressManager.update(progressHandler, ProgressMessage.DONE, 0);
            inProgress = false;
        }
    }
}
