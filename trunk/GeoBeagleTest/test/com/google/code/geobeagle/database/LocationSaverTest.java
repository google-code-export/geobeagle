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

package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class LocationSaverTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testSave() {
        CacheSqlWriter writer = createMock(CacheSqlWriter.class);
        Geocache geocache = createMock(Geocache.class);
        Provider<CacheSqlWriter> cacheWriterProvider = createMock(Provider.class);
        TagReader tagReader = createMock(TagReader.class);
        expect(tagReader.hasTag("LB12345", Tag.FOUND)).andReturn(true);
        expect(cacheWriterProvider.get()).andReturn(writer);
        mockStatic(DatabaseDI.class);

        writer.startWriting();
        expect(geocache.getId()).andReturn("LB12345");
        expect(geocache.getName()).andReturn("");
        expect(geocache.getLatitude()).andReturn(122.0);
        expect(geocache.getLongitude()).andReturn(37.0);
        expect(geocache.getSourceType()).andReturn(Source.GPX);
        expect(geocache.getSourceName()).andReturn("manhattan");
        expect(geocache.getCacheType()).andReturn(CacheType.TRADITIONAL);
        expect(geocache.getDifficulty()).andReturn(3);
        expect(geocache.getTerrain()).andReturn(1);
        expect(geocache.getContainer()).andReturn(2);
        expect(geocache.getAvailable()).andReturn(true);
        expect(geocache.getArchived()).andReturn(false);
        writer.insertAndUpdateCache("LB12345", "", 122, 37, Source.GPX, "manhattan",
                CacheType.TRADITIONAL, 3, 1, 2, true, false, true);
        writer.stopWriting();

        replayAll();
        new LocationSaver(cacheWriterProvider, tagReader).saveLocation(geocache);
        verifyAll();
    }
}
