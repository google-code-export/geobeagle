
package com.google.code.geobeagle.ui.cachelist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionRefreshTest {
    @Test
    public void testAct() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);

        geocacheListPresenter.doSort();

        PowerMock.replayAll();
        new MenuActionRefresh(geocacheListPresenter).act();
        PowerMock.verifyAll();
    }
}
