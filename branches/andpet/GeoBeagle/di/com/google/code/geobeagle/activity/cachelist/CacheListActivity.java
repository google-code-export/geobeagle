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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class CacheListActivity extends ListActivity {
    private CacheListDelegate mCacheListDelegate;

    // For testing.
    public CacheListDelegate getCacheListDelegate() {
        return mCacheListDelegate;
    }

    // This is the ctor that Android will use.
    public CacheListActivity() {
    }

    // This is the ctor for testing.
    public CacheListActivity(CacheListDelegate cacheListDelegate) {
        mCacheListDelegate = cacheListDelegate;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mCacheListDelegate.onContextItemSelected(item) || super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("GeoBeagle", "CacheListActivity onCreate");
        mCacheListDelegate = CacheListDelegateDI.create(this, getLayoutInflater());
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
        //Log.d("GeoBeagle", "CacheListActivity onPause");
        super.onPause();
        mCacheListDelegate.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("GeoBeagle", "CacheListActivity onResume");
        mCacheListDelegate.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCacheListDelegate.onActivityResult();
    }
    
}
