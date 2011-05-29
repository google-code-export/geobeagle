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

import com.google.inject.Injector;

import roboguice.activity.GuiceListActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;


public class CacheListActivity extends GuiceListActivity {

    private CacheListDelegate cacheListDelegate;

    public CacheListDelegate getCacheListDelegate() {
        return cacheListDelegate;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return cacheListDelegate.onContextItemSelected(item) || super.onContextItemSelected(item);
    }

    @Override
    public Dialog onCreateDialog(int idDialog) {
        super.onCreateDialog(idDialog);
        return cacheListDelegate.onCreateDialog(this, idDialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return cacheListDelegate.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return cacheListDelegate.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GeoBeagle", "CacheListActivity onCreate");
        requestWindowFeature(Window.FEATURE_PROGRESS);

        int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        if (sdkVersion >= Build.VERSION_CODES.HONEYCOMB) {
            startActivity(new Intent(this, CacheListActivityHoneycomb.class));
            finish();
            return;
        }

        Injector injector = this.getInjector();
        cacheListDelegate = injector.getInstance(CacheListDelegate.class);
        cacheListDelegate.onCreate(injector);

        Log.d("GeoBeagle", "Done creating CacheListActivity");
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        cacheListDelegate.onListItemClick(position);
    }

    @Override
    protected void onPause() {
        Log.d("GeoBeagle", "CacheListActivity onPause");
        /*
         * cacheListDelegate closes the database, it must be called before
         * super.onPause because the guice activity onPause nukes the database
         * object from the guice map.
         */
        cacheListDelegate.onPause();
        super.onPause();
        Log.d("GeoBeagle", "CacheListActivity onPauseComplete");
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        cacheListDelegate.onPrepareDialog(id, dialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SearchTarget searchTarget = getInjector().getInstance(SearchTarget.class);

        Log.d("GeoBeagle", "CacheListActivity onResume");
        cacheListDelegate.onResume(searchTarget);
    }
}
