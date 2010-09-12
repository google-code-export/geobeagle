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
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.inject.Inject;
import com.google.inject.Provider;

import roboguice.inject.ContextScoped;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.TextView;

@ContextScoped
public class ContextActionDelete implements ContextAction {
    static final String CACHE_TO_DELETE_NAME = "cache-to-delete-name";
    static final String CACHE_TO_DELETE_ID = "cache-to-delete-id";
    public static final int CACHE_LIST_DIALOG_CONFIRM_DELETE = 0;

    static class ContextActionDeleteStore {
        private final SharedPreferences sharedPreferences;

        @Inject
        public ContextActionDeleteStore(SharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
        }

        void saveCacheToDelete(String cacheName, String cacheId) {
            Editor editor = sharedPreferences.edit();
            editor.putString(ContextActionDelete.CACHE_TO_DELETE_ID, cacheId);
            editor.putString(ContextActionDelete.CACHE_TO_DELETE_NAME, cacheName);
            editor.commit();
        }

        String getCacheId() {
            String cacheId = sharedPreferences.getString(
                    ContextActionDelete.CACHE_TO_DELETE_ID, null);
            return cacheId;
        }

        String getCacheName() {
            String cacheName = sharedPreferences.getString(
                    ContextActionDelete.CACHE_TO_DELETE_NAME, null);
            return cacheName;
        }

    }
    public static class ContextActionDeleteDialogHelper {
        private final ContextActionDelete mContextActionDelete;
        private final ContextActionDelete.OnClickOk mOnClickOk;

        @Inject
        public ContextActionDeleteDialogHelper(ContextActionDelete contextActionDelete,
                ContextActionDelete.OnClickOk onClickOk) {
            mContextActionDelete = contextActionDelete;
            mOnClickOk = onClickOk;
        }

        public Dialog onCreateDialog(Builder builder) {
            return builder.setPositiveButton(R.string.delete_cache, mOnClickOk).create();
        }

        public void onPrepareDialog(Dialog dialog) {
            CharSequence confirmDeleteTitle = mContextActionDelete.getConfirmDeleteTitle();
            dialog.setTitle(confirmDeleteTitle);
            TextView textView = (TextView)dialog.findViewById(R.id.delete_cache);
            textView.setText(mContextActionDelete.getConfirmDeleteBodyText());
        }
    }

    public static class OnClickOk implements OnClickListener {
        private final ContextActionDelete mContextActionDelete;

        @Inject
        public OnClickOk(ContextActionDelete contextActionDelete) {
            mContextActionDelete = contextActionDelete;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            mContextActionDelete.delete();
            dialog.dismiss();
        }
    }

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
        mContextActionDeleteStore.saveCacheToDelete(cacheName, cacheId);
        mActivity.showDialog(CACHE_LIST_DIALOG_CONFIRM_DELETE);
    }

    void delete() {
        String cacheId = mContextActionDeleteStore.getCacheId();
        mCacheWriterProvider.get().deleteCache(cacheId);
        mCacheListRefresh.forceRefresh();
    }

    CharSequence getConfirmDeleteBodyText() {
        return String.format(mActivity.getString(R.string.confirm_delete_body_text),
                mContextActionDeleteStore.getCacheId(), mContextActionDeleteStore.getCacheName());
    }

    CharSequence getConfirmDeleteTitle() {
        return String.format(mActivity.getString(R.string.confirm_delete_title),
                mContextActionDeleteStore.getCacheId());
    }
}
