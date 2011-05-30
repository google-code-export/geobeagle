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
    private final Activity activity;
    private final Builder builder;
    private final CacheListRefresh cacheListRefresh;
    private final Provider<DbFrontend> cbFrontendProvider;
    private final BCachingStartTime bcachingLastUpdated;
    private final CompassFrameHider compassFrameHider;

    @Inject
    public MenuActionDeleteAllCaches(CacheListRefresh cacheListRefresh,
            Activity activity,
            Provider<DbFrontend> dbFrontendProvider,
            AlertDialog.Builder builder,
            BCachingStartTime bcachingLastUpdated,
            CompassFrameHider compassFrameHider) {
        this.cbFrontendProvider = dbFrontendProvider;
        this.builder = builder;
        this.activity = activity;
        this.cacheListRefresh = cacheListRefresh;
        this.bcachingLastUpdated = bcachingLastUpdated;
        this.compassFrameHider = compassFrameHider;
    }

    @Override
    public void act() {
        buildAlertDialog(cbFrontendProvider, cacheListRefresh, bcachingLastUpdated,
                compassFrameHider).show();
    }

    private AlertDialog buildAlertDialog(Provider<DbFrontend> dbFrontendProvider,
            CacheListRefresh cacheListRefresh,
            BCachingStartTime bcachingLastUpdated,
            CompassFrameHider compassFrameHider) {
        builder.setTitle(R.string.delete_all_title);
        final OnClickOkayListener onClickOkayListener = new OnClickOkayListener(activity,
                dbFrontendProvider, cacheListRefresh, bcachingLastUpdated, compassFrameHider);
        final DialogInterface.OnClickListener onClickCancelListener = new OnClickCancelListener();
        builder.setMessage(R.string.confirm_delete_all)
                .setPositiveButton(R.string.delete_all_title, onClickOkayListener)
                .setNegativeButton(R.string.cancel, onClickCancelListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOwnerActivity(activity);
        return alertDialog;
    }

}
