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

package com.google.code.geobeagle.database.filter;

import com.google.code.geobeagle.activity.cachelist.presenter.filter.UpdateFilterHandler;
import com.google.code.geobeagle.activity.preferences.EditPreferences;
import com.google.code.geobeagle.database.FoundCaches;
import com.google.code.geobeagle.database.TagReader;
import com.google.inject.Inject;

import roboguice.util.RoboThread;

import android.content.SharedPreferences;

public class UpdateFilterWorker extends RoboThread {
    private final UpdateFilterHandler updateFilterHandler;
    private final TagReader tagReader;
    private final SharedPreferences sharedPreferences;
    private final CacheVisibilityStore cacheVisibilityStore;

    @Inject
    public UpdateFilterWorker(SharedPreferences sharedPreferences,
            UpdateFilterHandler updateFilterHandler,
            TagReader tagReader,
            CacheVisibilityStore cacheVisibilityStore) {
        this.updateFilterHandler = updateFilterHandler;
        this.tagReader = tagReader;
        this.sharedPreferences = sharedPreferences;
        this.cacheVisibilityStore = cacheVisibilityStore;
    }

    @Override
    public void run() {
        cacheVisibilityStore.setAllVisible();

        if (sharedPreferences.getBoolean(EditPreferences.SHOW_FOUND_CACHES, false)) {
            updateFilterHandler.dismissClearFilterProgress();
            return;
        }

        hideFoundCaches();
        updateFilterHandler.dismissApplyFilterProgress();
    }

    private void hideFoundCaches() {
        FoundCaches foundCaches = null;
        try {
            foundCaches = tagReader.getFoundCaches();
            updateFilterHandler.showApplyFilterProgress(foundCaches.getCount());

            for (String cache : foundCaches.getCaches()) {
                updateFilterHandler.incrementApplyFilterProgress();
                cacheVisibilityStore.setInvisible(cache);
            }
        } finally {
            if (foundCaches != null)
                foundCaches.close();
        }
    }
}
