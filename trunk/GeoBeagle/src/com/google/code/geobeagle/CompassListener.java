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
import android.util.Log;

@ContextScoped
public class CompassListener implements SensorEventListener {
    private final Refresher mRefresher;
    private final LocationControlBuffered mLocationControlBuffered;
    private final Azimuth mAzimuth;
    private double mLastAzimuth;

    public double getLastAzimuth() {
        return mLastAzimuth;
    }

    @Inject
    public CompassListener(Refresher refresher,
            LocationControlBuffered locationControlBuffered,
            Azimuth azimuth) {
        mRefresher = refresher;
        mLocationControlBuffered = locationControlBuffered;
        mLastAzimuth = -1440f;
        mAzimuth = azimuth;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mAzimuth.sensorChanged(event);
        double currentAzimuth = mAzimuth.getAzimuth();
//        Log.d("GeoBeagle", "azimuth now " + mLastAzimuth + ", " + currentAzimuth);
        if (Math.abs(currentAzimuth - mLastAzimuth) > 5) {
            mLocationControlBuffered.setAzimuth(((int)currentAzimuth / 5) * 5);
            mRefresher.refresh();
            mLastAzimuth = currentAzimuth;
        }
    }
}
