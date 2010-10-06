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
import com.google.code.geobeagle.database.filter.ApplyFilterProgressDialog;
import com.google.code.geobeagle.database.filter.ClearFilterProgressDialog;
import com.google.code.geobeagle.database.filter.FilterCleanliness;
import com.google.code.geobeagle.database.filter.HideArchivedCachesProgressDialog;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.ProgressDialog;

public class UpdateFilterMediator {
    private final Provider<ApplyFilterProgressDialog> applyFilterProgressDialogProvider;
    private final CacheListRefresh cacheListRefresh;
    private final Provider<ClearFilterProgressDialog> clearFilterProgressDialogProvider;
    private final UpdateFlag updateFlag;
    private final FilterCleanliness filterCleanliness;
    private final Provider<HideArchivedCachesProgressDialog> hidingArchivedCachesProgressDialogProvider;
    private final Provider<HideWaypointsProgressDialog> hidingWaypointsProgressDialogProvider;

    @Inject
    public UpdateFilterMediator(CacheListRefresh cacheListRefresh,
            UpdateFlag updateFlag,
            Provider<ApplyFilterProgressDialog> applyFilterProgressDialogProvider,
            Provider<ClearFilterProgressDialog> clearFilterProgressDialogProvider,
            Provider<HideArchivedCachesProgressDialog> hidingArchivedCachesProgressDialogProvider,
            Provider<HideWaypointsProgressDialog> hidingWaypointsProgressDialogProvider,
            FilterCleanliness filterCleanliness) {
        this.cacheListRefresh = cacheListRefresh;
        this.updateFlag = updateFlag;
        this.applyFilterProgressDialogProvider = applyFilterProgressDialogProvider;
        this.clearFilterProgressDialogProvider = clearFilterProgressDialogProvider;
        this.hidingWaypointsProgressDialogProvider = hidingWaypointsProgressDialogProvider;
        this.hidingArchivedCachesProgressDialogProvider = hidingArchivedCachesProgressDialogProvider;
        this.filterCleanliness = filterCleanliness;
    }

    public void startFiltering() {
        updateFlag.setUpdatesEnabled(false);
    }

    public void showClearFilterProgress() {
        ProgressDialog progressDialog = clearFilterProgressDialogProvider.get();
        progressDialog.incrementProgressBy(1);
        progressDialog.show();
    }

    void dismissApplyFilterProgress() {
        applyFilterProgressDialogProvider.get().dismiss();
    }

    void dismissClearFilterProgress() {
        clearFilterProgressDialogProvider.get().dismiss();
    }

    void incrementApplyFilterProgress() {
        applyFilterProgressDialogProvider.get().incrementProgressBy(1);
    }

    void showApplyFilterProgress(int max) {
        ApplyFilterProgressDialog applyFilterProgressDialog = applyFilterProgressDialogProvider
                .get();
        applyFilterProgressDialog.setMax(max);
        applyFilterProgressDialog.show();
    }

    public void showHidingArchivedCachesProgress() {
        hidingArchivedCachesProgressDialogProvider.get().show();
    }

    public void showHidingWaypointsProgress() {
        hidingWaypointsProgressDialogProvider.get().show();
    }

    public void dismissHidingWaypointsProgress() {
        hidingWaypointsProgressDialogProvider.get().dismiss();
    }

    public void dismissHidingArchivedCachesProgress() {
        hidingArchivedCachesProgressDialogProvider.get().dismiss();
    }

    public void endFiltering() {
        filterCleanliness.markDirty(false);
        updateFlag.setUpdatesEnabled(true);
        cacheListRefresh.forceRefresh();
    }

}
