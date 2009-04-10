
package com.google.code.geobeagle.ui.cachelist;

import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class MenuActionRefreshTest {
    @Test
    public void testAct() {
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);

        geocacheListPresenter.onResume();

        PowerMock.replayAll();
        new MenuActionRefresh(geocacheListPresenter).act();
        PowerMock.verifyAll();
    }
}
