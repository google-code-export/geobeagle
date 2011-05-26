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

package com.google.code.geobeagle.activity.cachelist.actions.menu;

import com.google.code.geobeagle.actions.Action;
import com.google.code.geobeagle.xmlimport.CacheSyncer;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import android.util.Log;

@Singleton
public class MenuActionSyncGpx implements Action {
    private CacheSyncer cacheSyncer;
    private final Provider<CacheSyncer> cacheSyncerProvider;
    private boolean syncInProgress;

    @Inject
    public MenuActionSyncGpx(Injector injector) {
        cacheSyncerProvider = injector.getProvider(CacheSyncer.class);
        syncInProgress = false;
    }

    // For testing.
    public MenuActionSyncGpx(Provider<CacheSyncer> gpxImporterProvider) {
        cacheSyncerProvider = gpxImporterProvider;
        syncInProgress = false;
    }

    public void abort() {
        Log.d("GeoBeagle", "MenuActionSyncGpx aborting: " + syncInProgress);
        if (!syncInProgress)
            return;
        cacheSyncer.abort();
        syncInProgress = false;
    }

    @Override
    public void act() {
        Log.d("GeoBeagle", "MenuActionSync importing");
        cacheSyncer = cacheSyncerProvider.get();
        cacheSyncer.syncGpxs();
        syncInProgress = true;
    }
}
