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

package com.google.code.geobeagle.shakewaker;

import com.google.inject.Inject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

class ShakeListener implements SensorEventListener {
    private final PowerManager pm;
    private final ForceThresholdStrategy forceThresholdStrategy;

    @Inject
    ShakeListener(PowerManager pm, ForceThresholdStrategy forceThresholdStrategy) {
        this.pm = pm;
        this.forceThresholdStrategy = forceThresholdStrategy;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (forceThresholdStrategy.exceedsThreshold(event.values)) {
            WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "accel");
            wakeLock.acquire(5000);
            Log.d("GeoBeagle", "shaked; wakelocking: " + event.values[0] + ", " + event.values[1]
                    + ", " + event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
