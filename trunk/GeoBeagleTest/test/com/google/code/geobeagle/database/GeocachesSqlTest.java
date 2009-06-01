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
import com.google.code.geobeagle.database.CacheReader;
import com.google.code.geobeagle.database.CacheReaderCursor;
import com.google.code.geobeagle.database.Geocaches;
import com.google.code.geobeagle.database.GeocachesSql;
import com.google.code.geobeagle.database.WhereFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class GeocachesSqlTest {

    @Test
    public void testGetCount() {
        CacheReader cacheReader = PowerMock.createMock(CacheReader.class);

        expect(cacheReader.getTotalCount()).andReturn(12);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(cacheReader, null);
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
        Location location = PowerMock.createMock(Location.class);
        CacheReader cacheReader = PowerMock.createMock(CacheReader.class);
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        WhereFactory whereFactory = PowerMock.createMock(WhereFactory.class);

        expect(cacheReader.open(location, whereFactory)).andReturn(cursor);
        geocaches.clear();
        expect(cursor.getCache()).andReturn(geocache);
        geocaches.add(geocache);
        expect(cursor.moveToNext()).andReturn(false);
        cursor.close();

        PowerMock.replayAll();
        new GeocachesSql(cacheReader, geocaches).loadCaches(location, whereFactory);
        PowerMock.verifyAll();
    }

    @Test
    public void testReadOne() {
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        expect(cursor.getCache()).andReturn(geocache);
        expect(cursor.moveToNext()).andReturn(false);
        geocaches.add(geocache);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches);
        geocachesSql.read(cursor);
        PowerMock.verifyAll();
    }

    @Test
    public void testReadTwo() {
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache1 = PowerMock.createMock(Geocache.class);
        Geocache geocache2 = PowerMock.createMock(Geocache.class);

        expect(cursor.getCache()).andReturn(geocache1);
        geocaches.add(geocache1);
        expect(cursor.moveToNext()).andReturn(true);
        expect(cursor.getCache()).andReturn(geocache2);
        geocaches.add(geocache2);
        expect(cursor.moveToNext()).andReturn(false);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches);
        geocachesSql.read(cursor);
        PowerMock.verifyAll();
    }

}
