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

import com.google.code.geobeagle.OnClickCancelListener;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.Action;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class MenuActionDeleteAllCaches implements Action {
    private final Activity mActivity;
    private final Builder mBuilder;
    private final CacheListRefresh mCacheListRefresh;
    private final Provider<DbFrontend> mDbFrontendProvider;
    private final BCachingStartTime mBcachingLastUpdated;

    @Inject
    public MenuActionDeleteAllCaches(CacheListRefresh cacheListRefresh, Activity activity,
            Provider<DbFrontend> dbFrontendProvider, AlertDialog.Builder builder,
            BCachingStartTime bcachingLastUpdated) {
        mDbFrontendProvider = dbFrontendProvider;
        mBuilder = builder;
        mActivity = activity;
        mCacheListRefresh = cacheListRefresh;
        mBcachingLastUpdated = bcachingLastUpdated;
    }

    @Override
    public void act() {
      buildAlertDialog(mDbFrontendProvider, mCacheListRefresh, mBcachingLastUpdated).show();
    }

    private AlertDialog buildAlertDialog(Provider<DbFrontend> dbFrontendProvider,
            CacheListRefresh cacheListRefresh,
            BCachingStartTime bcachingLastUpdated) {
        mBuilder.setTitle(R.string.delete_all_title);
        final OnClickOkayListener onClickOkayListener = new OnClickOkayListener(mActivity,
                dbFrontendProvider, cacheListRefresh, bcachingLastUpdated);
        final DialogInterface.OnClickListener onClickCancelListener = new OnClickCancelListener();
        mBuilder.setMessage(R.string.confirm_delete_all).setPositiveButton(
                R.string.delete_all_title, onClickOkayListener).setNegativeButton(R.string.cancel,
                onClickCancelListener);
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.setOwnerActivity(mActivity);
        return alertDialog;
    }


}
