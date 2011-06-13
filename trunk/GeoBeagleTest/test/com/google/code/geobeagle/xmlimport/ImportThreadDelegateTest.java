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

package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        GpxAndZipFilenameFilter.class, File.class, GpxAndZipFiles.class, IGpxReader.class,
        Log.class
})
public class ImportThreadDelegateTest extends GeoBeagleTest {

    private DbFrontend dbFrontend;
    private SyncCollectingParameter syncCollectingParameter;
    private GpxSyncer gpxSyncer;
    private ImportBCachingWorker importBCachingWorker;
    private ErrorDisplayer errorDisplayer;
    private BCachingStartTime bcachingStartTime;

    @Before
    public void setUp() {
        dbFrontend = createMock(DbFrontend.class);
        syncCollectingParameter = createMock(SyncCollectingParameter.class);
        gpxSyncer = createMock(GpxSyncer.class);
        importBCachingWorker = createMock(ImportBCachingWorker.class);
        errorDisplayer = createMock(ErrorDisplayer.class);
        bcachingStartTime = createMock(BCachingStartTime.class);
    }

    @Test
    public void testRun() throws FileNotFoundException, IOException, ImportException,
            CancelException, BCachingException {
        syncCollectingParameter.reset();
        gpxSyncer.sync(syncCollectingParameter);
        importBCachingWorker.sync(syncCollectingParameter);
        expect(syncCollectingParameter.getLog()).andReturn("LOG");
        errorDisplayer.displayError(R.string.string, "LOG");
        bcachingStartTime.clearStartTime();
        dbFrontend.forceUpdate();

        replayAll();
        ImportThread importThread = new ImportThread(gpxSyncer, importBCachingWorker,
                errorDisplayer, bcachingStartTime, dbFrontend, syncCollectingParameter);
        importThread.run();
        verifyAll();
    }
}
