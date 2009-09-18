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

package com.google.code.geobeagle.activity;

import static org.junit.Assert.*;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.res.Resources;

@RunWith(PowerMockRunner.class)
public class MenuActionsTest {
    @Test
    public void testAct() {
        MenuActionSyncGpx menuActionSyncGpx = PowerMock.createMock(MenuActionSyncGpx.class);
        Resources resources = PowerMock.createMock(Resources.class);
        EasyMock.expect(menuActionSyncGpx.getId()).andReturn(R.string.menu_sync).anyTimes();
        menuActionSyncGpx.act();

        PowerMock.replayAll();
        final MenuActions menuActions = new MenuActions(resources);
        menuActions.add(menuActionSyncGpx);

        assertTrue(menuActions.act(R.string.menu_sync));
        assertFalse(menuActions.act(R.string.menu_cache_list));
        PowerMock.verifyAll();
    }

}
