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

import android.location.Location;
import android.location.LocationManager;

public class LocationControl {
    public static class LocationChooser {

        /**
         * Choose the better of two locations: If one location is newer and more
         * accurate, choose that. (This favors the gps). Otherwise, if one
         * location is newer, less accurate, but farther away than the sum of
         * the two accuracies, choose that. (This favors the network locator if
         * you've driven a distance and haven't been able to get a gps fix yet.)
         */
        public Location choose(Location location1, Location location2) {
            if (location1 == null)
                return location2;
            if (location2 == null)
                return location1;

            if (location2.getTime() > location1.getTime()) {
                if (location2.getAccuracy() <= location1.getAccuracy()) {
                    return location2;
                } else {
                    if (location1.distanceTo(location2) >= location1.getAccuracy()
                            + location2.getAccuracy()) {
                        return location2;
                    }
                }
            }
            return location1;
        }
    }

    private final LocationChooser mLocationChooser;
    private final LocationManager mLocationManager;

    public LocationControl(LocationManager locationManager, LocationChooser locationChooser) {
        this.mLocationManager = locationManager;
        this.mLocationChooser = locationChooser;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.android.geobrowse.GpsControlI#getLocation(android.content.Context)
     */
    public Location getLocation() {
        return mLocationChooser.choose(mLocationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER), mLocationManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

}
