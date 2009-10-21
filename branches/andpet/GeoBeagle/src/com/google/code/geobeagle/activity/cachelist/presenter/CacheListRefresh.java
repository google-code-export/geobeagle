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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;
import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI;
import com.google.code.geobeagle.database.CachesProviderArea;

import android.util.Log;

/** Decides what sort of cache list refreshing to do and carries it out. */
public class CacheListRefresh implements Refresher {

    private int getMinActionExceedingTolerance(IGpsLocation here, float azimuth, long now) {
        int i;
        for (i = 0; i < mActionAndTolerances.length; i++) {
            if (mActionAndTolerances[i].exceedsTolerance(here, azimuth, now))
                break;
        }
        return i;
    }

    private void performActions(IGpsLocation here, float azimuth, int startingAction, long now) {
        for (int i = startingAction; i < mActionAndTolerances.length; i++) {
            mActionAndTolerances[i].refresh();
            mActionAndTolerances[i].updateLastRefreshed(here, azimuth, now);
        }
    }

    public static class UpdateFlag {
        private boolean mUpdateFlag = true;

        public void setUpdatesEnabled(boolean enabled) {
            Log.d("GeoBeagle", "Update enabled: " + enabled);
            mUpdateFlag = enabled;
        }

        boolean updatesEnabled() {
            return mUpdateFlag;
        }
    }

    private final ActionAndTolerance mActionAndTolerances[];
    private final LocationControlBuffered mLocationControlBuffered;
    private final CacheListDelegateDI.Timing mTiming;
    private final UpdateFlag mUpdateFlag;
    private final CachesProviderArea[] mCachesProviders;

    public CacheListRefresh(ActionAndTolerance[] actionAndTolerances, CacheListDelegateDI.Timing timing,
            LocationControlBuffered locationControlBuffered, UpdateFlag updateFlag,
            CachesProviderArea[] providers) {
        mLocationControlBuffered = locationControlBuffered;
        mTiming = timing;
        mActionAndTolerances = actionAndTolerances;
        mUpdateFlag = updateFlag;
        mCachesProviders = providers;
    }

    @Override
    public void forceRefresh() {
        mTiming.start();
        final long now = mTiming.getTime();
        for (CachesProviderArea area : mCachesProviders) {
            area.reloadFilter();
        }
        performActions(mLocationControlBuffered.getGpsLocation(),
                mLocationControlBuffered.getAzimuth(), 0, now);
    }

    @Override
    public void refresh() {
        // TODO: Is this check still necessary?
        /*
         * if (!mSqliteWrapper.isOpen()) { Log.d("GeoBeagle",
         * "Refresh: database is closed, punting."); return; }
         */

        if (!mUpdateFlag.updatesEnabled())
            return;
        //Log.d("GeoBeagle", "CacheListRefresh.refresh");
        mTiming.start();
        final long now = mTiming.getTime();
        final IGpsLocation here = mLocationControlBuffered.getGpsLocation();
        final float azimuth = mLocationControlBuffered.getAzimuth();
        final int minActionExceedingTolerance = getMinActionExceedingTolerance(here,
                azimuth, now);
        performActions(here, azimuth, minActionExceedingTolerance, now);
    }
}
