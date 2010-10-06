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

package com.google.code.geobeagle.activity.cachelist.presenter.filter;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.database.filter.FilterProgressDialog;
import com.google.code.geobeagle.database.filter.FilterCleanliness;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.ProgressDialog;

public class UpdateFilterMediator {
    private final CacheListRefresh cacheListRefresh;
    private final Provider<FilterProgressDialog> clearFilterProgressDialogProvider;
    private final UpdateFlag updateFlag;
    private final FilterCleanliness filterCleanliness;

    @Inject
    public UpdateFilterMediator(CacheListRefresh cacheListRefresh,
            UpdateFlag updateFlag,
            Provider<FilterProgressDialog> clearFilterProgressDialogProvider,
            FilterCleanliness filterCleanliness) {
        this.cacheListRefresh = cacheListRefresh;
        this.updateFlag = updateFlag;
        this.clearFilterProgressDialogProvider = clearFilterProgressDialogProvider;
        this.filterCleanliness = filterCleanliness;
    }

    public void startFiltering(String string) {
        updateFlag.setUpdatesEnabled(false);
        ProgressDialog progressDialog = clearFilterProgressDialogProvider.get();
        progressDialog.setMessage(string);
        progressDialog.show();
    }

    public void endFiltering() {
        filterCleanliness.markDirty(false);
        updateFlag.setUpdatesEnabled(true);
        cacheListRefresh.forceRefresh();
        ProgressDialog progressDialog = clearFilterProgressDialogProvider.get();
        progressDialog.dismiss();
    }

    public void setProgressMessage(String message) {
        ProgressDialog progressDialog = clearFilterProgressDialogProvider.get();
        progressDialog.setMessage(message);
    }

}
