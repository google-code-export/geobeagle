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

import android.content.SharedPreferences;

import java.util.Hashtable;

public class BCachingListImporterStateless {
    static final String MAX_COUNT = "50";

    private final BCachingListImportHelper bCachingListImportHelper;
    private final Hashtable<String, String> params;

    @Inject
    BCachingListImporterStateless(BCachingListImportHelper bCachingListImportHelper,
            SharedPreferences sharedPreferences) {
        this.bCachingListImportHelper = bCachingListImportHelper;
        params = new Hashtable<String, String>();
        params.put("a", "list");
        boolean syncFinds = sharedPreferences.getBoolean("bcaching-sync-finds", false);
        params.put("found", syncFinds ? "2" : "0"); // 0-no, 1-yes, 2-both
        commonParams(params);
    }

    // For testing.
    BCachingListImporterStateless(Hashtable<String, String> params,
            BCachingListImportHelper bCachingListImportHelper) {
        this.bCachingListImportHelper = bCachingListImportHelper;
        this.params = params;
    }

    private BCachingList importList(String maxCount, String startTime) throws BCachingException {
        params.put("maxcount", maxCount);
        params.put("since", startTime);
//         params.put("since", "1298740607924");
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

    public static void commonParams(Hashtable<String, String> params) {
        params.put("lastuploaddays", "7");
        params.put("app", "GeoBeagle");
        params.put("timeAsLong", "1");
        params.put("own", "2");
    }
}
