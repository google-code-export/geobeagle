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

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class ShakeWakerTest {
    private SharedPreferences sharedPreferences;
    private SensorManager sensorManager;
    private ShakeListener shakeListener;
    private Sensor accelerometer;
    private List<Sensor> sensorList;

    @Before
    public void setUp() {
        accelerometer = createMock(Sensor.class);
        sensorManager = createMock(SensorManager.class);
        shakeListener = createMock(ShakeListener.class);
        sharedPreferences = createMock(SharedPreferences.class);
        sensorList = new ArrayList<Sensor>();
    }

    @Test
    public void testShakeWakerRegister() {
        sensorList.add(accelerometer);
        expect(sharedPreferences.getString(ShakeWaker.SHAKE_WAKE, "0")).andReturn("5");
        expect(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)).andReturn(sensorList);
        expect(
                sensorManager.registerListener(shakeListener, accelerometer,
                        SensorManager.SENSOR_DELAY_UI)).andReturn(true);
        shakeListener.acquireWakeLock(5);
        replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        verifyAll();
    }

    @Test
    public void testShakeWakerRegisterNoPreferenceSet() {
        sensorList.add(accelerometer);
        expect(sharedPreferences.getString(ShakeWaker.SHAKE_WAKE, "0")).andReturn("0");
        shakeListener.removeAllWakeLocks();
        replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        verifyAll();
    }

    @Test
    public void testShakeWakerUnregister() {
        sensorManager.unregisterListener(shakeListener);
        shakeListener.removeAllWakeLocks();
        replayAll();

        new ShakeWaker(null, sensorManager, shakeListener).unregister();
        verifyAll();
    }

    @Test
    public void testShakeWakerRegisterNoAccelerometer() {
        expect(sharedPreferences.getString(ShakeWaker.SHAKE_WAKE, "0")).andReturn("5");
        shakeListener.acquireWakeLock(5);
        expect(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)).andReturn(sensorList);
        replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        verifyAll();
    }
}
