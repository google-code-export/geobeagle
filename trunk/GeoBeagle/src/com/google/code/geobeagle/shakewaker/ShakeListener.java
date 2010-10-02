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
import android.os.Handler;
import android.util.Log;

class ShakeListener implements SensorEventListener {
    private final ForceThresholdStrategy forceThresholdStrategy;
    private final Handler handler;
    private int shakeWakeDuration;
    private final WakeLockReleaser wakeLockReleaser;
    private final WakeLockView wakeLockView;

    @Inject
    ShakeListener(ForceThresholdStrategy forceThresholdStrategy,
            Handler handler,
            WakeLockReleaser wakeLockReleaser,
            WakeLockView wakeLockView) {
        this.forceThresholdStrategy = forceThresholdStrategy;
        this.handler = handler;
        this.wakeLockReleaser = wakeLockReleaser;
        this.wakeLockView = wakeLockView;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (forceThresholdStrategy.exceedsThreshold(event.values)) {
            renewWakeLock();
            Log.d("GeoBeagle", "shaked; wakelocking: " + event.values[0] + ", " + event.values[1]
                    + ", " + event.values[2]);
        }
    }

    private void renewWakeLock() {
        Log.d("GeoBeagle", "Acquiring wakelock");
        wakeLockView.keepScreenOn(true);
        handler.removeCallbacks(wakeLockReleaser);
        handler.postDelayed(wakeLockReleaser, shakeWakeDuration * 1000);
    }

    void acquireWakeLock(int shakeWakeDuration) {
        this.shakeWakeDuration = shakeWakeDuration;
        renewWakeLock();
    }

    void removeAllWakeLocks() {
        wakeLockView.keepScreenOn(false);
        handler.removeCallbacks(wakeLockReleaser);
    }
}
