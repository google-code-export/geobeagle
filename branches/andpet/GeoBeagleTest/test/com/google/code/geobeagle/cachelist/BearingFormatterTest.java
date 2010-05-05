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

import com.google.code.geobeagle.activity.cachelist.presenter.AbsoluteBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;

import org.junit.Test;

public class BearingFormatterTest {

    @Test
    public void testFormatRelativeBearing() {
        BearingFormatter relativeBearingFormatter = new RelativeBearingFormatter();
        assertEquals("^", relativeBearingFormatter.formatBearing(-720, 0));
        assertEquals("^", relativeBearingFormatter.formatBearing(44, 0));
        assertEquals(">", relativeBearingFormatter.formatBearing(134, 0));
        assertEquals("v", relativeBearingFormatter.formatBearing(220, 0));
        assertEquals("<", relativeBearingFormatter.formatBearing(314, 0));
        assertEquals("^", relativeBearingFormatter.formatBearing(315, 0));
        assertEquals("<", relativeBearingFormatter.formatBearing(315, 1));
    }

    @Test
    public void testFormatAbsoluteBearing() {
        BearingFormatter absoluteBearingFormatter = new AbsoluteBearingFormatter();
        assertEquals("N", absoluteBearingFormatter.formatBearing(-720, 0));
        assertEquals("NE", absoluteBearingFormatter.formatBearing(44, 0));
        assertEquals("SE", absoluteBearingFormatter.formatBearing(134, 0));
        assertEquals("SW", absoluteBearingFormatter.formatBearing(220, 0));
        assertEquals("NW", absoluteBearingFormatter.formatBearing(314, 0));
        assertEquals("NW", absoluteBearingFormatter.formatBearing(315, 90));
    }
}
