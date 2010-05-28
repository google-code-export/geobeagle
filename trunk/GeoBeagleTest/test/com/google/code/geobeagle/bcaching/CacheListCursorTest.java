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
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.bcaching.BCachingLastUpdated.LastReadPosition;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingList;
import com.google.code.geobeagle.bcaching.communication.BCachingListImporter;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        CacheListCursor.class, Log.class
})
public class CacheListCursorTest extends GeoBeagleTest {

    private ProgressHandler progressHandler;
    private ProgressManager progressManager;
    private BCachingListImporter bcachingListImporter;
    private BCachingLastUpdated bcachingLastUpdated;
    private TimeRecorder timeRecorder;
    private LastReadPosition lastReadPosition;
    private BCachingList bcachingList;

    @Before
    public void setUp() {
        mockStatic(System.class);
        bcachingLastUpdated = createMock(BCachingLastUpdated.class);
        bcachingListImporter = createMock(BCachingListImporter.class);
        progressManager = createMock(ProgressManager.class);
        progressHandler = createMock(ProgressHandler.class);
        timeRecorder = createMock(TimeRecorder.class);
        lastReadPosition = createMock(LastReadPosition.class);
        bcachingList = createMock(BCachingList.class);
    }

    @Test
    public void testOpen() throws BCachingException {
        expect(bcachingLastUpdated.getLastUpdateTime()).andReturn(900L);
        bcachingListImporter.setStartTime("900");
        expect(bcachingListImporter.getTotalCount()).andReturn(128);
        progressManager.update(progressHandler, ProgressMessage.SET_MAX, 128);
        expect(lastReadPosition.getSaved()).andReturn(25);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, 25);

        replayAll();
        assertTrue(new CacheListCursor(bcachingLastUpdated, progressManager, progressHandler,
                bcachingListImporter, timeRecorder, lastReadPosition).open());
        verifyAll();
    }

    @Test
    public void testOpenNoCaches() throws BCachingException {
        expect(bcachingLastUpdated.getLastUpdateTime()).andReturn(900L);
        bcachingListImporter.setStartTime("900");
        expect(bcachingListImporter.getTotalCount()).andReturn(0);

        replayAll();
        assertFalse(new CacheListCursor(bcachingLastUpdated, progressManager, progressHandler,
                bcachingListImporter, timeRecorder, lastReadPosition).open());
        verifyAll();
    }

    @Test
    public void testClose() throws BCachingException {
        expect(bcachingListImporter.getBCachingList()).andReturn(bcachingList);
        expect(bcachingList.getServerTime()).andReturn("1234567");
        timeRecorder.saveTime("1234567");
        lastReadPosition.put(0);

        replayAll();
        new CacheListCursor(bcachingLastUpdated, progressManager, progressHandler,
                bcachingListImporter, timeRecorder, lastReadPosition).close();
        verifyAll();
    }

    @Test
    public void testReadCaches() throws BCachingException {
        expect(lastReadPosition.get()).andReturn(7);
        bcachingListImporter.readCacheList("7");
        expect(bcachingListImporter.getBCachingList()).andReturn(bcachingList);
        expect(bcachingList.getCachesRead()).andReturn(5);

        replayAll();
        assertTrue(new CacheListCursor(null, null, null, bcachingListImporter, timeRecorder,
                lastReadPosition).readCaches());
        verifyAll();
    }

    @Test
    public void testIncrement() throws BCachingException {
        expect(bcachingListImporter.getBCachingList()).andReturn(bcachingList);
        expect(bcachingList.getCachesRead()).andReturn(5);
        expect(lastReadPosition.get()).andReturn(12);
        lastReadPosition.put(17);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, 17);

        replayAll();
        CacheListCursor cacheListCursor = new CacheListCursor(bcachingLastUpdated, progressManager,
                progressHandler, bcachingListImporter, timeRecorder, lastReadPosition);
        cacheListCursor.increment();
        verifyAll();
    }

    @Test
    public void testGetCacheIds() throws BCachingException {
        expect(bcachingListImporter.getBCachingList()).andReturn(bcachingList);
        expect(bcachingList.getCacheIds()).andReturn("1,2,3");

        replayAll();
        CacheListCursor cacheListCursor = new CacheListCursor(bcachingLastUpdated, progressManager,
                progressHandler, bcachingListImporter, timeRecorder, lastReadPosition);
        assertEquals("1,2,3", cacheListCursor.getCacheIds());
        verifyAll();
    }
}
