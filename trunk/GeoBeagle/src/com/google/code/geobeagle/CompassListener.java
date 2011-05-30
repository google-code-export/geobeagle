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

import com.google.inject.Inject;

import roboguice.inject.ContextScoped;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

@ContextScoped
public class CompassListener implements SensorEventListener {
    private final Refresher refresher;
    private final LocationControlBuffered locationControlBuffered;
    private final Azimuth azimuth;
    private double lastAzimuth;

    public double getLastAzimuth() {
        return lastAzimuth;
    }

    @Inject
    public CompassListener(Refresher refresher,
            LocationControlBuffered locationControlBuffered,
            Azimuth azimuth) {
        this.refresher = refresher;
        this.locationControlBuffered = locationControlBuffered;
        this.lastAzimuth = -1440f;
        this.azimuth = azimuth;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        azimuth.sensorChanged(event);
        double currentAzimuth = azimuth.getAzimuth();
//        Log.d("GeoBeagle", "azimuth now " + mLastAzimuth + ", " + currentAzimuth);
        if (Math.abs(currentAzimuth - lastAzimuth) > 5) {
            locationControlBuffered.setAzimuth(((int)currentAzimuth / 5) * 5);
            refresher.refresh();
            lastAzimuth = currentAzimuth;
        }
    }
}
