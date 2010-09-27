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

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

public class ShakeWaker {

    static final String SHAKE_WAKE = "shake-wake";
    private final SharedPreferences sharedPreferences;
    private final SensorManager sensorManager;
    private final ShakeListener shakeListener;

    @Inject
    ShakeWaker(SharedPreferences sharedPreferences,
            SensorManager sensorManager,
            ShakeListener shakeListener) {
        this.sharedPreferences = sharedPreferences;
        this.sensorManager = sensorManager;
        this.shakeListener = shakeListener;
    }

    public void register() {
        boolean shakeWake = sharedPreferences.getBoolean(SHAKE_WAKE, false);
        if (!shakeWake)
            return;
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() <= 0)
            return;
        sensorManager.registerListener(shakeListener, sensorList.get(0),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void unregister() {
        sensorManager.unregisterListener(shakeListener);
    }
}

