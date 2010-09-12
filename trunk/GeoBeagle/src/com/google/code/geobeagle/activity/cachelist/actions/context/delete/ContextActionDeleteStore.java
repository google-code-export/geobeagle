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

package com.google.code.geobeagle.activity.cachelist.actions.context.delete;

import com.google.inject.Inject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ContextActionDeleteStore {
    private final SharedPreferences sharedPreferences;
    static final String CACHE_TO_DELETE_NAME = "cache-to-delete-name";
    static final String CACHE_TO_DELETE_ID = "cache-to-delete-id";

    @Inject
    public ContextActionDeleteStore(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveCacheToDelete(String cacheId, String cacheName) {
        Editor editor = sharedPreferences.edit();
        editor.putString(CACHE_TO_DELETE_ID, cacheId);
        editor.putString(CACHE_TO_DELETE_NAME, cacheName);
        editor.commit();
    }

    public String getCacheId() {
        return sharedPreferences.getString(CACHE_TO_DELETE_ID, null);
    }

    public String getCacheName() {
        return sharedPreferences.getString(CACHE_TO_DELETE_NAME, null);
    }

}
