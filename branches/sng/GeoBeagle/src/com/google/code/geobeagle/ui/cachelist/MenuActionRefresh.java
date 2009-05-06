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

package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.GeocachesSql;

import android.app.ListActivity;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MenuActionRefresh implements MenuAction {
    private final CacheListData mCacheListData;
    private final FilterNearestCaches mFilterNearestCaches;
    private final GeocacheListAdapter mGeocacheListAdapter;
    private final GeocachesSql mGeocachesSql;
    private final ListActivity mListActivity;
    private final ListTitleFormatter mListTitleFormatter;
    private final LocationControlBuffered mLocationControlBuffered;

    public MenuActionRefresh(ListActivity listActivity,
            LocationControlBuffered locationControlBuffered,
            FilterNearestCaches filterNearestCaches, GeocachesSql geocachesSql,
            CacheListData cacheListData, GeocacheListAdapter geocacheListAdapter,
            ListTitleFormatter listTitleFormatter) {
        mGeocachesSql = geocachesSql;
        mCacheListData = cacheListData;
        mGeocacheListAdapter = geocacheListAdapter;
        mLocationControlBuffered = locationControlBuffered;
        mListActivity = listActivity;
        mFilterNearestCaches = filterNearestCaches;
        mListTitleFormatter = listTitleFormatter;
    }

    public void onCreate() {
        mListActivity.setListAdapter(mGeocacheListAdapter);
    }

    public void act() {
//        Calendar calendar = Calendar.getInstance();
//        long timeStart = calendar.getTimeInMillis();

        Location location = mLocationControlBuffered.getLocation();
        mGeocachesSql.loadCaches(location, mFilterNearestCaches.getWhereFactory());
        ArrayList<Geocache> geocaches = mGeocachesSql.getGeocaches();
//        long timeSql = calendar.getTimeInMillis();
//        Log.v("GeoBeagle", "SQL Time: " + (timeSql - timeStart));

        mCacheListData.add(geocaches, mLocationControlBuffered);
//        long timeSort = calendar.getTimeInMillis();
//        Log.v("GeoBeagle", "Sort Time: " + (timeSort - timeSql));

        mGeocacheListAdapter.notifyDataSetChanged();
//        long timeListAdapter = calendar.getTimeInMillis();
//        Log.v("GeoBeagle", "Set adapter time: " + (timeListAdapter - timeSort));

        updateTitle();
        // long timeTitle = calendar.getTimeInMillis();
        // Log.v("GeoBeagle", "updatetitle time: " + (timeTitle -
        // timeListAdapter));
    }

    public void updateTitle() {
        int sqlCount = mGeocachesSql.getCount();
        mListActivity.setTitle(mListActivity.getString(mFilterNearestCaches.getTitleText(),
                mCacheListData.size(), sqlCount));
        TextView textView = (TextView)mListActivity.findViewById(android.R.id.empty);
        textView.setText(mListTitleFormatter.getBodyText(sqlCount));
    }
}
