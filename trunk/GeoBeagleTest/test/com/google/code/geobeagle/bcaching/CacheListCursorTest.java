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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImporter;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.bcaching.preferences.LastReadPosition;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.code.geobeagle.xmlimport.SyncCollectingParameter;

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
    private BCachingStartTime bcachingStartTime;
    private LastReadPosition lastReadPosition;
    private SyncCollectingParameter syncCollectingParameter;

    @Before
    public void setUp() {
        mockStatic(System.class);
        bcachingStartTime = createMock(BCachingStartTime.class);
        bcachingListImporter = createMock(BCachingListImporter.class);
        progressManager = createMock(ProgressManager.class);
        progressHandler = createMock(ProgressHandler.class);
        lastReadPosition = createMock(LastReadPosition.class);
        syncCollectingParameter = createMock(SyncCollectingParameter.class);
    }

    @Test
    public void testOpen() throws BCachingException {
        expect(bcachingStartTime.getLastUpdateTime()).andReturn(900L);
        bcachingListImporter.setStartTime("900");
        expect(bcachingListImporter.getTotalCount()).andReturn(128);
        syncCollectingParameter.Log(R.string.sync_message_bcaching_start);
        syncCollectingParameter.NestedLog(R.string.sync_message_bcaching_last_sync, "12-31 16:00");
        progressManager.update(progressHandler, ProgressMessage.SET_MAX, 128);
        lastReadPosition.load();
        expect(lastReadPosition.get()).andReturn(25);
        progressManager.setCurrentProgress(25);
        progressManager.update(progressHandler, ProgressMessage.SET_PROGRESS, 25);

        replayAll();
        assertTrue(new CacheListCursor(bcachingStartTime, progressManager, progressHandler,
                bcachingListImporter, lastReadPosition).open(syncCollectingParameter));
        verifyAll();
    }

    @Test
    public void testOpenNoCaches() throws BCachingException {
        expect(bcachingStartTime.getLastUpdateTime()).andReturn(900L);
        syncCollectingParameter.Log(R.string.sync_message_bcaching_start);
        syncCollectingParameter.NestedLog(R.string.sync_message_bcaching_last_sync, "12-31 16:00");
        bcachingListImporter.setStartTime("900");
        expect(bcachingListImporter.getTotalCount()).andReturn(0);
        syncCollectingParameter.NestedLog(R.string.sync_message_bcaching_synced_caches, 0);

        replayAll();
        assertFalse(new CacheListCursor(bcachingStartTime, progressManager, progressHandler,
                bcachingListImporter, lastReadPosition).open(syncCollectingParameter));
        verifyAll();
    }

    @Test
    public void testClose() {
        bcachingStartTime.resetStartTime();
        lastReadPosition.put(0);

        replayAll();
        new CacheListCursor(bcachingStartTime, null, null, null, lastReadPosition).close();
        verifyAll();
    }

    @Test
    public void testReadCaches() throws BCachingException {
        expect(lastReadPosition.get()).andReturn(7);
        bcachingListImporter.readCacheList(7);
        expect(bcachingListImporter.getCachesRead()).andReturn(5);

        replayAll();
        assertTrue(new CacheListCursor(null, null, null, bcachingListImporter, lastReadPosition)
                .readCaches() > 0);
        verifyAll();
    }

    @Test
    public void testIncrement() throws BCachingException {
        expect(bcachingListImporter.getCachesRead()).andReturn(5);
        expect(lastReadPosition.get()).andReturn(12);
        lastReadPosition.put(17);

        replayAll();
        CacheListCursor cacheListCursor = new CacheListCursor(null, progressManager,
                progressHandler, bcachingListImporter, lastReadPosition);
        cacheListCursor.increment();
        verifyAll();
    }

    @Test
    public void testGetCacheIds() throws BCachingException {
        expect(bcachingListImporter.getCacheIds()).andReturn("1,2,3");

        replayAll();
        CacheListCursor cacheListCursor = new CacheListCursor(null, null, null,
                bcachingListImporter, null);
        assertEquals("1,2,3", cacheListCursor.getCacheIds());
        verifyAll();
    }
}
