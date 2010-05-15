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
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingList;
import com.google.code.geobeagle.bcaching.communication.BCachingListImporter;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import android.os.Handler;
import android.util.Log;

public class ImportBCachingWorker extends Thread {
    public static interface ImportBCachingWorkerFactory {
        ImportBCachingWorker create(Handler handler);
    }

    public static class ImportBCaching {
        private final ImportBCachingWorkerFactory importBCachingWorkerFactory;
        private final ProgressHandler progressHandler;

        @Inject
        ImportBCaching(ImportBCachingWorkerFactory importBCachingWorkerFactory,
                ProgressHandler progressHandler) {
            this.importBCachingWorkerFactory = importBCachingWorkerFactory;
            this.progressHandler = progressHandler;
        }

        public void importBCaching() {
            importBCachingWorkerFactory.create(progressHandler).start();
        }
    }

    private final Handler handler;
    private final BCachingLastUpdated bcachingLastUpdated;
    private final BCachingListImporter bcachingListImporter;
    private final ErrorDisplayer errorDisplayer;
    private final ProgressManager progressManager;
    private DetailsReaderImport detailsReaderImport;

    @Inject
    public ImportBCachingWorker(@Assisted Handler handler, ProgressManager progressManager,
            BCachingLastUpdated bcachingLastUpdated, BCachingListImporter bcachingListImporter,
            ErrorDisplayer errorDisplayer, DetailsReaderImport detailsReaderImport) {
        this.handler = handler;
        this.bcachingLastUpdated = bcachingLastUpdated;
        this.bcachingListImporter = bcachingListImporter;
        this.errorDisplayer = errorDisplayer;
        this.progressManager = progressManager;
        this.detailsReaderImport = detailsReaderImport;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        progressManager.update(handler, ProgressMessage.START, 0);
        String lastUpdateTime = bcachingLastUpdated.getLastUpdateTime();
        // DetailsReaderImport detailsReaderImport =
        // detailsReaderImportFactory.create(handler);

        try {
            int totalCount = bcachingListImporter.getTotalCount(lastUpdateTime);
            if (totalCount <= 0)
                return;
            progressManager.update(handler, ProgressMessage.SET_MAX, totalCount);
            Log.d("GeoBeagle", "totalCount = " + totalCount);

            int updatedCaches = 0;
            BCachingList bcachingList = bcachingListImporter.getCacheList(updatedCaches,
                    lastUpdateTime);
            int cachesRead;
            while ((cachesRead = bcachingList.getCachesRead()) > 0) {
                // detailsReader.getCacheDetails(bcachingList.getCacheIds(),
                // updatedCaches);
                Log.d("GeoBeagle", "cachesRead: " + cachesRead);
                detailsReaderImport.getCacheDetails(bcachingList.getCacheIds(), progressManager,
                        handler, updatedCaches);

                updatedCaches += cachesRead;
                progressManager.update(handler, ProgressMessage.SET_PROGRESS, updatedCaches);
                bcachingList = bcachingListImporter.getCacheList(updatedCaches, lastUpdateTime);
            }
            bcachingLastUpdated.putLastUpdateTime(now);
        } catch (BCachingException e) {
            errorDisplayer.displayError(R.string.problem_importing_from_bcaching, e.getMessage());
        } finally {
            progressManager.update(handler, ProgressMessage.DONE, 0);
        }
    }

}
