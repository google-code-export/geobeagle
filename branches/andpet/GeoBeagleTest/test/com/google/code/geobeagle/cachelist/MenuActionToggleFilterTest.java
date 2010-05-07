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
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionToggleFilter;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.CachesProviderToggler;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.res.Resources;

@RunWith(PowerMockRunner.class)
public class MenuActionToggleFilterTest {
    @Test
    public void testAct() {
        CachesProviderToggler cachesProviderToggler = PowerMock.createMock(CachesProviderToggler.class);
        CacheListAdapter cacheListAdapter = PowerMock.createMock(CacheListAdapter.class);

        cachesProviderToggler.toggle();
        cacheListAdapter.forceRefresh();

        PowerMock.replayAll();
        final MenuActionToggleFilter menuActionToggleFilter = new MenuActionToggleFilter(
                cachesProviderToggler, cacheListAdapter, null);
        menuActionToggleFilter.act();
        PowerMock.verifyAll();
    }
    
    @Test
    public void testGetLabel() {
        CachesProviderToggler cachesProviderToggler = PowerMock.createMock(CachesProviderToggler.class);
        CacheListAdapter cacheListAdapter = PowerMock.createMock(CacheListAdapter.class);
        Resources resources = PowerMock.createMock(Resources.class);

        EasyMock.expect(cachesProviderToggler.isShowingNearest()).andReturn(true);
        EasyMock.expect(resources.getString(R.string.menu_show_all_caches)).andReturn("show all caches");

        EasyMock.expect(cachesProviderToggler.isShowingNearest()).andReturn(false);
        EasyMock.expect(resources.getString(R.string.menu_show_nearest_caches)).andReturn("show nearest caches");

        PowerMock.replayAll();
        final MenuActionToggleFilter menuActionToggleFilter = new MenuActionToggleFilter(
                cachesProviderToggler, cacheListAdapter, resources);
        assertEquals("show all caches", menuActionToggleFilter.getLabel());
        assertEquals("show nearest caches", menuActionToggleFilter.getLabel());
        PowerMock.verifyAll();
    }
}
