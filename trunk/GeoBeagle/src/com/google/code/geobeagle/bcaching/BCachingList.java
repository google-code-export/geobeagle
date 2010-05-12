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

import com.google.code.geobeagle.bcaching.BCachingCommunication.BCachingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

class BCachingList {
    private final JSONObject cacheList;

    BCachingList(JSONObject json) {
        cacheList = json;
    }

    int getCachesRead() throws BCachingException {
        try {
            return cacheList.getJSONArray("data").length();
        } catch (JSONException e) {
            throw new BCachingException("Error parsing server data: " + e.getLocalizedMessage());
        }
    }

    int getTotalCount() throws BCachingException {
        try {
            return cacheList.getInt("totalCount");
        } catch (JSONException e) {
            throw new BCachingException("Error parsing server data: " + e.getLocalizedMessage());
        }
    }

    String getCacheIds() throws BCachingException {
        try {
            JSONArray summary = cacheList.getJSONArray("data");
            Log.d("GeoBeagle", summary.toString());

            StringBuilder csvIds = new StringBuilder();
            int count = summary.length();
            for (int i = 0; i < count; i++) {
                csvIds.append(',');
                JSONObject cacheObject = summary.getJSONObject(i);
                csvIds.append(String.valueOf(cacheObject.getInt("id")));
            }
            if (count > 0)
                csvIds.deleteCharAt(0);
            return csvIds.toString();
        } catch (JSONException e) {
            throw new BCachingException("Error parsing server data: " + e.getLocalizedMessage());
        }
    }
}