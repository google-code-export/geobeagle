
package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionsTest {
    @Test
    public void testAct() {
        MenuActionSyncGpx menuActionSyncGpx = PowerMock.createMock(MenuActionSyncGpx.class);

        menuActionSyncGpx.act();

        PowerMock.replayAll();
        new MenuActions(menuActionSyncGpx, null, null, null).act(R.id.menu_sync);
        PowerMock.verifyAll();
    }
}
