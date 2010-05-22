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

package com.google.code.geobeagle.bcaching.progress;

import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.inject.Inject;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

public class ProgressHandler extends Handler {
    private final ProgressDialog progressDialog;
    private final Refresher refresher;

    @Inject
    public ProgressHandler(ProgressDialog progressDialog, CacheListRefresh refresher) {
        this.progressDialog = progressDialog;
        this.refresher = refresher;
    }

    public void done() {
        progressDialog.dismiss();
        refresher.refresh();
    }

    @Override
    public void handleMessage(Message msg) {
        ProgressMessage progressMessage = ProgressMessage.fromInt(msg.what);
        progressMessage.act(this, msg);
    }

    public void setFile(String filename, int progress) {
        progressDialog.setMessage("Loading: " + filename);
        progressDialog.setProgress(progress);
    }

    public void setMax(int max) {
        progressDialog.setMax(max);
    }

    public void setProgress(int progress) {
        progressDialog.setProgress(progress);
    }

    public void show() {
        progressDialog.show();
    }
}
