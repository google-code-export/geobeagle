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

import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.location.LocationControlBuffered;
import com.google.code.geobeagle.location.LocationControlBuffered.IGpsLocation;

import android.app.ListActivity;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class CacheListRefresh implements Refresher {
    static class ActionAndTolerance {
        private final RefreshAction mRefreshAction;
        private final ToleranceStrategy mToleranceStrategy;

        public ActionAndTolerance(RefreshAction refreshAction, ToleranceStrategy toleranceStrategy) {
            mRefreshAction = refreshAction;
            mToleranceStrategy = toleranceStrategy;
        }

        public boolean exceedsTolerance(IGpsLocation here, float azimuth, long now) {
            return mToleranceStrategy.exceedsTolerance(here, azimuth, now);
        }

        public void refresh() {
            mRefreshAction.refresh();
        }

        public void updateLastRefreshed(IGpsLocation here, float azimuth, long now) {
            mToleranceStrategy.updateLastRefreshed(here, azimuth, now);
        }
    }

    public static class ActionManager {
        private final ActionAndTolerance mActionAndTolerances[];

        public ActionManager(ActionAndTolerance actionAndTolerances[]) {
            mActionAndTolerances = actionAndTolerances;
        }

        public int getMinActionExceedingTolerance(IGpsLocation here, float azimuth, long now) {
            int i;
            for (i = 0; i < mActionAndTolerances.length; i++) {
                if (mActionAndTolerances[i].exceedsTolerance(here, azimuth, now))
                    break;
            }
            return i;
        }

        public void performActions(IGpsLocation here, float azimuth, int startingAction, long now) {
            for (int i = startingAction; i < mActionAndTolerances.length; i++) {
                mActionAndTolerances[i].refresh();
                mActionAndTolerances[i].updateLastRefreshed(here, azimuth, now);
            }
        }
    }

    static class AdapterCachesSorter implements RefreshAction {
        private final CacheListData mCacheListData;
        private final LocationControlBuffered mLocationControlBuffered;
        private final CacheListDelegateDI.Timing mTiming;

        AdapterCachesSorter(CacheListData cacheListData, CacheListDelegateDI.Timing timing,
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
//            Log.v("GeoBeagle", "notifyDataSetChanged");
            mGeocacheListAdapter.notifyDataSetChanged();
        }
    }

    static class LocationAndAzimuthTolerance implements ToleranceStrategy {
        private float mLastAzimuth;
        LocationTolerance mLocationTolerance;

        public LocationAndAzimuthTolerance(LocationTolerance locationTolerance, float lastAzimuth) {
            mLocationTolerance = locationTolerance;
            mLastAzimuth = lastAzimuth;
        }

        public boolean exceedsTolerance(IGpsLocation here, float currentAzimuth, long now) {
            if (mLastAzimuth != currentAzimuth) {
                Log.v("GeoBeagle", "new azimuth: " + currentAzimuth);
                mLastAzimuth = currentAzimuth;
                return true;
            }
            return mLocationTolerance.exceedsTolerance(here, currentAzimuth, now);
        }

        public void updateLastRefreshed(IGpsLocation here, float azimuth, long now) {
            mLocationTolerance.updateLastRefreshed(here, azimuth, now);
            mLastAzimuth = azimuth;
        }
    }

    static class LocationTolerance implements ToleranceStrategy {
        private IGpsLocation mLastRefreshLocation;
        private final float mLocationTolerance;
        private final int mMinTimeBetweenRefresh;
        private long mLastRefreshTime;

        public LocationTolerance(float locationTolerance, IGpsLocation lastRefreshed,
                int minTimeBetweenRefresh) {
            mLocationTolerance = locationTolerance;
            mLastRefreshLocation = lastRefreshed;
            mMinTimeBetweenRefresh = minTimeBetweenRefresh;
            mLastRefreshTime = 0;
        }

        public boolean exceedsTolerance(IGpsLocation here, float azimuth, long now) {
            if (now < mLastRefreshTime + mMinTimeBetweenRefresh)
                return false;
            final float distanceTo = here.distanceTo(mLastRefreshLocation);
//            Log.v("GeoBeagle", "distance, tolerance: " + distanceTo + ", " + mLocationTolerance);
            final boolean fExceedsTolerance = distanceTo >= mLocationTolerance;
            return fExceedsTolerance;
        }

        public void updateLastRefreshed(IGpsLocation here, float azimuth, long now) {
//            Log.v("GeoBeagle", "updateLastRefreshed here: " + here);
            mLastRefreshLocation = here;
            mLastRefreshTime = now;
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
        private final CacheListDelegateDI.Timing mTiming;
        private final TitleUpdater mTitleUpdater;

        SqlCacheLoader(GeocachesSql geocachesSql, FilterNearestCaches filterNearestCaches,
                CacheListData cacheListData, LocationControlBuffered locationControlBuffered,
                TitleUpdater titleUpdater, CacheListDelegateDI.Timing timing) {
            mGeocachesSql = geocachesSql;
            mFilterNearestCaches = filterNearestCaches;
            mCacheListData = cacheListData;
            mLocationControlBuffered = locationControlBuffered;
            mTiming = timing;
            mTitleUpdater = titleUpdater;
        }

        public void refresh() {
            final Location location = mLocationControlBuffered.getLocation();
//            Log.v("GeoBeagle", "Location: " + location);
            mGeocachesSql.loadCaches(location, mFilterNearestCaches.getWhereFactory());
            ArrayList<Geocache> geocaches = mGeocachesSql.getGeocaches();
            mTiming.lap("SQL time");

            mCacheListData.add(geocaches, mLocationControlBuffered);
            mTiming.lap("add to list time");

            mTitleUpdater.update();
        }
    }

    static class TitleUpdater {
        private final CacheListData mCacheListData;
        private final FilterNearestCaches mFilterNearestCaches;
        private final GeocachesSql mGeocachesSql;
        private final ListActivity mListActivity;
        private final ListTitleFormatter mListTitleFormatter;
        private final CacheListDelegateDI.Timing mTiming;

        TitleUpdater(GeocachesSql geocachesSql, ListActivity listActivity,
                FilterNearestCaches filterNearestCaches, CacheListData cacheListData,
                ListTitleFormatter listTitleFormatter, CacheListDelegateDI.Timing timing) {
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

    static interface ToleranceStrategy {
        public boolean exceedsTolerance(IGpsLocation here, float azimuth, long now);

        public void updateLastRefreshed(IGpsLocation here, float azimuth, long now);
    }

    private final ActionManager mActionManager;
    private final LocationControlBuffered mLocationControlBuffered;
    private final CacheListDelegateDI.Timing mTiming;

    public CacheListRefresh(ActionManager actionManager,
            LocationControlBuffered locationControlBuffered, CacheListDelegateDI.Timing timing) {
        mLocationControlBuffered = locationControlBuffered;
        mTiming = timing;
        mActionManager = actionManager;
    }

    public void forceRefresh() {
        mTiming.start();
        long now = mTiming.getTime();
        mActionManager.performActions(mLocationControlBuffered.getGpsLocation(),
                mLocationControlBuffered.getAzimuth(), 0, now);
    }

    public void refresh() {
        mTiming.start();
        long now = mTiming.getTime();
        final IGpsLocation here = mLocationControlBuffered.getGpsLocation();
        final float azimuth = mLocationControlBuffered.getAzimuth();
        final int minActionExceedingTolerance = mActionManager.getMinActionExceedingTolerance(here,
                azimuth, now);
        mActionManager.performActions(here, azimuth, minActionExceedingTolerance, now);
    }
}
