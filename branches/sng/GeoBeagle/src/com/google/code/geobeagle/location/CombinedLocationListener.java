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


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/*
 * Listener for the Location control.
 */
public class CombinedLocationListener implements LocationListener {
    private final LocationControlBuffered mLocationControlBuffered;
    private final LocationListener mLocationListener;

    public CombinedLocationListener(LocationControlBuffered locationControlBuffered,
            LocationListener locationListener) {
        mLocationListener = locationListener;
        mLocationControlBuffered = locationControlBuffered;
    }

    public void onLocationChanged(Location location) {
        // Ask the location control to pick the most accurate location (might
        // not be this one).
        // Log.v("GeoBeagle", "onLocationChanged:" + location);
        final Location chosenLocation = mLocationControlBuffered.getLocation();
        // Log.v("GeoBeagle", "onLocationChanged chosen Location" +
        // chosenLocation);
        mLocationListener.onLocationChanged(chosenLocation);
    }

    public void onProviderDisabled(String provider) {
        mLocationListener.onProviderDisabled(provider);
    }

    public void onProviderEnabled(String provider) {
        mLocationListener.onProviderEnabled(provider);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        mLocationListener.onStatusChanged(provider, status, extras);
    }
}
