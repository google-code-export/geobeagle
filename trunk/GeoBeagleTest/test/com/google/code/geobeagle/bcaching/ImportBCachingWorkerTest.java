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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.bcaching.BCachingLastUpdated.LastReadPosition;
import com.google.code.geobeagle.bcaching.CacheListCursor.TimeRecorder;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingList;
import com.google.code.geobeagle.bcaching.communication.BCachingListImporter;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.Message;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Message.class, Log.class, ImportBCachingWorker.class, TimeRecorder.class
})
public class ImportBCachingWorkerTest extends GeoBeagleTest {
    private BCachingLastUpdated bcachingLastUpdated;
    private BCachingListImporter bcachingListFactory;
    private BCachingList bcachingListFirst;
    private DetailsReaderImport detailsReaderImport;
    private ErrorDisplayer errorDisplayer;
    private ProgressHandler progressHandler;
    private ProgressManager progressManager;
    private CacheListCursor cacheListCursor;
    private LastReadPosition lastReadPosition;

    @Before
    public void setUp() {
        mockStatic(Message.class);
        mockStatic(System.class);
        progressHandler = createMock(ProgressHandler.class);
        progressManager = createMock(ProgressManager.class);
        bcachingListFirst = createMock(BCachingList.class);
        bcachingLastUpdated = createMock(BCachingLastUpdated.class);
        bcachingListFactory = createMock(BCachingListImporter.class);
        errorDisplayer = createMock(ErrorDisplayer.class);
        detailsReaderImport = createMock(DetailsReaderImport.class);
        lastReadPosition = createMock(LastReadPosition.class);
        cacheListCursor = new CacheListCursor(bcachingLastUpdated, progressManager,
                progressHandler, bcachingListFactory, new TimeRecorder(bcachingLastUpdated),
                lastReadPosition);
    }

    @Test
    public void testWorkerNoCaches() throws BCachingException {
        expect(System.currentTimeMillis()).andReturn(1234L);

        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(bcachingLastUpdated.getLastUpdateTime()).andReturn(1000L);
        bcachingListFactory.setStartTime("1000");
        expect(bcachingListFactory.getTotalCount()).andReturn(0);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);

        replayAll();
        new ImportBCachingWorker(progressHandler, progressManager, null, null, null,
                cacheListCursor).run();
        verifyAll();
    }

    @Test
    public void testWorkerOneCache() throws BCachingException {
        BCachingList bcachingListLast = createMock(BCachingList.class);

        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(System.currentTimeMillis()).andReturn(8888L);
        expect(bcachingLastUpdated.getLastUpdateTime()).andReturn(1000L);
        bcachingListFactory.setStartTime("1000");
        expect(bcachingListFactory.getTotalCount()).andReturn(1);
        progressManager.update(progressHandler, ProgressMessage.SET_MAX, 1);
        expect(lastReadPosition.getSaved()).andReturn(0);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, 0);

        expect(lastReadPosition.get()).andReturn(0);
        expect(bcachingListFactory.getCacheList("0")).andReturn(bcachingListFirst);
        expect(bcachingListFirst.getCachesRead()).andReturn(1);

        expect(bcachingListFirst.getCacheIds()).andReturn("GC1234");
        expect(detailsReaderImport.loadCacheDetails("GC1234")).andReturn(true);
      
        expect(lastReadPosition.get()).andReturn(0);
        expect(bcachingListFirst.getCachesRead()).andReturn(1);
        lastReadPosition.put(1);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, 1);

        expect(lastReadPosition.get()).andReturn(1);
        expect(bcachingListFactory.getCacheList("1")).andReturn(bcachingListLast);
        expect(bcachingListLast.getCachesRead()).andReturn(0);

        bcachingLastUpdated.putLastUpdateTime(8888L);
        lastReadPosition.put(0);
        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);

        replayAll();
        new ImportBCachingWorker(progressHandler, progressManager, null, detailsReaderImport, null,
                cacheListCursor).run();
        verifyAll();
    }

    @Test
    public void testWorkerRaise() throws BCachingException {
        expect(System.currentTimeMillis()).andReturn(1234L);

        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(bcachingLastUpdated.getLastUpdateTime()).andReturn(1000L);
        expect(bcachingListFactory.getTotalCount()).andThrow(new BCachingException("io exception"));
        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);
        errorDisplayer.displayError(R.string.problem_importing_from_bcaching, "io exception");

        replayAll();
        new ImportBCachingWorker(progressHandler, progressManager, errorDisplayer, null, null,
                cacheListCursor).run();
        verifyAll();
    }

    @Test
    public void testWorkerSixtyCaches() throws BCachingException {
        BCachingList bcachingListSecond = createMock(BCachingList.class);
        BCachingList bcachingListLast = createMock(BCachingList.class);

        expect(System.currentTimeMillis()).andReturn(8888L);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(bcachingLastUpdated.getLastUpdateTime()).andReturn(1000L);
        bcachingListFactory.setStartTime("1000");
        expect(bcachingListFactory.getTotalCount()).andReturn(60);
        progressManager.update(progressHandler, ProgressMessage.SET_MAX, 60);

        expect(bcachingListFactory.getCacheList("0")).andReturn(bcachingListFirst);

        expect(bcachingListFirst.getCachesRead()).andReturn(50);
        expect(bcachingListFirst.getCacheIds()).andReturn("GC1234,etc");
        expect(detailsReaderImport.loadCacheDetails("GC1234,etc")).andReturn(true);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, 50);

        expect(bcachingListFactory.getCacheList("50")).andReturn(bcachingListSecond);

        expect(bcachingListSecond.getCachesRead()).andReturn(10);
        expect(bcachingListSecond.getCacheIds()).andReturn("GC456,etc");
        expect(detailsReaderImport.loadCacheDetails("GC456,etc")).andReturn(true);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, 60);

        expect(bcachingListFactory.getCacheList("60")).andReturn(bcachingListLast);
        expect(bcachingListLast.getCachesRead()).andReturn(0);

        progressManager.update(progressHandler, ProgressMessage.DONE, 0);
        bcachingLastUpdated.putLastUpdateTime(8888L);

        replayAll();
        new ImportBCachingWorker(progressHandler, progressManager, null, detailsReaderImport, null,
                cacheListCursor).run();
        verifyAll();
    }
}
