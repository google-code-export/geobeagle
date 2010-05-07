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

package com.google.code.geobeagle.database;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class WhereFactoryTest {

    @Test
    public void testGetWhereFactoryAllCaches() {
        assertEquals(null, new WhereFactoryAllCaches().getWhere(null, 0, 0));
    }

    @Test
    public void testGetWhereFactoryFixedArea() {
        assertEquals("Latitude >= 0.0 AND Latitude < 2.0 AND Longitude >= 1.0 AND Longitude < 3.0",
                new WhereFactoryFixedArea(0, 1, 2, 3).getWhere(null, -1, -1));
    }

}
