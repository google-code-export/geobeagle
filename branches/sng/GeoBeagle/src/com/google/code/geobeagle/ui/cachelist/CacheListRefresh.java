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
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.GeocachesSql;

import android.app.ListActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class CacheListRefresh implements Refresher {
    static class ActionAndTolerance {
        private final RefreshAction mRefreshAction;
        private final ToleranceStrategy mToleranceStrategy;

        public ActionAndTolerance(RefreshAction refreshAction, ToleranceStrategy toleranceStrategy,
                float tolerance, IGpsLocation lastRefreshed) {
            mRefreshAction = refreshAction;
            mToleranceStrategy = toleranceStrategy;
        }

        public boolean exceedsTolerance(IGpsLocation here, float azimuth) {
            return mToleranceStrategy.exceedsTolerance(here, azimuth);
        }

        public void refresh() {
            mRefreshAction.refresh();
        }

        public void updateLastRefreshed(IGpsLocation here, float azimuth) {
            mToleranceStrategy.updateLastRefreshed(here, azimuth);
        }
    }

    public static class ActionManager {
        private final ActionAndTolerance mActionAndTolerances[];

        public ActionManager(ActionAndTolerance actionAndTolerances[]) {
            mActionAndTolerances = actionAndTolerances;
        }

        public int getMinActionExceedingTolerance(IGpsLocation here, float azimuth) {
            int i;
            for (i = 0; i < mActionAndTolerances.length; i++) {
                if (mActionAndTolerances[i].exceedsTolerance(here, azimuth))
                    break;
            }
            return i;
        }

        public void performActions(IGpsLocation here, float azimuth, int startingAction) {
            for (int i = startingAction; i < mActionAndTolerances.length; i++) {
                mActionAndTolerances[i].refresh();
                mActionAndTolerances[i].updateLastRefreshed(here, azimuth);
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

        public boolean exceedsTolerance(IGpsLocation here, float currentAzimuth) {
            if (mLastAzimuth != currentAzimuth) {
                Log.v("GeoBeagle", "new azimuth: " + currentAzimuth);
                mLastAzimuth = currentAzimuth;
                return true;
            }
            return mLocationTolerance.exceedsTolerance(here, currentAzimuth);
        }

        public void updateLastRefreshed(IGpsLocation here, float azimuth) {
            mLocationTolerance.updateLastRefreshed(here, azimuth);
            mLastAzimuth = azimuth;
        }
    }

    static class LocationTolerance implements ToleranceStrategy {
        private IGpsLocation mLastRefreshed;
        private final float mLocationTolerance;

        public LocationTolerance(float locationTolerance, IGpsLocation lastRefreshed) {
            mLocationTolerance = locationTolerance;
            mLastRefreshed = lastRefreshed;
        }

        public boolean exceedsTolerance(IGpsLocation here, float azimuth) {
            return (here.distanceTo(mLastRefreshed) >= mLocationTolerance);
        }

        public void updateLastRefreshed(IGpsLocation here, float azimuth) {
            mLastRefreshed = here;
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
            mGeocachesSql.loadCaches(mLocationControlBuffered.getLocation(), mFilterNearestCaches
                    .getWhereFactory());
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
        public boolean exceedsTolerance(IGpsLocation here, float azimuth);

        public void updateLastRefreshed(IGpsLocation here, float azimuth);
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
        mActionManager.performActions(mLocationControlBuffered.getGpsLocation(),
                mLocationControlBuffered.getAzimuth(), 0);
    }

    public void refresh() {
        mTiming.start();
        final IGpsLocation here = mLocationControlBuffered.getGpsLocation();
        final float azimuth = mLocationControlBuffered.getAzimuth();
        final int minActionExceedingTolerance = mActionManager.getMinActionExceedingTolerance(here,
                azimuth);
        mActionManager.performActions(here, azimuth, minActionExceedingTolerance);
    }
}
