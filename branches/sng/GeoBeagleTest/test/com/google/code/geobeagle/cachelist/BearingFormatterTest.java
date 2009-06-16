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

import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;

import org.junit.Test;

public class BearingFormatterTest {

    @Test
    public void testFormatBearing() {
        BearingFormatter bearingFormatter = new BearingFormatter();
        assertEquals("^", bearingFormatter.formatBearing(-720));
        assertEquals("^", bearingFormatter.formatBearing(44));
        assertEquals(">", bearingFormatter.formatBearing(134));
        assertEquals("v", bearingFormatter.formatBearing(220));
        assertEquals("<", bearingFormatter.formatBearing(314));
        assertEquals("^", bearingFormatter.formatBearing(315));
    }
}
