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

package com.google.code.geobeagle.actions;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.actions.CacheActionDelete;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.DbFrontend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CacheActionDeleteTest {

    @Test
    public void testActionDelete() {
        CacheWriter cacheWriter = PowerMock.createMock(CacheWriter.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        TitleUpdater titleUpdater = PowerMock.createMock(TitleUpdater.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        expect(dbFrontend.getCacheWriter()).andReturn(cacheWriter);
        expect(geocache.getId()).andReturn("GC123");
        cacheWriter.deleteCache("GC123");
        geocacheListAdapter.notifyDataSetChanged();
        titleUpdater.refresh();

        PowerMock.replayAll();
        new CacheActionDelete(geocacheListAdapter, titleUpdater, dbFrontend)
                .act(geocache);
        PowerMock.verifyAll();
    }
}
