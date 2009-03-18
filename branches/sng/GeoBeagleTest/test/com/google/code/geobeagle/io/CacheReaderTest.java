
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.CacheReader.WhereFactory;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;

import android.database.Cursor;
import android.location.Location;

import junit.framework.TestCase;

public class CacheReaderTest extends TestCase {

    private void expectQuery(SQLiteWrapper sqliteWrapper, Cursor cursor, String where) {
        expect(
                sqliteWrapper.query(eq("CACHES"), (String[])eq(Database.READER_COLUMNS), eq(where),
                        (String[])isNull(), (String)isNull(), (String)isNull(), (String)isNull(),
                        (String)eq(CacheReader.SQL_QUERY_LIMIT))).andReturn(cursor);
    }

    public void testCacheReaderGetCache() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        WhereFactory whereFactory = createMock(WhereFactory.class);

        Cursor cursor = createMock(Cursor.class);

        expectQuery(sqliteWrapper, cursor, null);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("122");
        expect(cursor.getString(1)).andReturn("37");
        expect(cursor.getString(2)).andReturn("the_name");
        expect(cursor.getString(3)).andReturn("description");

        replay(sqliteWrapper);
        replay(cursor);
        CacheReader cacheReader = new CacheReader(sqliteWrapper, whereFactory);
        cacheReader.open(null);
        assertEquals("122, 37 (the_name: description)", cacheReader.getCache());
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheReaderOpen() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);
        Location location = createMock(Location.class);
        WhereFactory whereFactory = createMock(WhereFactory.class);

        String where = "Latitude > something AND Longitude < somethingelse";
        expect(whereFactory.getWhere(location)).andReturn(where);
        expectQuery(sqliteWrapper, cursor, where);
        expect(cursor.moveToFirst()).andReturn(true);

        replay(sqliteWrapper);
        replay(cursor);
        replay(location);
        replay(whereFactory);
        new CacheReader(sqliteWrapper, whereFactory).open(location);
        verify(sqliteWrapper);
        verify(cursor);
        verify(location);
        verify(whereFactory);
    }

    public void testCacheReaderOpenEmpty() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);
        WhereFactory whereFactory = createMock(WhereFactory.class);

        expect(whereFactory.getWhere(null)).andReturn("a=b");
        expectQuery(sqliteWrapper, cursor, "a=b");
        expect(cursor.moveToFirst()).andReturn(false);
        cursor.close();

        replay(whereFactory);
        replay(sqliteWrapper);
        replay(cursor);
        new CacheReader(sqliteWrapper, whereFactory).open(null);
        verify(sqliteWrapper);
        verify(cursor);
        verify(whereFactory);
    }

    public void testCacheReaderOpenError() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);
        WhereFactory whereFactory = createMock(WhereFactory.class);

        expect(whereFactory.getWhere(null)).andReturn("a=b");
        expectQuery(sqliteWrapper, cursor, "a=b");
        expect(cursor.moveToFirst()).andReturn(true);

        replay(whereFactory);
        replay(sqliteWrapper);
        replay(cursor);
        new CacheReader(sqliteWrapper, whereFactory).open(null);
        verify(sqliteWrapper);
        verify(cursor);
        verify(whereFactory);
    }

    public void testGetWhere() {
        Location location = createMock(Location.class);
        expect(location.getLatitude()).andReturn(90.0);
        expect(location.getLongitude()).andReturn(180.0);

        replay(location);
        assertEquals(
                "Latitude > 89.92 AND Latitude < 90.08 AND Longitude > -180.0 AND Longitude < 180.0",
                new WhereFactory().getWhere(location));
        verify(location);
    }

    public void testGetWhereNullLocation() {
        assertEquals(null, new WhereFactory().getWhere(null));
    }
}
