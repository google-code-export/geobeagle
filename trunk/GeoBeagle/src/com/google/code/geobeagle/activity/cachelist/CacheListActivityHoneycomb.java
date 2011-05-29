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

import roboguice.activity.GuiceActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class CacheListActivityHoneycomb extends GuiceActivity {

    private CacheListDelegate cacheListDelegate;

    public CacheListDelegate getCacheListDelegate() {
        return cacheListDelegate;
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
        Log.d("GeoBeagle", "CacheListActivityHoneycomb onCreate");
        requestWindowFeature(Window.FEATURE_PROGRESS);

        Injector injector = getInjector();
        cacheListDelegate = injector.getInstance(CacheListDelegate.class);
        cacheListDelegate.onCreate(injector);

        Log.d("GeoBeagle", "Done creating CacheListActivityHoneycomb");
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
        Log.d("GeoBeagle", "CacheListActivityHoneycomb onPauseComplete");
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

        Log.d("GeoBeagle", "CacheListActivityHoneycomb onResume");
        cacheListDelegate.onResume(searchTarget);
    }
}
