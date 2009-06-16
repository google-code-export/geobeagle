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

package com.google.code.geobeagle.cachelist;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterMetric;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatter;

import org.junit.Test;

import java.util.Locale;

public class DistanceFormatterTest {
    @Test
    public void testKilometers() {
        DistanceFormatter distanceFormatter = new DistanceFormatterMetric();
        Locale.setDefault(Locale.ENGLISH);
        assertEquals("1.23km", distanceFormatter.formatDistance(1234.5f));
    }

    @Test
    public void testMeters() {
        DistanceFormatter distanceFormatter = new DistanceFormatterMetric();
        assertEquals("3m", distanceFormatter.formatDistance(3.5f));
    }

    @Test
    public void testNoGps() {
        DistanceFormatter distanceFormatter = new DistanceFormatterMetric();
        assertEquals("", distanceFormatter.formatDistance(-1f));
    }

}
