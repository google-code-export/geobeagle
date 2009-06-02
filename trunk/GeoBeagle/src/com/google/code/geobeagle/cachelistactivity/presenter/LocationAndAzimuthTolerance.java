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

package com.google.code.geobeagle.cachelistactivity.presenter;

import com.google.code.geobeagle.cachelistactivity.model.LocationControlBuffered.IGpsLocation;

import android.util.Log;

public class LocationAndAzimuthTolerance implements ToleranceStrategy {
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