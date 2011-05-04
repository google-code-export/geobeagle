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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.inject.Provider;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.DialogInterface;

class OnClickOkayListener implements DialogInterface.OnClickListener {

    private final CacheListRefresh cacheListRefresh;
    private final Provider<DbFrontend> dbFrontendProvider;
    private final BCachingStartTime bcachingLastUpdated;
    private final Activity activity;

    OnClickOkayListener(Activity activity,
            Provider<DbFrontend> dbFrontendProvider,
            CacheListRefresh cacheListRefresh,
            BCachingStartTime bcachingLastUpdated) {
        this.activity = activity;
        this.dbFrontendProvider = dbFrontendProvider;
        this.cacheListRefresh = cacheListRefresh;
        this.bcachingLastUpdated = bcachingLastUpdated;
    }

    void hideCompassFrame() {
        ListActivity listActivity = (ListActivity)activity;
        FragmentManager fragmentManager = listActivity.getFragmentManager();
        Fragment compassFragment = fragmentManager.findFragmentById(R.id.compass_frame);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(compassFragment);
        transaction.commit();
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        dialog.dismiss();
        dbFrontendProvider.get().deleteAll();
        bcachingLastUpdated.clearStartTime();
        cacheListRefresh.forceRefresh();
        hideCompassFrame();
    }
}
