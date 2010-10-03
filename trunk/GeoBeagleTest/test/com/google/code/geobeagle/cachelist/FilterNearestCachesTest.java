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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.WhereFactoryAllCaches;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches;
import com.google.code.geobeagle.database.filter.FilterNearestCaches;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class FilterNearestCachesTest {

    @Test
    public void test() {
        WhereFactoryAllCaches whereFactoryAllCaches = PowerMock
                .createMock(WhereFactoryAllCaches.class);
        WhereFactoryNearestCaches whereFactoryNearestCaches = PowerMock
                .createMock(WhereFactoryNearestCaches.class);

        PowerMock.replayAll();
        FilterNearestCaches filterNearestCaches = new FilterNearestCaches(whereFactoryAllCaches,
                whereFactoryNearestCaches);
        assertEquals(whereFactoryNearestCaches, filterNearestCaches.getWhereFactory());
        assertEquals(R.string.menu_show_all_caches, filterNearestCaches.getMenuString());
        assertEquals(R.string.cache_list_title, filterNearestCaches.getTitleText());

        filterNearestCaches.toggle();
        assertEquals(R.string.menu_show_nearest_caches, filterNearestCaches.getMenuString());
        assertEquals(whereFactoryAllCaches, filterNearestCaches.getWhereFactory());
        assertEquals(R.string.cache_list_title_all, filterNearestCaches.getTitleText());

        filterNearestCaches.toggle();
        assertEquals(R.string.menu_show_all_caches, filterNearestCaches.getMenuString());
        assertEquals(whereFactoryNearestCaches, filterNearestCaches.getWhereFactory());
        assertEquals(R.string.cache_list_title, filterNearestCaches.getTitleText());
        PowerMock.verifyAll();
    }
}
