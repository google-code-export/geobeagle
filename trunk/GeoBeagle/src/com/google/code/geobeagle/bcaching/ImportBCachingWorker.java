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
import com.google.code.geobeagle.activity.cachelist.actions.menu.Abortable;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.activity.main.fieldnotes.Toaster;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.inject.Inject;
import com.google.inject.Injector;

import roboguice.inject.ContextScoped;
import roboguice.util.RoboThread;

import android.util.Log;
import android.widget.Toast;

/**
 * Thread class that does the work of importing caches from the bcaching.com
 * site.
 *
 * @author sng
 */
@ContextScoped
public class ImportBCachingWorker extends RoboThread implements Abortable {
    private final CacheImporter cacheImporter;
    private final CacheListCursor cursor;
    private final ErrorDisplayer errorDisplayer;
    private boolean inProgress;
    private final ProgressHandler progressHandler;
    private final ProgressManager progressManager;
    private final Toaster toaster;
    private final UpdateFlag updateFlag;

    @Inject
    public ImportBCachingWorker(Injector injector) {
        this.progressHandler = injector.getInstance(ProgressHandler.class);
        this.errorDisplayer = injector.getInstance(ErrorDisplayer.class);
        this.progressManager = injector.getInstance(ProgressManager.class);
        this.cacheImporter = injector.getInstance(CacheImporter.class);
        this.toaster = injector.getInstance(Toaster.class);
        this.cursor = injector.getInstance(CacheListCursor.class);
        this.updateFlag = injector.getInstance(UpdateFlag.class);
    }

    public ImportBCachingWorker(ProgressHandler progressHandler, ProgressManager progressManager,
            ErrorDisplayer errorDisplayer, CacheImporter cacheImporter,
            Toaster toaster, CacheListCursor cacheListCursor,
            UpdateFlag updateFlag) {
        this.progressHandler = progressHandler;
        this.errorDisplayer = errorDisplayer;
        this.progressManager = progressManager;
        this.cacheImporter = cacheImporter;
        this.toaster = toaster;
        this.cursor = cacheListCursor;
        this.updateFlag = updateFlag;
    }

    /*
     * Abort the import thread. This call will block
     */
    @Override
    public synchronized void abort() {
        if (!inProgress)
            return;
        try {
            Log.d("GeoBeagle", "abort: JOIN STARTED");
            while (inProgress) {
                Log.d("GeoBeagle", "sleeping...");
                Thread.sleep(100);
            }
            toaster.toast(R.string.import_canceled, Toast.LENGTH_LONG);
            Log.d("GeoBeagle", "abort: JOIN FINISHED");
        } catch (InterruptedException e) {
            Log.d("GeoBeagle", "Ignoring InterruptedException: " + e.getLocalizedMessage());
        }
        Log.d("GeoBeagle", "done abort IMPORT");

    }

    public synchronized boolean inProgress() {
        return inProgress;
    }

    @Override
    public void run() {
        Log.d("GeoBeagle", "Starting import");
        inProgress = true;
        updateFlag.setUpdatesEnabled(false);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        try {
            if (!cursor.open())
                return;
            while (cursor.readCaches()) {
                cacheImporter.load(cursor.getCacheIds());
                cursor.increment();
            }
            cursor.close();
        } catch (BCachingException e) {
            errorDisplayer.displayError(R.string.problem_importing_from_bcaching, e
                    .getLocalizedMessage());
        } catch (CancelException e) {
        }
        finally {
            updateFlag.setUpdatesEnabled(true);
            progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
            progressManager.update(progressHandler, ProgressMessage.DONE, 0);
            inProgress = false;
            Log.d("GeoBeagle", "ImportBcachingWorker ending");
        }
    }
}
