
package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.database.DatabaseTest.DesktopSQLiteDatabase;

import org.junit.Before;
import org.junit.Test;

public class CacheWriterTest {

    private static final String INSERT_INTO_CACHES = "INSERT INTO CACHES (Id, Description, Source, DeleteMe) ";
    private static final String INSERT_INTO_GPX = "INSERT INTO GPX (Name, ExportTime, DeleteMe) ";

    private SQLiteWrapper mSqlite;
    SourceNameTranslator mSourceNameTranslator;
    GeocacheFactory mFactory;
    DbFrontend mDbFrontend;
    
    @Before
    public void setUp() {
        mSqlite = createMock(SQLiteWrapper.class);
        mSourceNameTranslator = createMock(SourceNameTranslator.class);
        mFactory = createMock(GeocacheFactory.class);
        mDbFrontend = createMock(DbFrontend.class);        
    }
    
    @Test
    public void testClearCaches() {
        mSqlite.execSQL(Database.SQL_CLEAR_CACHES, "the source");
        mFactory.flushCache();

        replay(mSqlite);
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, null, mSourceNameTranslator, null);
        cacheWriterSql.clearCaches("the source");
        verify(mSqlite);
    }

    @Test
    public void testClearEarlierLoads() {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(DatabaseTest.currentSchema());  //andpe: Error "table CACHES already exists"

        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCTHISIMPORT', 'just loaded', 'foo.gpx', 0)");
        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCCLICKEDLINK', 'from a link', '"
                + Database.S0_INTENT + "', 0)");
        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCOLDIMPORT', 'from a gpx', 'bar.gpx', 1)");
        db.execSQL(INSERT_INTO_GPX + "VALUES ('nuke.gpx', '2009-04-30', 1)");
        db.execSQL(INSERT_INTO_GPX + "VALUES ('keep.gpx', '2009-04-30', 0)");

        CacheWriter cacheWriterSql = new CacheWriter(db, null, null, null);
        cacheWriterSql.clearEarlierLoads();

        assertEquals("GCTHISIMPORT|just loaded|||foo.gpx|1|0|0|0|0\n"
                + "GCCLICKEDLINK|from a link|||intent|0|0|0|0|0\n", db.dumpTable("CACHES"));
        assertEquals("keep.gpx|2009-04-30|1\n", db.dumpTable("GPX"));
    }

    @Test
    public void testDeleteCache() {
        mSqlite.execSQL(Database.SQL_DELETE_CACHE, "GC123");

        replay(mSqlite);
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, null, null, null);
        cacheWriterSql.deleteCache("GC123");
        verify(mSqlite);
    }

    @Test
    public void testInsertAndUpdate() {

        mSqlite.execSQL(Database.SQL_REPLACE_CACHE, "gc123", "a cache", 122.0, 37.0, "source", 0, 0,
                0, 0);
        expect(mSourceNameTranslator.sourceTypeToSourceName(Source.GPX, "source"))
                .andReturn("source");
        mDbFrontend.flushTotalCount();

        replay(mSqlite);
        replay(mSourceNameTranslator);
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, null, mSourceNameTranslator, null);
        cacheWriterSql.insertAndUpdateCache("gc123", "a cache", 122, 37, Source.GPX, "source",
                CacheType.NULL, 0, 0, 0);
        verify(mSqlite);
    }

    @Test
    public void testIsGpxAlreadyLoadedFalse() {
        expect(
                mSqlite.countResults(Database.TBL_GPX, "Name = ? AND ExportTime >= ?", "foo.gpx",
                        "04-30-2009")).andReturn(0);

        replay(mSqlite);
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend, mSourceNameTranslator, mFactory);
        assertFalse(cacheWriterSql.isGpxAlreadyLoaded("foo.gpx", "04-30-2009"));
        verify(mSqlite);
    }

    @Test
    public void testIsGpxAlreadyLoadedTrue() {
        expect(
                mSqlite.countResults(Database.TBL_GPX, Database.SQL_MATCH_NAME_AND_EXPORTED_LATER,
                        "foo.gpx", "04-30-2009")).andReturn(1);
        mSqlite.execSQL(Database.SQL_CACHES_DONT_DELETE_ME, "foo.gpx");
        mSqlite.execSQL(Database.SQL_GPX_DONT_DELETE_ME, "foo.gpx");

        replay(mSqlite);
        CacheWriter cacheWriterSql = new CacheWriter(mSqlite, mDbFrontend, mSourceNameTranslator, mFactory);
        assertTrue(cacheWriterSql.isGpxAlreadyLoaded("foo.gpx", "04-30-2009"));
        verify(mSqlite);
    }

    @Test
    public void testStartWriting() {
        mSqlite.beginTransaction();

        replay(mSqlite);
        new CacheWriter(mSqlite, mDbFrontend, mSourceNameTranslator, mFactory).startWriting();
        verify(mSqlite);
    }

    @Test
    public void testStopWriting() {
        mSqlite.setTransactionSuccessful();
        mSqlite.endTransaction();

        replay(mSqlite);
        new CacheWriter(mSqlite, mDbFrontend, mSourceNameTranslator, mFactory).stopWriting();
        verify(mSqlite);
    }

    @Test
    public void testWriteGpx() {
        mSqlite.execSQL(Database.SQL_REPLACE_GPX, "foo.gpx", "2009-04-30 10:30");

        replay(mSqlite);
        new CacheWriter(mSqlite, mDbFrontend, mSourceNameTranslator, mFactory).writeGpx("foo.gpx", "2009-04-30 10:30");
        verify(mSqlite);
    }
}
