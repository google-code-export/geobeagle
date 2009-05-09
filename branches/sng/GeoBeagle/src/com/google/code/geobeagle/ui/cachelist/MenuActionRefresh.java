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
    static class AdapterCachesSorter {
        private final CacheListData mCacheListData;
        private final GeocacheListAdapter mGeocacheListAdapter;
        private final Timing mTiming;

        AdapterCachesSorter(CacheListData cacheListData, GeocacheListAdapter geocacheListAdapter,
                Timing timing) {
            mCacheListData = cacheListData;
            mGeocacheListAdapter = geocacheListAdapter;
            mTiming = timing;
        }

        public void sort() {
            mCacheListData.sort();
            mTiming.lap("sort time");

            mGeocacheListAdapter.notifyDataSetChanged();
            mTiming.lap("notify changed time");
        }
    }

    static class SqlCacheLoader {
        private final CacheListData mCacheListData;
        private final FilterNearestCaches mFilterNearestCaches;
        private final GeocachesSql mGeocachesSql;
        private final LocationControlBuffered mLocationControlBuffered;
        private final Timing mTiming;

        SqlCacheLoader(GeocachesSql geocachesSql, FilterNearestCaches filterNearestCaches,
                CacheListData cacheListData, LocationControlBuffered locationControlBuffered,
                Timing timing) {
            mGeocachesSql = geocachesSql;
            mFilterNearestCaches = filterNearestCaches;
            mCacheListData = cacheListData;
            mLocationControlBuffered = locationControlBuffered;
            mTiming = timing;
        }

        public void load(Location location) {
            mGeocachesSql.loadCaches(location, mFilterNearestCaches.getWhereFactory());
            ArrayList<Geocache> geocaches = mGeocachesSql.getGeocaches();
            mTiming.lap("SQL time");

            mCacheListData.add(geocaches, mLocationControlBuffered);
            mTiming.lap("add to list time");
        }
    }

    static class Timing {
        private final Calendar mCalendar;
        private long mStartTime;

        public Timing() {
            mCalendar = Calendar.getInstance();
        }

        public void lap(CharSequence msg) {
            long finishTime = mCalendar.getTimeInMillis();
            Log.v("GeoBeagle", msg + ": " + (finishTime - mStartTime));
            mStartTime = finishTime;
        }

        public void start() {
            mStartTime = mCalendar.getTimeInMillis();
        }
    }

    static class TitleUpdater {
        private final CacheListData mCacheListData;
        private final FilterNearestCaches mFilterNearestCaches;
        private final GeocachesSql mGeocachesSql;
        private final ListActivity mListActivity;
        private final ListTitleFormatter mListTitleFormatter;
        private final Timing mTiming;

        TitleUpdater(GeocachesSql geocachesSql, ListActivity listActivity,
                FilterNearestCaches filterNearestCaches, CacheListData cacheListData,
                ListTitleFormatter listTitleFormatter, Timing timing) {
            mGeocachesSql = geocachesSql;
            mListActivity = listActivity;
            mFilterNearestCaches = filterNearestCaches;
            mCacheListData = cacheListData;
            mListTitleFormatter = listTitleFormatter;
            mTiming = timing;
        }

        void update() {
            final int sqlCount = mGeocachesSql.getCount();
            final int nearestCachesCount = mCacheListData.size();
            mListActivity.setTitle(mListActivity.getString(mFilterNearestCaches.getTitleText(),
                    nearestCachesCount, sqlCount));
            if (0 == nearestCachesCount) {
                TextView textView = (TextView)mListActivity.findViewById(android.R.id.empty);
                textView.setText(mListTitleFormatter.getBodyText(sqlCount));
            }
            mTiming.lap("update title time");
        }
    }

    private AdapterCachesSorter mAdapterCachesSorter;
    private Location mLastSortLocation;
    private Location mLastSqlLocation;
    private final LocationControlBuffered mLocationControlBuffered;
    private final SqlCacheLoader mSqlCacheLoader;
    private final Timing mTiming;
    private final TitleUpdater mTitleUpdater;

    public MenuActionRefresh(AdapterCachesSorter adapterCachesSorter,
            LocationControlBuffered locationControlBuffered, SqlCacheLoader sqlCacheLoader,
            Timing timing, TitleUpdater titleUpdater, Location lastSortLocation,
            Location lastSqlLocation) {
        mAdapterCachesSorter = adapterCachesSorter;
        mLocationControlBuffered = locationControlBuffered;
        mSqlCacheLoader = sqlCacheLoader;
        mTiming = timing;
        mTitleUpdater = titleUpdater;
        mLastSqlLocation = lastSqlLocation;
        mLastSortLocation = lastSortLocation;
    }

    public void act() {
        mTiming.start();
        Location location = mLocationControlBuffered.getLocation();

        if (location.distanceTo(mLastSqlLocation) > 500) {
            mSqlCacheLoader.load(location);
            mTitleUpdater.update();
            mAdapterCachesSorter.sort();
            mLastSqlLocation = location;
            mLastSortLocation = location;
        }

        else if (location.distanceTo(mLastSortLocation) > 6) {
            mAdapterCachesSorter.sort();
            mLastSortLocation = location;
        }
    }
}
