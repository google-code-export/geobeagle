
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheFactory;
import com.google.code.geobeagle.data.GeocacheFactory.Source;
import com.google.code.geobeagle.io.CacheReader.CacheReaderCursor;
import com.google.code.geobeagle.io.CacheReader.WhereFactory;
import com.google.code.geobeagle.io.DatabaseDI.CacheReaderCursorFactory;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;

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

        expect(sqliteWrapper.countResults(Database.TBL_CACHES, null)).andReturn(17);

        PowerMock.replayAll();
        assertEquals(17, new CacheReader(sqliteWrapper, null, null).getTotalCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetWhere() {
        Location location = PowerMock.createMock(Location.class);
        expect(location.getLatitude()).andReturn(90.0);
        expect(location.getLongitude()).andReturn(180.0);

        PowerMock.replayAll();
        assertEquals(
                "Latitude > 89.92 AND Latitude < 90.08 AND Longitude > -180.0 AND Longitude < 180.0",
                new WhereFactory().getWhere(location));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetWhereNullLocation() {
        assertEquals(null, new WhereFactory().getWhere(null));
    }

    @Test
    public void testOpen() {
        Location location = PowerMock.createMock(Location.class);
        WhereFactory whereFactory = PowerMock.createMock(WhereFactory.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Cursor cursor = PowerMock.createMock(Cursor.class);
        DatabaseDI.CacheReaderCursorFactory cacheReaderCursorFactory = PowerMock
                .createMock(CacheReaderCursorFactory.class);
        CacheReaderCursor cacheReaderCursor = PowerMock.createMock(CacheReaderCursor.class);

        String where = "Latitude > something AND Longitude < somethingelse";
        expect(whereFactory.getWhere(location)).andReturn(where);
        expectQuery(sqliteWrapper, cursor, where);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cacheReaderCursorFactory.create(cursor)).andReturn(cacheReaderCursor);

        PowerMock.replayAll();
        assertEquals(cacheReaderCursor, new CacheReader(sqliteWrapper, whereFactory,
                cacheReaderCursorFactory).open(location));
        PowerMock.verifyAll();
    }

    @Test
    public void testOpenEmpty() {
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        Cursor cursor = PowerMock.createMock(Cursor.class);
        WhereFactory whereFactory = PowerMock.createMock(WhereFactory.class);

        expect(whereFactory.getWhere(null)).andReturn("a=b");
        expectQuery(sqliteWrapper, cursor, "a=b");
        expect(cursor.moveToFirst()).andReturn(false);
        cursor.close();

        PowerMock.replayAll();
        new CacheReader(sqliteWrapper, whereFactory, null).open(null);
        PowerMock.verifyAll();
    }
}
