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

import com.google.code.geobeagle.cachelistactivity.actions.menu.MenuActionToggleFilter;
import com.google.code.geobeagle.cachelistactivity.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.FilterNearestCaches;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionToggleFilterTest {
    @Test
    public void testAct() {
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);

        filterNearestCaches.toggle();
        cacheListRefresh.forceRefresh();

        PowerMock.replayAll();
        new MenuActionToggleFilter(filterNearestCaches, cacheListRefresh).act();
        PowerMock.verifyAll();
    }
}
