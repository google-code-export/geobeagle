
package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.io.GpxImporter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionSyncGpxTest {
    @Test
    public void testAct() {
        GpxImporter gpxImporter = PowerMock.createMock(GpxImporter.class);
        MenuActionRefresh menuActionRefresh = PowerMock.createMock(MenuActionRefresh.class);

        gpxImporter.importGpxs(menuActionRefresh);

        PowerMock.replayAll();
        new MenuActionSyncGpx(gpxImporter, menuActionRefresh).act();
        PowerMock.verifyAll();
    }
}
