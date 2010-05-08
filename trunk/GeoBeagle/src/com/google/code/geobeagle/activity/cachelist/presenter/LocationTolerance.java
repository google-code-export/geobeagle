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

import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;

public class LocationTolerance implements ToleranceStrategy {
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
        // Log.d("GeoBeagle", "distance, tolerance: " + distanceTo + ", " +
        // mLocationTolerance);
        final boolean fExceedsTolerance = distanceTo >= mLocationTolerance;
        return fExceedsTolerance;
    }

    public void updateLastRefreshed(IGpsLocation here, float azimuth, long now) {
        // Log.d("GeoBeagle", "updateLastRefreshed here: " + here);
        mLastRefreshLocation = here;
        mLastRefreshTime = now;
    }
}
