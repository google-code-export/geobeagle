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

package com.google.code.geobeagle.location;

import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;

public class CombinedLocationManager {

    private final ArrayList<LocationListener> mLocationListeners;
    private final LocationManager mLocationManager;

    public CombinedLocationManager(LocationManager locationManager,
            ArrayList<LocationListener> locationListeners) {
        mLocationManager = locationManager;
        mLocationListeners = locationListeners;
    }

    public boolean isProviderEnabled() {
        return mLocationManager.isProviderEnabled("gps")
                || mLocationManager.isProviderEnabled("network");
    }

    public void removeUpdates() {
        for (LocationListener locationListener : mLocationListeners) {
            mLocationManager.removeUpdates(locationListener);
        }
        mLocationListeners.clear();
    }

    public void requestLocationUpdates(int minTime, int minDistance,
            LocationListener locationListener) {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime,
                minDistance, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance,
                locationListener);
        mLocationListeners.add(locationListener);
    }
}
