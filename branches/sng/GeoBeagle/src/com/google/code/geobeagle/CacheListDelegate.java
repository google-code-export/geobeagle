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

import com.google.code.geobeagle.io.LocationBookmarksSql;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheListDelegate {
    public static final String MY_CURRENT_LOCATION = "My Current Location";
    public static final String[] ADAPTER_FROM = {
        "cache"
    };
    public static final int[] ADAPTER_TO = {
        R.id.txt_cache
    };
    public static final String SELECT_CACHE = "SELECT_CACHE";

    public static List<Map<String, Object>> createSimpleAdapterData(List<CharSequence> locations) {
        ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>(locations
                .size());
        for (CharSequence location : locations) {
            Map<String, Object> map = new HashMap<String, Object>(1);
            map.put("cache", location);
            arrayList.add(map);
        }
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("cache", MY_CURRENT_LOCATION);
        arrayList.add(map);
        Collections.reverse(arrayList);
        return arrayList;
    }

    private final List<CharSequence> mLocations;
    private final ListActivity mParent;
    private final LocationBookmarksSql mLocationBookmarks;
    private final ErrorDisplayer mErrorDisplayer;

    public CacheListDelegate(ListActivity parent, List<CharSequence> locations,
            LocationBookmarksSql locationBookmarks, ErrorDisplayer errorDisplayer) {
        mParent = parent;
        mLocations = locations;
        mErrorDisplayer = errorDisplayer;
        mLocationBookmarks = locationBookmarks;
    }

    public void onResume() {
        mLocationBookmarks.onResume(null, mErrorDisplayer);
        mParent.setListAdapter(createSimpleAdapter(mParent, mLocations, R.layout.cache_row,
                ADAPTER_FROM, ADAPTER_TO));
    }

    public void onCreate() {
        mParent.setContentView(R.layout.cache_list);
    }

    protected SimpleAdapter createSimpleAdapter(Context context, List<CharSequence> locations,
            int view_layout, String[] from, int[] to) {
        return new SimpleAdapter(context, createSimpleAdapterData(locations), view_layout, from, to);
    }

    protected Intent createIntent(Context context, Class<?> cls) {
        return new Intent(context, cls);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = createIntent(mParent, GeoBeagle.class);
        CharSequence value = null;
        if (position > 0)
            value = mLocations.get(mLocations.size() - position);
        intent.putExtra("location", value).setAction(SELECT_CACHE);
        mParent.startActivity(intent);
    }
}
