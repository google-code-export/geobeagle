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

import com.google.code.geobeagle.bcaching.BCachingLastUpdated.LastReadPosition;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImporter;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.inject.Inject;

class CacheListCursor {
    private final BCachingLastUpdated bcachingLastUpdated;
    private final BCachingListImporter bcachingListImporter;
    private final LastReadPosition lastReadPosition;
    private final ProgressHandler progressHandler;
    private final ProgressManager progressManager;
    private final TimeRecorder timeRecorder;

    @Inject
    CacheListCursor(BCachingLastUpdated bcachingLastUpdated, ProgressManager progressManager,
            ProgressHandler progressHandler, BCachingListImporter bcachingListImporter,
            TimeRecorder timeRecorder, LastReadPosition lastReadPosition) {
        this.bcachingLastUpdated = bcachingLastUpdated;
        this.progressManager = progressManager;
        this.progressHandler = progressHandler;
        this.bcachingListImporter = bcachingListImporter;
        this.timeRecorder = timeRecorder;
        this.lastReadPosition = lastReadPosition;
    }

    void close(String lastModified) {
        timeRecorder.saveTime(lastModified);
        lastReadPosition.put(0);
    }

    String getCacheIds() throws BCachingException {
        return bcachingListImporter.getBCachingList().getCacheIds();
    }

    void increment() throws BCachingException {
        int position = lastReadPosition.get()
                + bcachingListImporter.getBCachingList().getCachesRead();
        lastReadPosition.put(position);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, position);
    }

    boolean open() throws BCachingException {
        bcachingListImporter.setStartTime(String.valueOf(bcachingLastUpdated.getLastUpdateTime()));
//        bcachingListImporter.setStartTime("1274686304000");
//        bcachingListImporter.setStartTime("1274686304000");
        int totalCount = bcachingListImporter.getTotalCount();

        if (totalCount <= 0)
            return false;

        progressManager.update(progressHandler, ProgressMessage.SET_MAX, totalCount);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, lastReadPosition
                .getSaved());
        return true;
    }

    boolean readCaches() throws BCachingException {
        bcachingListImporter.readCacheList(String.valueOf(lastReadPosition.get()));
        
        return bcachingListImporter.getBCachingList().getCachesRead() > 0;
    }

}
