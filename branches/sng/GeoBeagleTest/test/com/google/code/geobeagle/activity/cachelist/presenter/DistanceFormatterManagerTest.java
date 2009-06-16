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

package com.google.code.geobeagle.activity.cachelist.presenter;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;

@RunWith(PowerMockRunner.class)
public class DistanceFormatterManagerTest {
    private SharedPreferences mSharedPreferences;
    private HasDistanceFormatter mHasDistanceFormatter;
    private DistanceFormatterImperial mDistanceFormatterImperial;
    private DistanceFormatterMetric mDistanceFormatterMetric;

    @Before
    public void setup() {
        mSharedPreferences = PowerMock.createMock(SharedPreferences.class);
        mDistanceFormatterImperial = PowerMock.createMock(DistanceFormatterImperial.class);
        mDistanceFormatterMetric = PowerMock.createMock(DistanceFormatterMetric.class);
        mHasDistanceFormatter = PowerMock.createMock(HasDistanceFormatter.class);
    }

    @Test
    public void testSetFormatterImperial() {
        EasyMock.expect(mSharedPreferences.getBoolean("imperial", false)).andReturn(true);
        mHasDistanceFormatter.setDistanceFormatter(mDistanceFormatterImperial);

        PowerMock.replayAll();
        final DistanceFormatterManager distanceFormatterManager = new DistanceFormatterManager(
                mSharedPreferences, mDistanceFormatterImperial, mDistanceFormatterMetric);
        distanceFormatterManager.addHasDistanceFormatter(mHasDistanceFormatter);
        distanceFormatterManager.setFormatter();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetFormatterMetric() {
        EasyMock.expect(mSharedPreferences.getBoolean("imperial", false)).andReturn(false);
        mHasDistanceFormatter.setDistanceFormatter(mDistanceFormatterMetric);

        PowerMock.replayAll();
        final DistanceFormatterManager distanceFormatterManager = new DistanceFormatterManager(
                mSharedPreferences, mDistanceFormatterImperial, mDistanceFormatterMetric);
        distanceFormatterManager.addHasDistanceFormatter(mHasDistanceFormatter);
        distanceFormatterManager.setFormatter();
        PowerMock.verifyAll();
    }
}
