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

package com.google.code.geobeagle;

import com.google.code.geobeagle.ui.CacheListDelegate;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class CacheList extends ListActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("Import caches from /sdcard/caches.gpx (max 100)");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return mCacheListDelegate.onOptionsItemSelected(item);
    }

    public static final String SELECT_CACHE = CacheListDelegate.SELECT_CACHE;

    private static CacheListDelegate buildCacheListDelegate(ListActivity listActivity) {
        return CacheListDelegate.create(listActivity);
    }

    private CacheListDelegate mCacheListDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCacheListDelegate = buildCacheListDelegate(this);
        mCacheListDelegate.onCreate();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCacheListDelegate.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCacheListDelegate.onResume();
    }

}
