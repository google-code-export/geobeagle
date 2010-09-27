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

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.xmlimport.GpxImporter;
import com.google.code.geobeagle.xmlimport.GpxImporterFactory;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionSyncGpxTest extends GeoBeagleTest {
    private Provider<ImportBCachingWorker> importBCachingWorkerProvider;
    private GpxImporterFactory gpxImporterFactory;
    private GpxImporter gpxImporter;
    private ImportBCachingWorker importBCachingWorker;
    private MenuActionSyncGpx menuActionSyncGpx;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        importBCachingWorkerProvider = createMock(Provider.class);
        gpxImporterFactory = createMock(GpxImporterFactory.class);
        gpxImporter = createMock(GpxImporter.class);
        importBCachingWorker = createMock(ImportBCachingWorker.class);
        menuActionSyncGpx = new MenuActionSyncGpx(importBCachingWorkerProvider, gpxImporterFactory);
    }

    @Test
    public void testAct() {
        expect(importBCachingWorkerProvider.get()).andReturn(importBCachingWorker);
        expect(gpxImporterFactory.create()).andReturn(gpxImporter);
        gpxImporter.importGpxs();

        replayAll();
        menuActionSyncGpx.act();
        verifyAll();
    }

    @Test
    public void testAbort() {
        expect(importBCachingWorkerProvider.get()).andReturn(importBCachingWorker);
        expect(gpxImporterFactory.create()).andReturn(gpxImporter);
        gpxImporter.importGpxs();

        expect(gpxImporterFactory.create()).andReturn(gpxImporter).anyTimes();
        expect(importBCachingWorkerProvider.get()).andReturn(importBCachingWorker).anyTimes();
        gpxImporter.abort();
        importBCachingWorker.abort();

        replayAll();
        menuActionSyncGpx.act();
        menuActionSyncGpx.abort();
        verifyAll();
    }
}
