
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.data.Geocache.Source;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.DatabaseTest.DesktopSQLiteDatabase;

import java.io.IOException;

import junit.framework.TestCase;

public class CacheWriterTest extends TestCase {

    private static final String INSERT_INTO_CACHES = "INSERT INTO CACHES (Id, Description, Source, DeleteMe) ";
    private static final String INSERT_INTO_GPX = "INSERT INTO GPX (Name, ExportTime, DeleteMe) ";

    public void testClearCaches() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);

        sqlite.execSQL(Database.SQL_CLEAR_CACHES, "the source");

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite, null);
        cacheWriter.clearCaches("the source");
        verify(sqlite);
    }

    public void testClearEarlierLoads() throws IOException {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(DatabaseTest.currentSchema());

        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCTHISIMPORT', 'just loaded', 'foo.gpx', 0)");
        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCCLICKEDLINK', 'from a link', '"
                + Database.S0_INTENT + "', 0)");
        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCOLDIMPORT', 'from a gpx', 'bar.gpx', 1)");
        db.execSQL(INSERT_INTO_GPX + "VALUES ('nuke.gpx', '2009-04-30', 1)");
        db.execSQL(INSERT_INTO_GPX + "VALUES ('keep.gpx', '2009-04-30', 0)");

        CacheWriter cacheWriter = new CacheWriter(db, null);
        cacheWriter.clearEarlierLoads();

        assertEquals("GCTHISIMPORT|just loaded|||foo.gpx|1\n"
                + "GCCLICKEDLINK|from a link|||intent|0\n", db.dumpTable("CACHES"));
        assertEquals("keep.gpx|2009-04-30|1\n", db.dumpTable("GPX"));
    }

    public void testDeleteCache() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);

        sqlite.execSQL(Database.SQL_DELETE_CACHE, "GC123");

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite, null);
        cacheWriter.deleteCache("GC123");
        verify(sqlite);
    }

    public void testInsertAndUpdate() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        DbToGeocacheAdapter dbToGeocacheAdapter = createMock(DbToGeocacheAdapter.class);

        sqlite.execSQL(Database.SQL_REPLACE_CACHE, "gc123", "a cache", 122.0, 37.0, "source");
        expect(dbToGeocacheAdapter.sourceTypeToSourceName(Source.GPX, "source")).andReturn("source");
        
        replay(sqlite);
        replay(dbToGeocacheAdapter);
        CacheWriter cacheWriter = new CacheWriter(sqlite, dbToGeocacheAdapter);
        cacheWriter.insertAndUpdateCache("gc123", "a cache", 122, 37, Source.GPX, "source");
        verify(sqlite);
    }

    public void testIsGpxAlreadyLoadedFalse() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);

        expect(
                sqlite.countResults(Database.TBL_GPX, "Name = ? AND ExportTime >= ?", "foo.gpx",
                        "04-30-2009")).andReturn(0);

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite, null);
        assertFalse(cacheWriter.isGpxAlreadyLoaded("foo.gpx", "04-30-2009"));
        verify(sqlite);
    }

    public void testIsGpxAlreadyLoadedTrue() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);

        expect(
                sqlite.countResults(Database.TBL_GPX, Database.SQL_MATCH_NAME_AND_EXPORTED_LATER,
                        "foo.gpx", "04-30-2009")).andReturn(1);
        sqlite.execSQL(Database.SQL_CACHES_DONT_DELETE_ME, "foo.gpx");
        sqlite.execSQL(Database.SQL_GPX_DONT_DELETE_ME, "foo.gpx");

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite, null);
        assertTrue(cacheWriter.isGpxAlreadyLoaded("foo.gpx", "04-30-2009"));
        verify(sqlite);
    }

    public void testStartWriting() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        sqlite.beginTransaction();

        replay(sqlite);
        new CacheWriter(sqlite, null).startWriting();
        verify(sqlite);
    }

    public void testStopWriting() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        sqlite.setTransactionSuccessful();
        sqlite.endTransaction();

        replay(sqlite);
        new CacheWriter(sqlite, null).stopWriting();
        verify(sqlite);
    }

    public void testWriteGpx() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        sqlite.execSQL(Database.SQL_REPLACE_GPX, "foo.gpx", "2009-04-30 10:30");

        replay(sqlite);
        new CacheWriter(sqlite, null).writeGpx("foo.gpx", "2009-04-30 10:30");
        verify(sqlite);
    }

}
