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
import com.google.inject.Inject;

import android.util.Log;

public class CacheListRefresh implements Refresher {
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

    private final ActionManager mActionManager;
    private final LocationControlBuffered mLocationControlBuffered;
    private final CacheListDelegateDI.Timing mTiming;
    private final UpdateFlag mUpdateFlag;

    @Inject
    public CacheListRefresh(ActionManager actionManager, CacheListDelegateDI.Timing timing,
            LocationControlBuffered locationControlBuffered, UpdateFlag updateFlag) {
        mLocationControlBuffered = locationControlBuffered;
        mTiming = timing;
        mActionManager = actionManager;
        mUpdateFlag = updateFlag;
    }

    public void forceRefresh() {
        mTiming.start();
        final long now = mTiming.getTime();
        mActionManager.performActions(mLocationControlBuffered.getGpsLocation(),
                mLocationControlBuffered.getAzimuth(), 0, now);
    }

    public void refresh() {
        // TODO: Is this check still necessary?
        /*
         * if (!mSqliteWrapper.isOpen()) { Log.d("GeoBeagle",
         * "Refresh: database is closed, punting."); return; }
         */

        if (!mUpdateFlag.updatesEnabled())
            return;
//        Log.d("GeoBeagle", "CacheListRefresh.refresh");
        mTiming.start();
        final long now = mTiming.getTime();
        final IGpsLocation here = mLocationControlBuffered.getGpsLocation();
        final float azimuth = mLocationControlBuffered.getAzimuth();
        final int minActionExceedingTolerance = mActionManager.getMinActionExceedingTolerance(here,
                azimuth, now);
        mActionManager.performActions(here, azimuth, minActionExceedingTolerance, now);
    }
}
