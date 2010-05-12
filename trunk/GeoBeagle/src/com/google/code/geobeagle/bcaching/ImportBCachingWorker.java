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
import com.google.code.geobeagle.bcaching.BCachingCommunication.BCachingException;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;


import android.os.Handler;
import android.util.Log;


public class ImportBCachingWorker extends Thread {
    public static interface ImportBCachingWorkerFactory {
        ImportBCachingWorker create(Handler handler);
    }

    private final Handler handler;
    private final BCachingLastUpdated bcachingLastUpdated;
    private final BCachingListFactory bcachingListFactory;
    private final ErrorDisplayer errorDisplayer;
    private final ProgressManager progressManager;
    private final DetailsReader detailsReader;

    @Inject
    public ImportBCachingWorker(@Assisted Handler handler, ProgressManager progressManager,
            BCachingLastUpdated bcachingLastUpdated, BCachingListFactory bcachingListFactory,
            ErrorDisplayer errorDisplayer, DetailsReader detailsReader) {
        this.handler = handler;
        this.bcachingLastUpdated = bcachingLastUpdated;
        this.bcachingListFactory = bcachingListFactory;
        this.errorDisplayer = errorDisplayer;
        this.progressManager = progressManager;
        this.detailsReader = detailsReader;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        progressManager.update(handler, ProgressMessage.START, 0);
        String lastUpdateTime = bcachingLastUpdated.getLastUpdateTime();

        try {
            int totalCount = bcachingListFactory.getTotalCount(lastUpdateTime);
            if (totalCount <= 0)
                return;
            progressManager.update(handler, ProgressMessage.SET_MAX, totalCount);
            Log.d("GeoBeagle", "totalCount = " + totalCount);

            int updatedCaches = 0;
            BCachingList bcachingList = bcachingListFactory.getCacheList(updatedCaches, now);
            int cachesRead;
            while ((cachesRead = bcachingList.getCachesRead()) > 0) {
                detailsReader.getCacheDetails(bcachingList.getCacheIds(), updatedCaches);

                updatedCaches += cachesRead;
                progressManager.update(handler, ProgressMessage.SET_PROGRESS, updatedCaches);
                bcachingList = bcachingListFactory.getCacheList(updatedCaches, now);
            }
        } catch (BCachingException e) {
            errorDisplayer.displayError(R.string.problem_importing_from_bcaching, e.getMessage());
            e.printStackTrace();
        } finally {
            progressManager.update(handler, ProgressMessage.DONE, 0);
            Log.d("GeoBeagle", "Setting bcaching_lastupdate to " + now);
            bcachingLastUpdated.putLastUpdateTime(now);
        }
    }

}
