
package com.google.code.geobeagle;

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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class Azimuth {

    private double currentAzimuth;
    private float[] gravity;
    private float[] geomagnetic;
    private float rotationMatrix[] = new float[9];
    private float identityMatrix[] = new float[9];
    private float orientations[] = new float[3];

    public Azimuth() {
        rotationMatrix = new float[9];
        identityMatrix = new float[9];
        orientations = new float[9];
    }

    public void sensorChanged(SensorEvent event) {
        boolean sensorReady = false;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone();
            sensorReady = true;
        }
        if (gravity == null || geomagnetic == null || !sensorReady) {
            return;
        }
        if (!SensorManager.getRotationMatrix(rotationMatrix, identityMatrix, gravity, geomagnetic)) {
            return;
        }
        SensorManager.getOrientation(rotationMatrix, orientations);
        currentAzimuth = Math.toDegrees(orientations[0]);
    }

    public double getAzimuth() {
        return currentAzimuth;
    }
}
