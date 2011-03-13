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

package com.google.code.geobeagle.activity.cachelist.actions.context;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.actions.context.delete.ContextActionDeleteStore;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.CacheSqlWriter;
import com.google.inject.Inject;
import com.google.inject.Provider;

import roboguice.inject.ContextScoped;

import android.app.Activity;

@ContextScoped
public class ContextActionDelete implements ContextAction {
    private final Activity activity;
    private final Provider<CacheSqlWriter> cacheWriterProvider;
    private final GeocacheVectors geocacheVectors;
    private final CacheListRefresh cacheListRefresh;
    private final ContextActionDeleteStore contextActionDeleteStore;

    @Inject
    public ContextActionDelete(GeocacheVectors geocacheVectors,
            Provider<CacheSqlWriter> cacheWriterProvider,
            Activity activity,
            ContextActionDeleteStore contextActionDeleteStore,
            CacheListRefresh cacheListRefresh) {
        this.geocacheVectors = geocacheVectors;
        this.cacheWriterProvider = cacheWriterProvider;
        this.activity = activity;
        this.contextActionDeleteStore = contextActionDeleteStore;
        this.cacheListRefresh = cacheListRefresh;
    }

    @Override
    public void act(int position) {
        GeocacheVector geocacheVector = geocacheVectors.get(position);
        String cacheName = geocacheVector.getName().toString();
        String cacheId = geocacheVector.getId().toString();
        contextActionDeleteStore.saveCacheToDelete(cacheId, cacheName);
        activity.showDialog(R.id.delete_cache);
    }

    public void delete() {
        String cacheId = contextActionDeleteStore.getCacheId();
        cacheWriterProvider.get().deleteCache(cacheId);
        cacheListRefresh.forceRefresh();
    }

    public CharSequence getConfirmDeleteBodyText() {
        return String.format(activity.getString(R.string.confirm_delete_body_text),
                contextActionDeleteStore.getCacheId(), contextActionDeleteStore.getCacheName());
    }

    public CharSequence getConfirmDeleteTitle() {
        return String.format(activity.getString(R.string.confirm_delete_title),
                contextActionDeleteStore.getCacheId());
    }
}
