
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
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);

        gpxImporter.importGpxs(geocacheListPresenter);

        PowerMock.replayAll();
        new MenuActionSyncGpx(gpxImporter, geocacheListPresenter).act();
        PowerMock.verifyAll();
    }
}
