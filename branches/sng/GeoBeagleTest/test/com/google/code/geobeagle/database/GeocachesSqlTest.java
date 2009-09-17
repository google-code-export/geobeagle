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
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.Geocache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class GeocachesSqlTest {

    @Test
    public void testGetCount() {
        CacheReader cacheReaderSql = PowerMock.createMock(CacheReader.class);

        expect(cacheReaderSql.getTotalCount()).andReturn(12);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(cacheReaderSql, null);
        assertEquals(12, geocachesSql.getCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetGeocaches() {
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);

        ArrayList<Geocache> arGeocaches = new ArrayList<Geocache>();
        expect(geocaches.getAll()).andReturn(arGeocaches);

        PowerMock.replayAll();
        assertEquals(arGeocaches, new GeocachesSql(null, geocaches).getGeocaches());
        PowerMock.verifyAll();
    }

    @Test
    public void testLoad() {
        CacheReader cacheReaderSql = PowerMock.createMock(CacheReader.class);
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        WhereFactory whereFactory = PowerMock.createMock(WhereFactory.class);

        expect(cacheReaderSql.open(122, 37, whereFactory, CacheReader.SQL_QUERY_LIMIT)).andReturn(
                cursor);
        geocaches.clear();
        expect(cursor.getCache()).andReturn(geocache);
        geocaches.add(geocache);
        expect(cursor.moveToNext()).andReturn(false);
        cursor.close();

        PowerMock.replayAll();
        new GeocachesSql(cacheReaderSql, geocaches).loadCaches(122, 37, whereFactory);
        PowerMock.verifyAll();
    }

}
