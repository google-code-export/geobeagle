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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.io.LocationBookmarksSql;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Map;

public class CacheListDelegate {

    public static class CacheListDelegateFactory {
        public SimpleAdapter createSimpleAdapter(Context context,
                ArrayList<Map<String, Object>> arrayList, int view_layout, String[] from, int[] to) {
            return new SimpleAdapter(context, arrayList, view_layout, from, to);
        }
    }

    public static final String[] ADAPTER_FROM = {
            "cache", "distance"
    };
    public static final int[] ADAPTER_TO = {
            R.id.txt_cache, R.id.distance
    };
    public static final String SELECT_CACHE = "SELECT_CACHE";

    private final CacheListData mCacheListData;
    private final CacheListDelegateFactory mCacheListDelegateFactory;
    private final LocationBookmarksSql mLocationBookmarks;
    private final LocationControl mLocationControl;
    private final ListActivity mParent;
    private final Intent mIntent;

    public CacheListDelegate(ListActivity parent, LocationBookmarksSql locationBookmarks,
            LocationControl locationControl, CacheListDelegateFactory cacheListDelegateFactory,
            CacheListData cacheListData, Intent intent) {
        mParent = parent;
        mLocationBookmarks = locationBookmarks;
        mLocationControl = locationControl;
        mCacheListDelegateFactory = cacheListDelegateFactory;
        mCacheListData = cacheListData;
        mIntent = intent;
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_list);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        mIntent.putExtra("location", mCacheListData.getLocation(position)).setAction(SELECT_CACHE);
        mParent.startActivity(mIntent);
    }

    public void onResume() {
        mLocationBookmarks.onResume(null);
        mCacheListData.add(mLocationBookmarks.getLocations(), mLocationControl.getLocation());

        mParent.setListAdapter(mCacheListDelegateFactory.createSimpleAdapter(mParent,
                mCacheListData.getAdapterData(), R.layout.cache_row, ADAPTER_FROM, ADAPTER_TO));
    }
}
