
package com.google.code.geobeagle.cachelist;

import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.xmlimport.GpxImporter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionSyncGpxTest {
    @Test
    public void testAct() {
        GpxImporter gpxImporter = PowerMock.createMock(GpxImporter.class);
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);

        gpxImporter.importGpxs(cacheListRefresh);

        PowerMock.replayAll();
        new MenuActionSyncGpx(gpxImporter, cacheListRefresh).act();
        PowerMock.verifyAll();
    }
}
