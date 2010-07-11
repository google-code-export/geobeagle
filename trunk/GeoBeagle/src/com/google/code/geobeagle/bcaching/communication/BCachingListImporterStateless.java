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

import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.inject.Inject;

import java.util.Hashtable;

public class BCachingListImporterStateless {
    static final String MAX_COUNT = "50";

    private final BCachingListImportHelper bCachingListImportHelper;
    private static Hashtable<String, String> params;
    static {
        params = new Hashtable<String, String>();
        params.put("a", "list");
        params.put("found", "0");
        BCachingModule.commonParams(params);
    }

    @Inject
    public BCachingListImporterStateless(BCachingListImportHelper bCachingListImportHelper) {
        this.bCachingListImportHelper = bCachingListImportHelper;
    }

    private BCachingList importList(String maxCount, String startTime) throws BCachingException {
        params.put("maxcount", maxCount);
        params.put("since", startTime);
        return bCachingListImportHelper.importList(params);
    }

    public BCachingList getCacheList(String startPosition, String startTime)
            throws BCachingException {
        params.put("first", startPosition);
        return importList(MAX_COUNT, startTime);
    }

    public int getTotalCount(String startTime) throws BCachingException {
        params.remove("first");
        BCachingList importList = importList("1", startTime);
        return importList.getTotalCount();
    }
}
