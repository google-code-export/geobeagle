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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.CacheReader;
import com.google.code.geobeagle.database.CacheReaderCursor;
import com.google.code.geobeagle.database.Database;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.DbToGeocacheAdapter;
import com.google.code.geobeagle.database.WhereFactory;
import com.google.code.geobeagle.database.WhereFactoryAllCaches;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches;
import com.google.code.geobeagle.database.DatabaseDI.CacheReaderCursorFactory;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.database.Cursor;
import android.location.Location;

@RunWith(PowerMockRunner.class)
public class CacheReaderTest {

    private void expectQuery(SQLiteWrapper sqliteWrapper, Cursor cursor, String where) {
        expect(
                sqliteWrapper.query(eq("CACHES"), eq(Database.READER_COLUMNS), eq(where),
                        (String)isNull(), (String)isNull(), (String)isNull(),
                        eq(CacheReader.SQL_QUERY_LIMIT))).andReturn(cursor);
    }

    @Test
    public void testCursorClose() {
        Cursor cursor = PowerMock.createMock(Cursor.class);

        cursor.close();

        PowerMock.replayAll();
        new CacheReaderCursor(cursor, null, null).close();
        PowerMock.verifyAll();
    }

    @Test
    public void testCursorGetCache() {
        Cursor cursor = PowerMock.createMock(Cursor.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        DbToGeocacheAdapter dbToGeocacheAdapter = PowerMock.createMock(DbToGeocacheAdapter.class);

        expect(cursor.getDouble(0)).andReturn(122.0);
        expect(cursor.getDouble(1)).andReturn(37.0);
        expect(cursor.getString(2)).andReturn("GC123");
        expect(cursor.getString(3)).andReturn("name");
        expect(cursor.getString(4)).andReturn("cupertino");
        expect(dbToGeocacheAdapter.sourceNameToSourceType("cupertino")).andReturn(Source.GPX);
        expect(geocacheFactory.create("GC123", "name", 122.0, 37.0, Source.GPX, "cupertino"))
                .andReturn(geocache);

        PowerMock.replayAll();
        assertEquals(geocache, new CacheReaderCursor(cursor, geocacheFactory, dbToGeocacheAdapter)
                .getCache());
        PowerMock.verifyAll();
    }

    @Test
    public void testCursorMoveToNext() {
        Cursor cursor = PowerMock.createMock(Cursor.class);

        expect(cursor.moveToNext()).andReturn(true);

        PowerMock.replayAll();
        new CacheReaderCursor(cursor, null, null).moveToNext();
        PowerMock.verifyAll();
    }

    @Test
    public void testGetTotalCount() {
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Cursor cursor = PowerMock.createMock(Cursor.class);

        expect(sqliteWrapper.rawQuery("SELECT COUNT(*) FROM " + Database.TBL_CACHES, null))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getInt(0)).andReturn(812);
        cursor.close();

        PowerMock.replayAll();
        assertEquals(812, new CacheReader(sqliteWrapper, null).getTotalCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetWhereFactoryAllCaches() {
        assertEquals(null, new WhereFactoryAllCaches().getWhere(null));
    }

    @Test
    public void testGetWhere() {
        Location location = PowerMock.createMock(Location.class);

        expect(location.getLatitude()).andReturn(90.0);
        expect(location.getLongitude()).andReturn(180.0);

        PowerMock.replayAll();
        assertEquals(
                "Latitude > 89.92 AND Latitude < 90.08 AND Longitude > -180.0 AND Longitude < 180.0",
                new WhereFactoryNearestCaches().getWhere(location));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetWhereNullLocation() {
        assertEquals(null, new WhereFactoryNearestCaches().getWhere(null));
    }

    @Test
    public void testOpen() {
        Location location = PowerMock.createMock(Location.class);
        WhereFactory whereFactoryNearestCaches = PowerMock
                .createMock(WhereFactoryNearestCaches.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Cursor cursor = PowerMock.createMock(Cursor.class);
        DatabaseDI.CacheReaderCursorFactory cacheReaderCursorFactory = PowerMock
                .createMock(CacheReaderCursorFactory.class);
        CacheReaderCursor cacheReaderCursor = PowerMock.createMock(CacheReaderCursor.class);

        String where = "Latitude > something AND Longitude < somethingelse";
        expect(whereFactoryNearestCaches.getWhere(location)).andReturn(where);
        expectQuery(sqliteWrapper, cursor, where);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cacheReaderCursorFactory.create(cursor)).andReturn(cacheReaderCursor);

        PowerMock.replayAll();
        assertEquals(cacheReaderCursor, new CacheReader(sqliteWrapper, cacheReaderCursorFactory)
                .open(location, whereFactoryNearestCaches));
        PowerMock.verifyAll();
    }

    @Test
    public void testOpenEmpty() {
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Cursor cursor = PowerMock.createMock(Cursor.class);
        WhereFactory whereFactoryNearestCaches = PowerMock
                .createMock(WhereFactoryNearestCaches.class);

        expect(whereFactoryNearestCaches.getWhere(null)).andReturn("a=b");
        expectQuery(sqliteWrapper, cursor, "a=b");
        expect(cursor.moveToFirst()).andReturn(false);
        cursor.close();

        PowerMock.replayAll();
        new CacheReader(sqliteWrapper, null).open(null, whereFactoryNearestCaches);
        PowerMock.verifyAll();
    }
}