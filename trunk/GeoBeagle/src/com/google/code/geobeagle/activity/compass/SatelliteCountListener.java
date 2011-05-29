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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.R;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

import android.app.Activity;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;

class SatelliteCountListener implements GpsStatus.Listener {
    private final Provider<LocationManager> locationManagerProvider;
    private final Provider<Activity> activityProvider;

    @Inject
    SatelliteCountListener(Provider<Activity> activityProvider,
            Provider<LocationManager> locationManagerProvider) {
        this.locationManagerProvider = locationManagerProvider;
        this.activityProvider = activityProvider;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        try {
            GpsStatus gpsStatus = locationManagerProvider.get().getGpsStatus(null);
            int satelliteCount = 0;
            for (@SuppressWarnings("unused") GpsSatellite gpsSatellite : gpsStatus.getSatellites()) {
                satelliteCount++;
            }
            ((TextView)activityProvider.get().findViewById(R.id.satellite_count))
                    .setText(satelliteCount + " SATs");
        } catch (ProvisionException e) {
            Log.d("GeoBeagle", "ignoring provision exception in satellite count listener");
        }
    }

}
