
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
import android.util.Log;

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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values.clone();
        if (gravity == null || geomagnetic == null) {
            return;
        }
        if (!SensorManager.getRotationMatrix(rotationMatrix, identityMatrix, gravity, geomagnetic)) {
            return;
        }
//        float rotationMatrixOut[] = new float[9];

//        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
//                SensorManager.AXIS_Z, rotationMatrixOut);
        SensorManager.getOrientation(rotationMatrix, orientations);
//        currentAzimuth = currentAzimuth - (currentAzimuth - Math.toDegrees(orientations[0])) / 2.0;
        currentAzimuth = Math.toDegrees(orientations[0]);
//        float incl = SensorManager.getInclination(identityMatrix);
//        final float rad2deg = (float)(180.0f/Math.PI);
//        Log.d("Compass", "yaw: " + (int)(orientations[0]*rad2deg) +
//                "  pitch: " + (int)(orientations[1]*rad2deg) +
//                "  roll: " + (int)(orientations[2]*rad2deg) +
//                "  incl: " + (int)(incl*rad2deg)
//                );
        // Log.d("GeoBeagle", "azimuth: " + event.sensor.getType() + ", " +
        // orientations[0] + ", "
        // + currentAzimuth);
    }

    public double getAzimuth() {
        return currentAzimuth;
    }
}
