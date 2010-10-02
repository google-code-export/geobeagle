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

import com.google.code.geobeagle.gpsstatuswidget.InflatedGpsStatusWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;

class GpsStatusListener implements GpsStatus.Listener {

    private final InflatedGpsStatusWidget inflatedGpsStatusWidget;
    private final Provider<LocationManager> locationManagerProvider;

    @Inject
    public GpsStatusListener(InflatedGpsStatusWidget inflatedGpsStatusWidget,
            Provider<LocationManager> locationManagerProvider) {
        this.inflatedGpsStatusWidget = inflatedGpsStatusWidget;
        this.locationManagerProvider = locationManagerProvider;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        try {
            GpsStatus gpsStatus = locationManagerProvider.get().getGpsStatus(null);
            int satelliteCount = 0;
            for (@SuppressWarnings("unused") GpsSatellite gpsSatellite : gpsStatus.getSatellites()) {
                satelliteCount++;
            }
            inflatedGpsStatusWidget.getDelegate().setProvider("SAT: " + satelliteCount);
        } catch (ProvisionException e) {
            Log.d("GeoBeagle", "Ignoring provision exception during onGpsStatusChanged: " + e);
        }
    }

}
