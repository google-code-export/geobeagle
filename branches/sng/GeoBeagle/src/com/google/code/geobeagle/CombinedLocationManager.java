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

import android.location.LocationListener;
import android.location.LocationManager;

public class CombinedLocationManager {

    private final LocationManager mLocationManager;

    public CombinedLocationManager(LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    public void requestLocationUpdates(int minTime, int minDistance,
            LocationListener locationListener) {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime,
                minDistance, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance,
                locationListener);
    }

    public void removeUpdates(LocationListener locationListener) {
        mLocationManager.removeUpdates(locationListener);
    }
}
