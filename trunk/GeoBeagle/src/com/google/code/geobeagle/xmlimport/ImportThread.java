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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import roboguice.util.RoboThread;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportThread extends RoboThread {

    static class ImportThreadFactory {

        private final GpxSyncerFactory gpxSyncerFactory;
        private final Provider<ImportBCachingWorker> importBCachingWorkerProvider;
        private final ErrorDisplayer errorDisplayer;
        private final BCachingStartTime bcachingStartTime;
        private final DbFrontend dbFrontend;
        private final FileDataVersionChecker fileDataVersionChecker;
        private final SyncCollectingParameter syncCollectingParameter;

        @Inject
        ImportThreadFactory(Injector injector) {
            this.gpxSyncerFactory = injector.getInstance(GpxSyncerFactory.class);
            this.importBCachingWorkerProvider = injector.getProvider(ImportBCachingWorker.class);
            this.errorDisplayer = injector.getInstance(ErrorDisplayer.class);
            this.bcachingStartTime = injector.getInstance(BCachingStartTime.class);
            this.fileDataVersionChecker = injector.getInstance(FileDataVersionChecker.class);
            this.dbFrontend = injector.getInstance(DbFrontend.class);
            this.syncCollectingParameter = injector.getInstance(SyncCollectingParameter.class);
        }

        ImportThread create() {
            return new ImportThread(gpxSyncerFactory.create(), importBCachingWorkerProvider.get(),
                    errorDisplayer, bcachingStartTime, fileDataVersionChecker, dbFrontend,
                    syncCollectingParameter);
        }
    }

    private final GpxSyncer gpxSyncer;
    private final ImportBCachingWorker importBCachingWorker;
    private boolean isAlive;
    private final ErrorDisplayer errorDisplayer;
    private final BCachingStartTime bcachingStartTime;
    private final DbFrontend dbFrontend;
    private final FileDataVersionChecker fileDataVersionChecker;
    private final SyncCollectingParameter syncCollectingParameter;

    ImportThread(GpxSyncer gpxSyncer,
            ImportBCachingWorker importBCachingWorker,
            ErrorDisplayer errorDisplayer,
            BCachingStartTime bcachingStartTime,
            FileDataVersionChecker fileDataVersionChecker,
            DbFrontend dbFrontend,
            SyncCollectingParameter syncCollectingParameter) {
        this.gpxSyncer = gpxSyncer;
        this.importBCachingWorker = importBCachingWorker;
        this.errorDisplayer = errorDisplayer;
        this.bcachingStartTime = bcachingStartTime;
        this.fileDataVersionChecker = fileDataVersionChecker;
        this.dbFrontend = dbFrontend;
        this.syncCollectingParameter = syncCollectingParameter;
    }

    @Override
    public void run() {
        isAlive = true;
        try {
            if (fileDataVersionChecker.needsUpdating()) {
                dbFrontend.forceUpdate();
                bcachingStartTime.clearStartTime();
            }
            syncCollectingParameter.reset();
            gpxSyncer.sync(syncCollectingParameter);
            importBCachingWorker.sync(syncCollectingParameter);
            errorDisplayer.displayError(R.string.string, syncCollectingParameter.getLog());
        } catch (final FileNotFoundException e) {
            errorDisplayer.displayError(R.string.error_opening_file, e.getMessage());
            return;
        } catch (IOException e) {
            errorDisplayer.displayError(R.string.error_reading_file, e.getMessage());
            return;
        } catch (ImportException e) {
            errorDisplayer.displayError(e.getError(), e.getPath());
            return;
        } catch (BCachingException e) {
            errorDisplayer.displayError(R.string.problem_importing_from_bcaching,
                    e.getLocalizedMessage());
        } catch (CancelException e) {
            // Toast can't be displayed in this thread; it must be displayed in
            // main UI thread.
            return;
        } finally {
            Log.d("GeoBeagle", "<<< Syncing");
            isAlive = false;
        }
    }

    public boolean isAliveHack() {
        return isAlive;
    }

}
