
package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.shakewaker.ShakeWaker;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;

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
class GeoBeagleSensors {
    private final SensorManager sensorManager;
    private final RadarView radarView;
    private final SharedPreferences sharedPreferences;
    private final CompassListener compassListener;
    private final ShakeWaker shakeWaker;
    private final Provider<LocationManager> locationManagerProvider;
    private final SatelliteCountListener satelliteCountListener;

    @Inject
    GeoBeagleSensors(SensorManager sensorManager,
            RadarView radarView,
            SharedPreferences sharedPreferences,
            CompassListener compassListener,
            ShakeWaker shakeWaker,
            Provider<LocationManager> locationManagerProvider,
            SatelliteCountListener satelliteCountListener) {
        this.sensorManager = sensorManager;
        this.radarView = radarView;
        this.sharedPreferences = sharedPreferences;
        this.compassListener = compassListener;
        this.shakeWaker = shakeWaker;
        this.locationManagerProvider = locationManagerProvider;
        this.satelliteCountListener = satelliteCountListener;
    }

    public void registerSensors() {
        radarView.handleUnknownLocation();
        radarView.setUseImperial(sharedPreferences.getBoolean("imperial", false));
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(compassListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(compassListener, magnetometer, SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(radarView, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(radarView, magnetometer, SensorManager.SENSOR_DELAY_UI);

        locationManagerProvider.get().addGpsStatusListener(satelliteCountListener);

        shakeWaker.register();
    }

    public void unregisterSensors() {
        sensorManager.unregisterListener(radarView);
        sensorManager.unregisterListener(compassListener);
        shakeWaker.unregister();
        locationManagerProvider.get().removeGpsStatusListener(satelliteCountListener);
    }
}
