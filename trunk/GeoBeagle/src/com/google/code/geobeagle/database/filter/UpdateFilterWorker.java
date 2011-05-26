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
import com.google.code.geobeagle.activity.preferences.Preferences;
import com.google.inject.Inject;

import roboguice.util.RoboThread;

import android.content.SharedPreferences;

public class UpdateFilterWorker extends RoboThread {
    private final UpdateFilterHandler updateFilterHandler;
    private final SharedPreferences sharedPreferences;
    private final CacheVisibilityStore cacheVisibilityStore;

    @Inject
    public UpdateFilterWorker(SharedPreferences sharedPreferences,
            UpdateFilterHandler updateFilterHandler,
            CacheVisibilityStore cacheVisibilityStore) {
        this.updateFilterHandler = updateFilterHandler;
        this.sharedPreferences = sharedPreferences;
        this.cacheVisibilityStore = cacheVisibilityStore;
    }

    @Override
    public void run() {
        cacheVisibilityStore.setAllVisible();

        if (!sharedPreferences.getBoolean(Preferences.SHOW_WAYPOINTS, false)) {
            updateFilterHandler.setProgressMessage("Filtering waypoints");
            cacheVisibilityStore.hideWaypoints();
        }

        if (!sharedPreferences.getBoolean(Preferences.SHOW_UNAVAILABLE_CACHES, false)) {
            updateFilterHandler.setProgressMessage("Filtering unavailable caches");
            cacheVisibilityStore.hideUnavailableCaches();
        }

        boolean showFound = sharedPreferences.getBoolean(Preferences.SHOW_FOUND_CACHES, false);
        boolean showDnf = sharedPreferences.getBoolean(Preferences.SHOW_DNF_CACHES, true);
        if (!showFound || !showDnf) {
            updateFilterHandler.setProgressMessage("Filtering found/dnf caches");
            cacheVisibilityStore.hideFoundCaches(!showFound, !showDnf);
        }

        updateFilterHandler.endFiltering();
    }
}
