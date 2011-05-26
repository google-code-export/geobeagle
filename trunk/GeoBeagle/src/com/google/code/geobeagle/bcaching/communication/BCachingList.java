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

import android.util.Log;

public class BCachingList {
    private final BCachingJSONObject cacheList;

    BCachingList(BCachingJSONObject cacheList) {
        this.cacheList = cacheList;
    }

    String getCacheIds() throws BCachingException {
        BCachingJSONArray summary = cacheList.getJSONArray("data");
        Log.d("GeoBeagle", summary.toString());

        StringBuilder csvIds = new StringBuilder();
        int count = summary.length();
        for (int i = 0; i < count; i++) {
            csvIds.append(',');
            BCachingJSONObject cacheObject = summary.getJSONObject(i);
            csvIds.append(String.valueOf(cacheObject.getInt("id")));
        }
        if (count > 0)
            csvIds.deleteCharAt(0);
        return csvIds.toString();
    }

    long getServerTime() throws BCachingException {
        return cacheList.getLong("serverTime");
    }

    int getCachesRead() throws BCachingException {
        int length = cacheList.getJSONArray("data").length();
        Log.d("GeoBeagle", "getCachesRead: " + length);

        return length;
    }

    int getTotalCount() throws BCachingException {
        return cacheList.getInt("totalCount");
    }
}
