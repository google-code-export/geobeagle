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
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker.ImportBCachingWorkerFactory;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.inject.Inject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

public class MenuActionSyncBCaching extends MenuActionBase {

    private Handler handler;
    private final Activity activity;
    private final ImportBCachingWorkerFactory importBCachingWorkerFactory;

    @Inject
    public MenuActionSyncBCaching(Activity activity,
            ImportBCachingWorkerFactory importBCachingWorkerFactory) {
        super(R.string.menu_sync_bcaching);
        this.activity = activity;
        this.importBCachingWorkerFactory = importBCachingWorkerFactory;
    }

    @Override
    public void act() {
        ProgressDialog myProgressDialog = new ProgressDialog(activity);
        myProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        handler = new ProgressHandler(myProgressDialog);
        importBCachingWorkerFactory.create(handler).start();
    }
}
