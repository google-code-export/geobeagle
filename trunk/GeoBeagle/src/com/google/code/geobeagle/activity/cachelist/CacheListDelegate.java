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
import com.google.code.geobeagle.activity.ActivityRestorer;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.cachelist.actions.context.delete.ContextActionDeleteDialogHelper;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
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
    static class ImportIntentManager {
        static final String INTENT_EXTRA_IMPORT_TRIGGERED = "com.google.code.geabeagle.import_triggered";
        private final Activity mActivity;

        @Inject
        ImportIntentManager(Activity activity) {
            this.mActivity = activity;
        }

        boolean isImport() {
            Intent intent = mActivity.getIntent();
            if (intent == null)
                return false;

            String action = intent.getAction();
            if (action == null)
                return false;

            if (!action.equals("android.intent.action.VIEW"))
                return false;

            if (intent.getBooleanExtra(INTENT_EXTRA_IMPORT_TRIGGERED, false))
                return false;

            // Need to alter the intent so that the import isn't retriggered if
            // pause/resume is a result of the phone going to sleep and then
            // waking up again.
            intent.putExtra(INTENT_EXTRA_IMPORT_TRIGGERED, true);
            return true;
        }
    }

    private final ActivitySaver activitySaver;
    private final ActivityVisible activityVisible;
    private final CacheListRefresh cacheListRefresh;
    private final GeocacheListController controller;
    private final Provider<DbFrontend> dbFrontendProvider;
    private final ImportIntentManager importIntentManager;
    private final GeocacheListPresenter presenter;
    private final LogFindDialogHelper logFindDialogHelper;
    private final ContextActionDeleteDialogHelper contextActionDeleteDialogHelper;
    private final Activity activity;
    private ActivityRestorer activityRestorer;

    public CacheListDelegate(ImportIntentManager importIntentManager,
            ActivitySaver activitySaver,
            CacheListRefresh cacheListRefresh,
            GeocacheListController geocacheListController,
            GeocacheListPresenter geocacheListPresenter,
            Provider<DbFrontend> dbFrontendProvider,
            ActivityVisible activityVisible,
            LogFindDialogHelper logFindDialogHelper,
            ContextActionDeleteDialogHelper contextActionDeleteDialogHelper,
            Activity activity,
            ActivityRestorer activityRestorer) {
        this.activitySaver = activitySaver;
        this.cacheListRefresh = cacheListRefresh;
        this.controller = geocacheListController;
        this.presenter = geocacheListPresenter;
        this.importIntentManager = importIntentManager;
        this.dbFrontendProvider = dbFrontendProvider;
        this.activityVisible = activityVisible;
        this.logFindDialogHelper = logFindDialogHelper;
        this.contextActionDeleteDialogHelper = contextActionDeleteDialogHelper;
        this.activity = activity;
        this.activityRestorer = activityRestorer;
    }

    @Inject
    public CacheListDelegate(Injector injector) {
        this.activitySaver = injector.getInstance(ActivitySaver.class);
        this.cacheListRefresh = injector.getInstance(CacheListRefresh.class);
        this.controller = injector.getInstance(GeocacheListController.class);
        this.presenter = injector.getInstance(GeocacheListPresenter.class);
        this.importIntentManager = injector.getInstance(ImportIntentManager.class);
        this.dbFrontendProvider = injector.getProvider(DbFrontend.class);
        this.activityVisible = injector.getInstance(ActivityVisible.class);
        this.logFindDialogHelper = injector.getInstance(LogFindDialogHelper.class);
        this.contextActionDeleteDialogHelper = injector
                .getInstance(ContextActionDeleteDialogHelper.class);
        this.activity = injector.getInstance(Activity.class);
        this.activityRestorer = injector.getInstance(ActivityRestorer.class);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        return controller.onContextItemSelected(menuItem);
    }

    public void onCreate(Intent intent, InflatedGpsStatusWidget inflatedGpsStatusWidget, GpsStatusWidgetDelegate gpsStatusWidgetDelegate) {
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
            activityRestorer.restore(intent.getFlags(), ActivityType.CACHE_LIST);
        }
        presenter.onCreate();
        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
            activityRestorer.restore(intent.getFlags(), ActivityType.CACHE_LIST);
        }
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
        return contextActionDeleteDialogHelper.onCreateDialog(activity);
    }

    public void onPrepareDialog(int id, Dialog dialog) {
        if (id == R.id.delete_cache)
            contextActionDeleteDialogHelper.onPrepareDialog(dialog);
        else
            logFindDialogHelper.onPrepareDialog(activity, id, dialog);
    }

    void search(Activity cacheListActivityHoneycomb, SearchTarget searchTarget) {
        Intent intent = cacheListActivityHoneycomb.getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchTarget.setTarget(query);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(cacheListActivityHoneycomb,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        } else {
            searchTarget.setTarget(null);
        }
    }
}
