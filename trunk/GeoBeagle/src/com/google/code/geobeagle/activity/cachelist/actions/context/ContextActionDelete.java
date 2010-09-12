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
            final TextView textView = (TextView)dialog.findViewById(R.id.delete_cache);
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
    private final SharedPreferences mSharedPreferences;
    private final CacheListRefresh mCacheListRefresh;

    @Inject
    public ContextActionDelete(GeocacheVectors geocacheVectors,
            Provider<CacheWriter> cacheWriterProvider,
            Activity activity,
            SharedPreferences sharedPreferences,
            CacheListRefresh cacheListRefresh) {
        mGeocacheVectors = geocacheVectors;
        mCacheWriterProvider = cacheWriterProvider;
        mActivity = activity;
        mSharedPreferences = sharedPreferences;
        mCacheListRefresh = cacheListRefresh;
    }

    @Override
    public void act(int position) {
        Editor editor = mSharedPreferences.edit();
        GeocacheVector geocacheVector = mGeocacheVectors.get(position);
        editor.putString(CACHE_TO_DELETE_ID, geocacheVector.getId().toString());
        editor.putString(CACHE_TO_DELETE_NAME, geocacheVector.getName().toString());
        editor.commit();
        mActivity.showDialog(CACHE_LIST_DIALOG_CONFIRM_DELETE);
    }

    void delete() {
        mCacheWriterProvider.get().deleteCache(
                mSharedPreferences.getString(CACHE_TO_DELETE_ID, null));
        mCacheListRefresh.forceRefresh();
    }

    public CharSequence getConfirmDeleteBodyText() {
        return String.format(mActivity.getString(R.string.confirm_delete_body_text),
                mSharedPreferences.getString(CACHE_TO_DELETE_ID, null),
                mSharedPreferences.getString(CACHE_TO_DELETE_NAME, null));
    }

    public CharSequence getConfirmDeleteTitle() {
        return String.format(mActivity.getString(R.string.confirm_delete_title),
                mSharedPreferences.getString(CACHE_TO_DELETE_ID, null));
    }
}
