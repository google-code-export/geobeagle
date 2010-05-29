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

package com.google.code.geobeagle.bcaching.communication;

import com.google.inject.Inject;

public class BCachingListImporter {
    private final BCachingListImporterStateless bcachingListImporterStateless;
    private BCachingList bcachingList;
    private String startTime;
    private long serverTime;

    @Inject
    public BCachingListImporter(BCachingListImporterStateless bcachingListImporterStateless) {
        this.bcachingListImporterStateless = bcachingListImporterStateless;
    }

    public void readCacheList(int startPosition) throws BCachingException {
        bcachingList = bcachingListImporterStateless.getCacheList(String.valueOf(startPosition),
                startTime);
        serverTime = bcachingList.getServerTime();
    }

    public int getTotalCount() throws BCachingException {
        return bcachingListImporterStateless.getTotalCount(startTime);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Must be called after readCacheList().
     */
    public long getServerTime() {
        return serverTime;
    }

    public String getCacheIds() throws BCachingException {
        return bcachingList.getCacheIds();
    }

    public int getCachesRead() throws BCachingException {
        return bcachingList.getCachesRead();
    }

}
