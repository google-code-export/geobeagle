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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImporter;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.bcaching.preferences.LastReadPosition;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.code.geobeagle.xmlimport.SyncCollectingParameter;
import com.google.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Date;

class CacheListCursor {
    private final BCachingStartTime bcachingStartTime;
    private final BCachingListImporter bcachingListImporter;
    private final LastReadPosition lastReadPosition;
    private final ProgressHandler progressHandler;
    private final ProgressManager progressManager;
    private final SimpleDateFormat formatter;

    @Inject
    CacheListCursor(BCachingStartTime bcachingStartTime, ProgressManager progressManager,
            ProgressHandler progressHandler, BCachingListImporter bcachingListImporter,
            LastReadPosition lastReadPosition) {
        this.bcachingStartTime = bcachingStartTime;
        this.progressManager = progressManager;
        this.progressHandler = progressHandler;
        this.bcachingListImporter = bcachingListImporter;
        this.lastReadPosition = lastReadPosition;
        this.formatter = new SimpleDateFormat("MM-dd HH:mm");
    }

    void close() {
        bcachingStartTime.resetStartTime();
        lastReadPosition.put(0);
    }

    String getCacheIds() throws BCachingException {
        return bcachingListImporter.getCacheIds();
    }

    void increment() throws BCachingException {
        int position = lastReadPosition.get();
        if (position == 0) {
            bcachingStartTime.putNextStartTime(bcachingListImporter.getServerTime());
        }

        position += bcachingListImporter.getCachesRead();
        lastReadPosition.put(position);
    }

    boolean open(SyncCollectingParameter syncCollectingParameter) throws BCachingException {
        long serverTime = bcachingStartTime.getLastUpdateTime();
        // bcachingListImporter.setStartTime("1274686304000");
        // bcachingListImporter.setStartTime("1274686304000");
        bcachingListImporter.setStartTime(String.valueOf(serverTime));
        int totalCount = bcachingListImporter.getTotalCount();

        syncCollectingParameter.Log(R.string.sync_message_bcaching_start);
        if (serverTime == 0) {
            syncCollectingParameter.Log("  initial sync");
        } else {
            String longModtime = formatter.format(new Date(serverTime));
            syncCollectingParameter
                    .NestedLog(R.string.sync_message_bcaching_last_sync, longModtime);
        }
        if (totalCount <= 0) {
            syncCollectingParameter.NestedLog(R.string.sync_message_bcaching_synced_caches, 0);
            return false;
        }
        progressManager.update(progressHandler, ProgressMessage.SET_MAX, totalCount);
        lastReadPosition.load();
        int startPosition = lastReadPosition.get();
        progressManager.setCurrentProgress(startPosition);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, startPosition);
        return true;
    }

    int readCaches() throws BCachingException {
        bcachingListImporter.readCacheList(lastReadPosition.get());
        return bcachingListImporter.getCachesRead();
    }
}
