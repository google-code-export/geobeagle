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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.R.id;
import com.google.code.geobeagle.SuggestionProvider;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.actions.context.delete.ContextActionDeleteDialogHelper;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListPresenter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;

public class CacheListDelegate {
    private final ActivitySaver activitySaver;
    private final ActivityVisible activityVisible;
    private final CacheListRefresh cacheListRefresh;
    private final GeocacheListController controller;
    private final Provider<DbFrontend> dbFrontendProvider;
    private final ImportIntentManager importIntentManager;
    private final CacheListPresenter presenter;
    private final LogFindDialogHelper logFindDialogHelper;
    private final ContextActionDeleteDialogHelper contextActionDeleteDialogHelper;
    private final Activity activity;

    public CacheListDelegate(ImportIntentManager importIntentManager,
            ActivitySaver activitySaver,
            CacheListRefresh cacheListRefresh,
            GeocacheListController geocacheListController,
            CacheListPresenter cacheListPresenter,
            Provider<DbFrontend> dbFrontendProvider,
            ActivityVisible activityVisible,
            LogFindDialogHelper logFindDialogHelper,
            ContextActionDeleteDialogHelper contextActionDeleteDialogHelper,
            Activity activity) {
        this.activitySaver = activitySaver;
        this.cacheListRefresh = cacheListRefresh;
        this.controller = geocacheListController;
        this.presenter = cacheListPresenter;
        this.importIntentManager = importIntentManager;
        this.dbFrontendProvider = dbFrontendProvider;
        this.activityVisible = activityVisible;
        this.logFindDialogHelper = logFindDialogHelper;
        this.contextActionDeleteDialogHelper = contextActionDeleteDialogHelper;
        this.activity = activity;
    }

    @Inject
    public CacheListDelegate(Injector injector) {
        this.activitySaver = injector.getInstance(ActivitySaver.class);
        this.cacheListRefresh = injector.getInstance(CacheListRefresh.class);
        this.controller = injector.getInstance(GeocacheListController.class);
        this.presenter = injector.getInstance(CacheListPresenter.class);
        this.importIntentManager = injector.getInstance(ImportIntentManager.class);
        this.dbFrontendProvider = injector.getProvider(DbFrontend.class);
        this.activityVisible = injector.getInstance(ActivityVisible.class);
        this.logFindDialogHelper = injector.getInstance(LogFindDialogHelper.class);
        this.contextActionDeleteDialogHelper = injector
                .getInstance(ContextActionDeleteDialogHelper.class);
        this.activity = injector.getInstance(Activity.class);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        return controller.onContextItemSelected(menuItem);
    }

    public void onCreate(Injector injector) {
        InflatedGpsStatusWidget inflatedGpsStatusWidget = injector
                .getInstance(InflatedGpsStatusWidget.class);
        GpsStatusWidgetDelegate gpsStatusWidgetDelegate = injector
                .getInstance(GpsStatusWidgetDelegate.class);
        presenter.onCreate();
        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);
    }

    public void onCreateFragment(Object cacheListFragment) {
        presenter.onCreateFragment(cacheListFragment);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return controller.onCreateOptionsMenu(menu);
    }

    public void onListItemClick(int position) {
        controller.onListItemClick(position);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return controller.onOptionsItemSelected(item);
    }

    public void onPause() {
        activityVisible.setVisible(false);
        presenter.onPause();
        controller.onPause();
        activitySaver.save(ActivityType.CACHE_LIST);
        dbFrontendProvider.get().closeDatabase();
    }

    public void onResume(SearchTarget searchTarget) {
        search(activity, searchTarget);

        activityVisible.setVisible(true);
        presenter.onResume(cacheListRefresh);
        controller.onResume(importIntentManager.isImport());
    }

    Dialog onCreateDialog(Activity activity, int idDialog) {
        if (idDialog == id.menu_log_dnf || idDialog == id.menu_log_find) {
            return logFindDialogHelper.onCreateDialog(activity, idDialog);
        }
        return contextActionDeleteDialogHelper.onCreateDialog();
    }

    public void onPrepareDialog(int id, Dialog dialog) {
        if (id == R.id.delete_cache)
            contextActionDeleteDialogHelper.onPrepareDialog(dialog);
        else
            logFindDialogHelper.onPrepareDialog(activity, id, dialog);
    }

    void search(Activity activity, SearchTarget searchTarget) {
        Intent intent = activity.getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchTarget.setTarget(query);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(activity,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        } else {
            searchTarget.setTarget(null);
        }
    }
}
