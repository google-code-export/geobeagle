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

import com.google.code.geobeagle.OnClickCancelListener;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivityRestorer;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete.ContextActionDeleteDialogHelper;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.inject.Injector;

import roboguice.activity.GuiceListActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class CacheListActivity extends GuiceListActivity {
    private CacheListDelegate mCacheListDelegate;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mCacheListDelegate.onContextItemSelected(item) || super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GeoBeagle", "CacheListActivity onCreate");
        requestWindowFeature(Window.FEATURE_PROGRESS);

        final Injector injector = this.getInjector();

        final InflatedGpsStatusWidget inflatedGpsStatusWidget = injector
                .getInstance(InflatedGpsStatusWidget.class);
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = injector
                .getInstance(GpsStatusWidgetDelegate.class);
        inflatedGpsStatusWidget.setDelegate(gpsStatusWidgetDelegate);

        mCacheListDelegate = injector.getInstance(CacheListDelegate.class);

        mCacheListDelegate.onCreate();
        injector.getInstance(ActivityRestorer.class).restore(getIntent().getFlags());
        Log.d("GeoBeagle", "Done creating CacheListActivity");
    }

    @Override
    public Dialog onCreateDialog(int idDialog) {
        // idDialog must be CACHE_LIST_DIALOG_CONFIRM_DELETE.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View confirmDeleteCacheView = LayoutInflater.from(this).inflate(
                R.layout.confirm_delete_cache, null);

        builder.setNegativeButton(R.string.confirm_delete_negative, new OnClickCancelListener());
        builder.setView(confirmDeleteCacheView);

        return getInjector().getInstance(ContextActionDeleteDialogHelper.class).onCreateDialog(
                builder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return mCacheListDelegate.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCacheListDelegate.onListItemClick(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mCacheListDelegate.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.d("GeoBeagle", "CacheListActivity onPause");
        /*
         * cacheListDelegate closes the database, it must be called before
         * super.onPause because the guice activity onPause nukes the database
         * object from the guice map.
         */
        mCacheListDelegate.onPause();
        super.onPause();
        Log.d("GeoBeagle", "CacheListActivity onPauseComplete");
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        getInjector().getInstance(ContextActionDeleteDialogHelper.class).onPrepareDialog(dialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeoBeagle", "CacheListActivity onResume");
        mCacheListDelegate.onResume();
    }

}
