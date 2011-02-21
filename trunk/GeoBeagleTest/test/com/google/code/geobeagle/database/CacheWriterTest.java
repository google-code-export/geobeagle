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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.database.filter.Filter;
import com.google.code.geobeagle.xmlimport.SyncCollectingParameter;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.database.Cursor;
@RunWith(PowerMockRunner.class)

public class CacheWriterTest extends GeoBeagleTest {

    private static final String INSERT_INTO_CACHES = "INSERT INTO CACHES (Id, Description, Source, DeleteMe) ";
    private static final String INSERT_INTO_GPX = "INSERT INTO GPX (Name, ExportTime, DeleteMe) ";
    private ISQLiteDatabase sqlite;
    private SyncCollectingParameter syncCollectingParameter;
    private Provider<ISQLiteDatabase> sqliteProvider;
    private Cursor cursor;
    private DesktopSQLiteDatabase db;
    private DbToGeocacheAdapter dbToGeocacheAdapter;
    private Filter filter;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        cursor = createMock(Cursor.class);
        sqlite = createMock(SQLiteWrapper.class);
        syncCollectingParameter = createMock(SyncCollectingParameter.class);
        sqliteProvider = createMock(Provider.class);
        db = new DesktopSQLiteDatabase();
        dbToGeocacheAdapter = createMock(DbToGeocacheAdapter.class);
        filter = createMock(Filter.class);
    }

    @Test
    public void testClearEarlierLoads() {
        db.execSQL(DatabaseTest.currentSchema());
        expect(sqliteProvider.get()).andReturn(db);

        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCTHISIMPORT', 'just loaded', 'foo.gpx', 0)");
        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCCLICKEDLINK', 'from a link', '"
                + Database.S0_INTENT + "', 0)");
        db.execSQL(INSERT_INTO_CACHES + "VALUES ('GCOLDIMPORT', 'from a gpx', 'bar.gpx', 1)");
        db.execSQL(INSERT_INTO_GPX + "VALUES ('nuke.gpx', '2009-04-30', 1)");
        db.execSQL(INSERT_INTO_GPX + "VALUES ('keep.gpx', '2009-04-30', 0)");

        replayAll();
        new ClearCachesFromSourceImpl(sqliteProvider).clearEarlierLoads();
        verifyAll();

        assertEquals("GCTHISIMPORT|just loaded|||foo.gpx|1|0|0|0|0|1|0|1\n"
                + "GCCLICKEDLINK|from a link|||intent|0|0|0|0|0|1|0|1\n", db.dumpTable("CACHES"));
        assertEquals("keep.gpx|2009-04-30|1\n", db.dumpTable("GPX"));
    }

    @Test
    public void testDeleteCache() {
        expect(sqliteProvider.get()).andReturn(sqlite);

        sqlite.execSQL(Database.SQL_DELETE_CACHE, "GC123");

        replayAll();
        CacheSqlWriter cacheWriterSql = new CacheSqlWriter(sqliteProvider, null, null);
        cacheWriterSql.deleteCache("GC123");
        verifyAll();
    }

    @Test
    public void testInsertAndUpdate() {
        expect(filter.showBasedOnDnfState("gc123")).andReturn(true);
        expect(filter.showBasedOnFoundState(true)).andReturn(true);
        expect(filter.showBasedOnAvailableState(false)).andReturn(true);
        expect(filter.showBasedOnCacheType(CacheType.NULL)).andReturn(true);
        expect(sqliteProvider.get()).andReturn(sqlite);

        sqlite.execSQL(Database.SQL_REPLACE_CACHE, "gc123", "a cache", 122.0, 37.0, "source", 0, 0,
                0, 0, false, false, true);
        expect(dbToGeocacheAdapter.sourceTypeToSourceName(Source.GPX, "source"))
                .andReturn("source");

        replayAll();
        CacheSqlWriter cacheWriterSql = new CacheSqlWriter(sqliteProvider, dbToGeocacheAdapter, filter);
        cacheWriterSql.insertAndUpdateCache("gc123", "a cache", 122, 37, Source.GPX, "source",
                CacheType.NULL, 0, 0, 0, false, false, true);
        verifyAll();
    }

    @Test
    public void testIsGpxAlreadyLoadedInitialSync() {
        expect(sqliteProvider.get()).andReturn(sqlite);
        expect(sqlite.rawQuery((String)EasyMock.anyObject(), (String[])EasyMock.anyObject()))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(false);
        syncCollectingParameter.Log("  initial sync");
        cursor.close();

        replayAll();
        assertFalse(new GpxTableWriterGpxFiles(sqliteProvider, syncCollectingParameter)
                .isGpxAlreadyLoaded("foo.gpx", "04-30-2009"));
        verifyAll();
    }

    @Test
    public void testIsGpxAlreadyLoadedFalse() {
        expect(sqliteProvider.get()).andReturn(sqlite);
        expect(sqlite.rawQuery((String)EasyMock.anyObject(), (String[])EasyMock.anyObject()))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("05-01-2009 10:30:00");
        syncCollectingParameter.Log("07-02 10:30 --> 11-30 10:30");
        cursor.close();

        replayAll();
        GpxTableWriterGpxFiles gpxTableWriterGpxFiles = new GpxTableWriterGpxFiles(sqliteProvider,
                syncCollectingParameter);
        assertFalse(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("foo.gpx", "04-30-2009 10:30:00"));
        verifyAll();
    }

    @Test
    public void testIsGpxAlreadyLoadedTrue() {
        expect(sqliteProvider.get()).andReturn(sqlite);
        expect(sqlite.rawQuery((String)EasyMock.anyObject(), (String[])EasyMock.anyObject()))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("04-30-2009 10:30:00");
        syncCollectingParameter.Log("  no changes since 11-30 10:30");
        cursor.close();

        replayAll();
        GpxTableWriterGpxFiles gpxTableWriterGpxFiles = new GpxTableWriterGpxFiles(sqliteProvider,
                syncCollectingParameter);
        assertTrue(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("foo.gpx", "04-30-2009 10:30:00"));
        verifyAll();
    }

    @Test
    public void testStartWriting() {
        expect(sqliteProvider.get()).andReturn(sqlite);
        sqlite.beginTransaction();

        replayAll();
        new CacheSqlWriter(sqliteProvider, null, null).startWriting();
        verifyAll();
    }

    @Test
    public void testStopWriting() {
        expect(sqliteProvider.get()).andReturn(sqlite);
        sqlite.setTransactionSuccessful();
        sqlite.endTransaction();
        sqlite.execSQL(CacheSqlWriter.ANALYZE);

        replayAll();
        new CacheSqlWriter(sqliteProvider, null, null).stopWriting();
        verifyAll();
    }
}
