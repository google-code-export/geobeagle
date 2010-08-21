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

import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;

public class GpsEnabledLocation implements IGpsLocation {
    private final float mLatitude;
    private final float mLongitude;

    public GpsEnabledLocation(float latitude, float longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    public float distanceTo(IGpsLocation gpsLocation) {
        return gpsLocation.distanceToGpsEnabledLocation(this);
    }

    @Override
    public float distanceToGpsEnabledLocation(GpsEnabledLocation gpsEnabledLocation) {
        final float calculateDistanceFast = GeocacheVector.calculateDistanceFast(mLatitude,
                mLongitude, gpsEnabledLocation.mLatitude, gpsEnabledLocation.mLongitude);
        return calculateDistanceFast;
    }
}
