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
import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.GeocachesSql;

import android.app.ListActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MenuActionRefresh implements MenuAction {
    static class ActionAndTolerance {
        private IGpsLocation mLastRefreshed;
        private final RefreshAction mRefreshAction;
        private final float mTolerance;

        public ActionAndTolerance(RefreshAction refreshAction, float tolerance,
                IGpsLocation lastRefreshed) {
            mRefreshAction = refreshAction;
            mTolerance = tolerance;
            mLastRefreshed = lastRefreshed;
        }

        public boolean exceedsTolerance(IGpsLocation here) {
            final float distanceTo = here.distanceTo(mLastRefreshed);
            return (distanceTo >= mTolerance);
        }

        public void refresh() {
            mRefreshAction.refresh();
        }

        public void updateLastRefreshed(IGpsLocation here) {
            mLastRefreshed = here;
        }
    }

    static class AdapterCachesSorter implements RefreshAction {
        private final CacheListData mCacheListData;
        private final LocationControlBuffered mLocationControlBuffered;
        private final Timing mTiming;

        AdapterCachesSorter(CacheListData cacheListData, Timing timing,
                LocationControlBuffered locationControlBuffered) {
            mCacheListData = cacheListData;
            mTiming = timing;
            mLocationControlBuffered = locationControlBuffered;
        }

        public void refresh() {
            mLocationControlBuffered.getSortStrategy().sort(mCacheListData.get());
            mTiming.lap("sort time");
        }
    }

    static class DistanceUpdater implements RefreshAction {
        private final GeocacheListAdapter mGeocacheListAdapter;

        DistanceUpdater(GeocacheListAdapter geocacheListAdapter) {
            mGeocacheListAdapter = geocacheListAdapter;
        }

        public void refresh() {
            mGeocacheListAdapter.notifyDataSetChanged();
        }
    }

    static interface RefreshAction {
        public void refresh();
    }

    static class SqlCacheLoader implements RefreshAction {
        private final CacheListData mCacheListData;
        private final FilterNearestCaches mFilterNearestCaches;
        private final GeocachesSql mGeocachesSql;
        private final LocationControlBuffered mLocationControlBuffered;
        private final Timing mTiming;
        private final TitleUpdater mTitleUpdater;

        SqlCacheLoader(GeocachesSql geocachesSql, FilterNearestCaches filterNearestCaches,
                CacheListData cacheListData, LocationControlBuffered locationControlBuffered,
                TitleUpdater titleUpdater, Timing timing) {
            mGeocachesSql = geocachesSql;
            mFilterNearestCaches = filterNearestCaches;
            mCacheListData = cacheListData;
            mLocationControlBuffered = locationControlBuffered;
            mTiming = timing;
            mTitleUpdater = titleUpdater;
        }

        public void refresh() {
            mGeocachesSql.loadCaches(mLocationControlBuffered.getLocation(), mFilterNearestCaches
                    .getWhereFactory());
            ArrayList<Geocache> geocaches = mGeocachesSql.getGeocaches();
            mTiming.lap("SQL time");

            mCacheListData.add(geocaches, mLocationControlBuffered);
            mTiming.lap("add to list time");

            mTitleUpdater.update();
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

        public void update() {
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

    private ActionAndTolerance[] mActionAndTolerances;
    private final LocationControlBuffered mLocationControlBuffered;
    private final Timing mTiming;

    public MenuActionRefresh(LocationControlBuffered locationControlBuffered, Timing timing,
            ActionAndTolerance[] actionAndTolerances) {
        mLocationControlBuffered = locationControlBuffered;
        mTiming = timing;
        mActionAndTolerances = actionAndTolerances;
    }

    public void act() {
        mTiming.start();
        IGpsLocation here = mLocationControlBuffered.getGpsLocation();
        boolean fExceedsTolerances = false;
        for (int i = 0; i < mActionAndTolerances.length; i++) {
            final boolean exceedsTolerance = mActionAndTolerances[i].exceedsTolerance(here);
            if (exceedsTolerance)
                fExceedsTolerances = true;

            if (fExceedsTolerances) {
                mActionAndTolerances[i].refresh();
                mActionAndTolerances[i].updateLastRefreshed(here);
            }
        }
    }
}
