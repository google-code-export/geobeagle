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

package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.Geocaches;
import com.google.code.geobeagle.io.CacheReader.CacheReaderCursor;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class GeocachesSqlTest {

    private Database mDatabase;

    @Before
    public void setUp() {
        mDatabase = PowerMock.createMock(Database.class);
    }

    @Test
    public void testGetCount() {
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        CacheReader cacheReader = PowerMock.createMock(CacheReader.class);

        sqliteWrapper.openWritableDatabase(mDatabase);
        expect(cacheReader.getTotalCount()).andReturn(12);
        sqliteWrapper.close();

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(cacheReader, null, mDatabase, sqliteWrapper,
                null);
        assertEquals(12, geocachesSql.getCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetGeocaches() {
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);

        ArrayList<Geocache> arGeocaches = new ArrayList<Geocache>();
        expect(geocaches.getAll()).andReturn(arGeocaches);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches, null, null, null);
        assertEquals(arGeocaches, geocachesSql.getGeocaches());
        PowerMock.verifyAll();
    }

    @Test
    public void testLoad() {
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        LocationControl locationControl = PowerMock.createMock(LocationControl.class);
        CacheReader cacheReader = PowerMock.createMock(CacheReader.class);
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        sqliteWrapper.openWritableDatabase(mDatabase);
        expect(locationControl.getLocation()).andReturn(null);
        expect(cacheReader.open(null)).andReturn(cursor);
        expect(cursor.getCache()).andReturn(geocache);
        geocaches.clear();
        geocaches.add(geocache);
        expect(cursor.moveToNext()).andReturn(false);
        cursor.close();
        sqliteWrapper.close();

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(cacheReader, geocaches, mDatabase,
                sqliteWrapper, locationControl);
        geocachesSql.loadNearestCaches();
        PowerMock.verifyAll();
    }

    @Test
    public void testReadOne() {
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        geocaches.clear();
        expect(cursor.getCache()).andReturn(geocache);
        expect(cursor.moveToNext()).andReturn(false);
        geocaches.add(geocache);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches, null, null, null);
        geocachesSql.read(cursor);
        PowerMock.verifyAll();
    }

    @Test
    public void testReadTwo() {
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache1 = PowerMock.createMock(Geocache.class);
        Geocache geocache2 = PowerMock.createMock(Geocache.class);

        geocaches.clear();
        expect(cursor.getCache()).andReturn(geocache1);
        geocaches.add(geocache1);
        expect(cursor.moveToNext()).andReturn(true);
        expect(cursor.getCache()).andReturn(geocache2);
        geocaches.add(geocache2);
        expect(cursor.moveToNext()).andReturn(false);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches, null, null, null);
        geocachesSql.read(cursor);
        PowerMock.verifyAll();
    }

}
