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

package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.ui.cachelist.CacheListRefresh.TitleUpdater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ContextActionDeleteTest {

    @Test
    public void testActionDelete() {
        CacheWriter cacheWriter = PowerMock.createMock(CacheWriter.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        TitleUpdater titleUpdater = PowerMock.createMock(TitleUpdater.class);

        expect(geocacheVectors.get(17)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        cacheWriter.deleteCache("GC123");
        geocacheVectors.remove(17);
        geocacheListAdapter.notifyDataSetChanged();
        titleUpdater.update();

        PowerMock.replayAll();
        new ContextActionDelete(geocacheListAdapter, cacheWriter, geocacheVectors,
                titleUpdater).act(17);
        PowerMock.verifyAll();
    }
}
