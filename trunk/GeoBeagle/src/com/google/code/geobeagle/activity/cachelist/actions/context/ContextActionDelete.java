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
import com.google.code.geobeagle.database.CacheWriter;
import com.google.inject.Inject;
import com.google.inject.Provider;

import roboguice.inject.ContextScoped;

import android.app.Activity;

@ContextScoped
public class ContextActionDelete implements ContextAction {
    public static final int CACHE_LIST_DIALOG_CONFIRM_DELETE = 0;

    private final Activity mActivity;
    private final Provider<CacheWriter> mCacheWriterProvider;
    private final GeocacheVectors mGeocacheVectors;
    private final CacheListRefresh mCacheListRefresh;
    private final ContextActionDeleteStore mContextActionDeleteStore;

    @Inject
    public ContextActionDelete(GeocacheVectors geocacheVectors,
            Provider<CacheWriter> cacheWriterProvider,
            Activity activity,
            ContextActionDeleteStore contextActionDeleteStore,
            CacheListRefresh cacheListRefresh) {
        mGeocacheVectors = geocacheVectors;
        mCacheWriterProvider = cacheWriterProvider;
        mActivity = activity;
        mContextActionDeleteStore = contextActionDeleteStore;
        mCacheListRefresh = cacheListRefresh;
    }

    @Override
    public void act(int position) {
        GeocacheVector geocacheVector = mGeocacheVectors.get(position);
        String cacheName = geocacheVector.getName().toString();
        String cacheId = geocacheVector.getId().toString();
        mContextActionDeleteStore.saveCacheToDelete(cacheId, cacheName);
        mActivity.showDialog(CACHE_LIST_DIALOG_CONFIRM_DELETE);
    }

    public void delete() {
        String cacheId = mContextActionDeleteStore.getCacheId();
        mCacheWriterProvider.get().deleteCache(cacheId);
        mCacheListRefresh.forceRefresh();
    }

    public CharSequence getConfirmDeleteBodyText() {
        return String.format(mActivity.getString(R.string.confirm_delete_body_text),
                mContextActionDeleteStore.getCacheId(), mContextActionDeleteStore.getCacheName());
    }

    public CharSequence getConfirmDeleteTitle() {
        return String.format(mActivity.getString(R.string.confirm_delete_title),
                mContextActionDeleteStore.getCacheId());
    }
}
