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


import org.json.JSONException;
import org.json.JSONObject;

public class BCachingJSONObject {
    private final JSONObject jsonObject;

    public BCachingJSONObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public BCachingJSONArray getJSONArray(String key) throws BCachingException {
        try {
            return new BCachingJSONArray(jsonObject.getJSONArray(key));
        } catch (JSONException e) {
            throw new BCachingException(e.getLocalizedMessage());
        }
    }

    public int getInt(String key) throws BCachingException {
        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
            throw new BCachingException(e.getLocalizedMessage());
        }
    }
}