
package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.shakewaker.ShakeWaker;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.LocationManager;

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
        sensorManager.registerListener(radarView, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(compassListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);
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
