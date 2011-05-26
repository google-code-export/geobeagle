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

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.DialogInterface;

class OnClickOkayListener implements DialogInterface.OnClickListener {
    private final CacheListRefresh cacheListRefresh;
    private final Provider<DbFrontend> dbFrontendProvider;
    private final BCachingStartTime bcachingLastUpdated;
    private final Activity activity;
    private final CompassFrameHider compassFrameHider;

    OnClickOkayListener(Activity activity,
            Provider<DbFrontend> dbFrontendProvider,
            CacheListRefresh cacheListRefresh,
            BCachingStartTime bcachingLastUpdated,
            CompassFrameHider compassFrameHider) {
        this.activity = activity;
        this.dbFrontendProvider = dbFrontendProvider;
        this.cacheListRefresh = cacheListRefresh;
        this.bcachingLastUpdated = bcachingLastUpdated;
        this.compassFrameHider = compassFrameHider;
    }

    void hideCompassFrame() {
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        dialog.dismiss();
        dbFrontendProvider.get().deleteAll();
        bcachingLastUpdated.clearStartTime();
        cacheListRefresh.forceRefresh();
        compassFrameHider.hideCompassFrame(activity);
    }
}
