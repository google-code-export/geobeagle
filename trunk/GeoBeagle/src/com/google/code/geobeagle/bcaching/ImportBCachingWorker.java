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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.CacheListModule.ToasterSyncAborted;
import com.google.code.geobeagle.activity.cachelist.actions.menu.Abortable;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;
import com.google.inject.Inject;

import roboguice.util.RoboThread;

import android.util.Log;

public class ImportBCachingWorker extends RoboThread implements Abortable {
    private final CacheListCursor cursor;
    private final DetailsReaderImport detailsReaderImport;
    private final ErrorDisplayer errorDisplayer;
    private boolean inProgress;
    private final ProgressHandler progressHandler;
    private final ProgressManager progressManager;
    private final Toaster toaster;

    @Inject
    public ImportBCachingWorker(ProgressHandler progressHandler, ProgressManager progressManager,
            ErrorDisplayer errorDisplayer, DetailsReaderImport detailsReaderImport,
            @ToasterSyncAborted Toaster toaster, CacheListCursor cacheListCursor) {
        this.progressHandler = progressHandler;
        this.errorDisplayer = errorDisplayer;
        this.progressManager = progressManager;
        this.detailsReaderImport = detailsReaderImport;
        this.toaster = toaster;
        this.cursor = cacheListCursor;
    }

    @Override
    public void abort() {
        if (!inProgress)
            return;
        Log.d("GeoBeagle", "ABORTING IMPORT");
        try {
            Log.d("GeoBeagle", "abort: JOIN STARTED");
            inProgress = false;
            join();
            toaster.showToast();
            Log.d("GeoBeagle", "abort: JOIN FINISHED");
        } catch (InterruptedException e) {
            Log.d("GeoBeagle", "Ignoring InterruptedException: " + e.getLocalizedMessage());
        }
        Log.d("GeoBeagle", "done abort IMPORT");

    }

    @Override
    public void run() {
        inProgress = true;
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        try {
            if (!cursor.open())
                return;

            while (cursor.readCaches()) {
                if (!detailsReaderImport.loadCacheDetails(cursor.getCacheIds())) {
                    return;
                }

                cursor.increment();
            }
            cursor.close();
            progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        } catch (BCachingException e) {
            progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
            errorDisplayer.displayError(R.string.problem_importing_from_bcaching, e
                    .getLocalizedMessage());
        } finally {
            /*
             * Do NOT call refresh here--we might be here as the result of
             * switching activities; a refresh will reference a stale database
             * handle and crash.
             */
            progressManager.update(progressHandler, ProgressMessage.DONE, 0);
            inProgress = false;
            Log.d("GeoBeagle", "ImportBcachingWorker ending");
        }
    }
}
