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

import com.google.code.geobeagle.activity.ActivityWithDatabaseLifecycleManager;
import com.google.code.geobeagle.database.NullClosable;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

//TODO: Rename to CacheListActivity
public class CacheList extends ListActivity {
    private CacheListDelegate mCacheListDelegate;
    private ActivityWithDatabaseLifecycleManager mActivityWithDatabaseLifecycleManager;

    // This is the ctor that Android will use.
    public CacheList() {
    }

    // This is the ctor for testing.
    public CacheList(CacheListDelegate cacheListDelegate) {
        mCacheListDelegate = cacheListDelegate;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mCacheListDelegate.onContextItemSelected(item) || super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GeoBeagle", "CacheList onCreate");

        mCacheListDelegate = CacheListDelegateDI.create(this, getLayoutInflater());
        final NullClosable nullClosable = new NullClosable();
        final GeoBeagleSqliteOpenHelper geoBeagleSqliteOpenHelper = new GeoBeagleSqliteOpenHelper(
                this);
        mActivityWithDatabaseLifecycleManager = new ActivityWithDatabaseLifecycleManager(
                mCacheListDelegate, nullClosable, geoBeagleSqliteOpenHelper);

        mCacheListDelegate.onCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return mCacheListDelegate.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCacheListDelegate.onListItemClick(l, v, position, id);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onMenuOpened(int, android.view.Menu)
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        super.onMenuOpened(featureId, menu);
        return mCacheListDelegate.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mCacheListDelegate.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.d("GeoBeagle", "CacheList onPause");

        super.onPause();
        mActivityWithDatabaseLifecycleManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeoBeagle", "CacheList onResume");

        mActivityWithDatabaseLifecycleManager.onResume();
    }

}
