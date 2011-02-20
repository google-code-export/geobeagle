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
import com.google.code.geobeagle.activity.preferences.Preferences;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.code.geobeagle.xmlimport.SyncCollectingParameter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Message.class, Log.class, ImportBCachingWorker.class
})
public class ImportBCachingWorkerTest extends GeoBeagleTest {
    private CacheImporter cacheImporter;
    private ProgressHandler progressHandler;
    private ProgressManager progressManager;
    private CacheListCursor cursor;
    private UpdateFlag updateFlag;
    private SharedPreferences sharedPreferences;
    private SyncCollectingParameter syncCollectingParameter;

    @Before
    public void setUp() {
        mockStatic(Message.class);
        mockStatic(System.class);
        progressHandler = createMock(ProgressHandler.class);
        progressManager = createMock(ProgressManager.class);
        createMock(ErrorDisplayer.class);
        cacheImporter = createMock(CacheImporter.class);
        updateFlag = createMock(UpdateFlag.class);
        cursor = createMock(CacheListCursor.class);
        sharedPreferences = createMock(SharedPreferences.class);
        syncCollectingParameter = createMock(SyncCollectingParameter.class);
    }

    @Test
    public void testWorkerNoCaches() throws BCachingException, CancelException {
        expect(sharedPreferences.getBoolean(Preferences.BCACHING_ENABLED, false))
                .andReturn(true);
        updateFlag.setUpdatesEnabled(false);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(cursor.open(null)).andReturn(false);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);
        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        updateFlag.setUpdatesEnabled(true);

        replayAll();
        ImportBCachingWorker importBCachingWorker = new ImportBCachingWorker(progressHandler,
                progressManager, cacheImporter, cursor, updateFlag, sharedPreferences);
        importBCachingWorker.sync(null);
        verifyAll();
    }

    @Test
    public void testWorkerOneChunk() throws BCachingException, CancelException {
        expect(sharedPreferences.getBoolean(Preferences.BCACHING_ENABLED, false))
                .andReturn(true);
        updateFlag.setUpdatesEnabled(false);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(cursor.open(syncCollectingParameter)).andReturn(true);

        expect(cursor.readCaches()).andReturn(3);
        expect(cursor.getCacheIds()).andReturn("1,2,3");
        cacheImporter.load("1,2,3");
        cursor.increment();
        expect(cursor.readCaches()).andReturn(0);
        syncCollectingParameter.NestedLog(R.string.sync_message_bcaching_synced_caches, 3);
        cursor.close();
        updateFlag.setUpdatesEnabled(true);

        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);


        replayAll();
        new ImportBCachingWorker(progressHandler, progressManager, cacheImporter, cursor,
                updateFlag, sharedPreferences).sync(syncCollectingParameter);
        verifyAll();
    }

    @Test
    public void testWorkerTwoChunks() throws BCachingException, CancelException {
        expect(sharedPreferences.getBoolean(Preferences.BCACHING_ENABLED, false))
                .andReturn(true);
        updateFlag.setUpdatesEnabled(false);
        progressManager.update(progressHandler, ProgressMessage.START, 0);
        expect(cursor.open(syncCollectingParameter)).andReturn(true);

        expect(cursor.readCaches()).andReturn(3);
        expect(cursor.getCacheIds()).andReturn("1,2,3");
        cacheImporter.load("1,2,3");
        cursor.increment();

        expect(cursor.readCaches()).andReturn(3);
        expect(cursor.getCacheIds()).andReturn("4,5,6");
        cacheImporter.load("4,5,6");
        cursor.increment();

        expect(cursor.readCaches()).andReturn(0);
        cursor.close();
        syncCollectingParameter.NestedLog(R.string.sync_message_bcaching_synced_caches, 6);

        progressManager.update(progressHandler, ProgressMessage.REFRESH, 0);
        progressManager.update(progressHandler, ProgressMessage.DONE, 0);
        updateFlag.setUpdatesEnabled(true);

        replayAll();
        new ImportBCachingWorker(progressHandler, progressManager, cacheImporter, cursor,
                updateFlag, sharedPreferences).sync(syncCollectingParameter);
        verifyAll();
    }
}
