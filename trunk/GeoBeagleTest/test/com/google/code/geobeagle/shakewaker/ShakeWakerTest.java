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

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
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

    @Before
    public void setUp() {
        sensorManager = PowerMock.createMock(SensorManager.class);
        shakeListener = PowerMock.createMock(ShakeListener.class);
        sharedPreferences = PowerMock.createMock(SharedPreferences.class);
    }

    @Test
    public void testShakeWakerRegister() {
        Sensor accelerometer = PowerMock.createMock(Sensor.class);
        List<Sensor> sensorList = new ArrayList<Sensor>();

        sensorList.add(accelerometer);
        EasyMock.expect(sharedPreferences.getBoolean(ShakeWaker.SHAKE_WAKE, false)).andReturn(true);
        EasyMock.expect(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)).andReturn(
                sensorList);
        EasyMock.expect(
                sensorManager.registerListener(shakeListener, accelerometer,
                        SensorManager.SENSOR_DELAY_UI)).andReturn(true);
        PowerMock.replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        PowerMock.verifyAll();
    }

    @Test
    public void testShakeWakerRegisterNoPreferenceSet() {
        Sensor accelerometer = PowerMock.createMock(Sensor.class);
        List<Sensor> sensorList = new ArrayList<Sensor>();

        sensorList.add(accelerometer);
        EasyMock.expect(sharedPreferences.getBoolean(ShakeWaker.SHAKE_WAKE, false))
                .andReturn(false);
        PowerMock.replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        PowerMock.verifyAll();
    }

    @Test
    public void testShakeWakerUnregister() {
        sensorManager.unregisterListener(shakeListener);
        PowerMock.replayAll();

        new ShakeWaker(null, sensorManager, shakeListener).unregister();
        PowerMock.verifyAll();
    }

    @Test
    public void testShakeWakerRegisterNoAccelerometer() {
        List<Sensor> sensorList = new ArrayList<Sensor>();

        EasyMock.expect(sharedPreferences.getBoolean(ShakeWaker.SHAKE_WAKE, false)).andReturn(true);
        EasyMock.expect(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)).andReturn(
                sensorList);
        PowerMock.replayAll();

        new ShakeWaker(sharedPreferences, sensorManager, shakeListener).register();
        PowerMock.verifyAll();
    }
}
