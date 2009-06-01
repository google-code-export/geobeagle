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

package com.google.code.geobeagle.database;

import android.location.Location;

public class WhereFactoryNearestCaches implements WhereFactory {
    // 1 degree ~= 111km
    public static final double DEGREES_DELTA = 0.08;

    public String getWhere(Location location) {
        if (location == null)
            return null;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        double latLow = latitude - WhereFactoryNearestCaches.DEGREES_DELTA;
        double latHigh = latitude + WhereFactoryNearestCaches.DEGREES_DELTA;
        double lat_radians = Math.toRadians(latitude);
        double cos_lat = Math.cos(lat_radians);
        double lonLow = Math.max(-180, longitude - WhereFactoryNearestCaches.DEGREES_DELTA
                / cos_lat);
        double lonHigh = Math.min(180, longitude + WhereFactoryNearestCaches.DEGREES_DELTA
                / cos_lat);
        return "Latitude > " + latLow + " AND Latitude < " + latHigh + " AND Longitude > " + lonLow
                + " AND Longitude < " + lonHigh;
    }
}
