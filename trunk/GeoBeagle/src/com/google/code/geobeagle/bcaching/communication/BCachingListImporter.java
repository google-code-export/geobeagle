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

import com.google.code.geobeagle.bcaching.BCachingAnnotations.CacheListAnnotation;
import com.google.inject.Inject;

import java.util.Hashtable;

public class BCachingListImporter {
    static final String MAX_COUNT = "50";

    private final BCachingListImportHelper bCachingListImportHelper;
    private final Hashtable<String, String> params;
    private String startTime;

    public static class BCachingListImporter2 {
        BCachingListImporter bcachingListImporter;
        private BCachingList bcachingList;

        BCachingListImporter2(BCachingListImporter bcachingListImporter) {
            this.bcachingListImporter = bcachingListImporter;
        }

        public BCachingList getCacheList(String startPosition) throws BCachingException {
            bcachingList = bcachingListImporter.getCacheList(startPosition);
            return bcachingList;
        }
        
        public int getTotalCount() throws BCachingException {
            return bcachingListImporter.getTotalCount();
        }
        
        public void setStartTime(String startTime) {
            bcachingListImporter.setStartTime(startTime);
        }
    }

    @Inject
    BCachingListImporter(@CacheListAnnotation Hashtable<String, String> params,
            BCachingListImportHelper bCachingListImportHelper) {
        this.params = params;
        this.bCachingListImportHelper = bCachingListImportHelper;
    }

    private BCachingList getCacheList(String maxCount, String startingPosition)
            throws BCachingException {
        params.put("maxcount", maxCount);
        params.put("since", startingPosition);
        return bCachingListImportHelper.importList(params);
    }

    public BCachingList getCacheList(String startPosition) throws BCachingException {
        params.put("first", startPosition);
        return getCacheList(MAX_COUNT, startTime);
    }

    public int getTotalCount() throws BCachingException {
        params.remove("first");
        return getCacheList("1", startTime).getTotalCount();
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
