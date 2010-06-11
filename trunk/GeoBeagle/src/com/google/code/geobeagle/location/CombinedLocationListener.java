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

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.inject.Inject;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/*
 * Listener for the Location control.
 */
public class CombinedLocationListener implements LocationListener {
    private final LocationControlBuffered mLocationControlBuffered;
    private final LocationListener mLocationListener;
    private final ActivityVisible mActivityVisible;

    @Inject
    public CombinedLocationListener(LocationControlBuffered locationControlBuffered,
            LocationListener locationListener, ActivityVisible activityVisible) {
        mLocationListener = locationListener;
        mLocationControlBuffered = locationControlBuffered;
        mActivityVisible = activityVisible;
    }

    public void onLocationChanged(Location location) {
        if (!mActivityVisible.getVisible())
            return;
        // Ask the location control to pick the most accurate location (might
        // not be this one).
        // Log.d("GeoBeagle", "onLocationChanged:" + location);
        final Location chosenLocation = mLocationControlBuffered.getLocation();
        // Log.d("GeoBeagle", "onLocationChanged chosen Location" +
        // chosenLocation);
        mLocationListener.onLocationChanged(chosenLocation);
    }

    public void onProviderDisabled(String provider) {
        if (!mActivityVisible.getVisible())
            return;
        mLocationListener.onProviderDisabled(provider);
    }

    public void onProviderEnabled(String provider) {
        if (!mActivityVisible.getVisible())
            return;
        mLocationListener.onProviderEnabled(provider);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (!mActivityVisible.getVisible())
            return;
        mLocationListener.onStatusChanged(provider, status, extras);
    }
}
