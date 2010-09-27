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

package com.google.code.geobeagle.bcaching;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.Message;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Message.class, Log.class, ImportBCachingWorker.class
})
public class ImportBCachingWorkerTest extends GeoBeagleTest {
    private DetailsReaderImport detailsReaderImport;
    private ErrorDisplayer errorDisplayer;
    private ProgressHandler progressHandler;
    private ProgressManager progressManager;
    private CacheListCursor cursor;
    private UpdateFlag updateFlag;

    @Before
    public void setUp() {
        mockStatic(Message.class);
        mockStatic(System.class);
        progressHandler = createMock(ProgressHandler.class);
        progressManager = createMock(ProgressManager.class);
        errorDisplayer = createMock(ErrorDisplayer.class);
        detailsReaderImport = createMock(DetailsReaderImport.class);
        updateFlag = createMock(UpdateFlag.class);
        cursor = createMock(CacheListCursor.class);
    }

    @Test
    public void testWorkerNoCaches() throws BCachingException {
        updateFlag.setUpdatesEnabled(false);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(cursor.open()).andReturn(false);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);
        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        updateFlag.setUpdatesEnabled(true);

        replayAll();
        ImportBCachingWorker importBCachingWorker = new ImportBCachingWorker(progressHandler,
                progressManager, null, null, null, cursor, updateFlag);
        importBCachingWorker.run();
        verifyAll();
    }

    @Test
    public void testWorkerOneChunk() throws BCachingException {
        updateFlag.setUpdatesEnabled(false);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(cursor.open()).andReturn(true);

        expect(cursor.readCaches()).andReturn(true);
        expect(cursor.getCacheIds()).andReturn("1,2,3");
        expect(detailsReaderImport.loadCacheDetails("1,2,3")).andReturn(true);
        cursor.increment();
        expect(cursor.readCaches()).andReturn(false);
        cursor.close();
        updateFlag.setUpdatesEnabled(true);

        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);

        replayAll();
        new ImportBCachingWorker(progressHandler, progressManager, null, detailsReaderImport, null,
                cursor, updateFlag).run();
        verifyAll();
    }

    @Test
    public void testWorkerRaise() throws BCachingException {
        updateFlag.setUpdatesEnabled(false);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(cursor.open())
                .andThrow(new BCachingException("io exception"));
        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);
        errorDisplayer.displayError(R.string.problem_importing_from_bcaching, "io exception");
        updateFlag.setUpdatesEnabled(true);

        replayAll();
        ImportBCachingWorker importBCachingWorker = new ImportBCachingWorker(progressHandler,
                progressManager, errorDisplayer, null, null, cursor, updateFlag);
        importBCachingWorker.run();
        verifyAll();
    }

    @Test
    public void testWorkerTwoChunks() throws BCachingException {
        updateFlag.setUpdatesEnabled(false);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(cursor.open()).andReturn(true);

        expect(cursor.readCaches()).andReturn(true);
        expect(cursor.getCacheIds()).andReturn("1,2,3");
        expect(detailsReaderImport.loadCacheDetails("1,2,3")).andReturn(true);
        cursor.increment();

        expect(cursor.readCaches()).andReturn(true);
        expect(cursor.getCacheIds()).andReturn("4,5,6");
        expect(detailsReaderImport.loadCacheDetails("4,5,6")).andReturn(true);
        cursor.increment();

        expect(cursor.readCaches()).andReturn(false);
        cursor.close();
        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);
        updateFlag.setUpdatesEnabled(true);

        replayAll();
        new ImportBCachingWorker(progressHandler, progressManager, null, detailsReaderImport, null,
                cursor, updateFlag).run();
        verifyAll();
    }
}
