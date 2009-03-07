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

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.notNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.Database.CacheReader;
import com.google.code.geobeagle.io.Database.CacheWriter;
import com.google.code.geobeagle.io.Database.OpenHelperDelegate;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import junit.framework.TestCase;

public class DatabaseTest extends TestCase {

    public void testCacheReaderGetCache() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(
                sqliteWrapper.query(eq("CACHES"), (String[])eq(Database.READER_COLUMNS),
                        (String)isNull(), (String[])isNull(), (String)isNull(), (String)isNull(),
                        (String)isNull())).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("122");
        expect(cursor.getString(1)).andReturn("37");
        expect(cursor.getString(2)).andReturn("the_name");
        expect(cursor.getString(3)).andReturn("description");

        replay(sqliteWrapper);
        replay(cursor);
        final CacheReader cacheReader = new CacheReader(sqliteWrapper);
        cacheReader.open();
        assertEquals("122, 37 (the_name: description)", cacheReader.getCache());
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheReaderOpen() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(
                sqliteWrapper.query(eq("CACHES"), (String[])eq(Database.READER_COLUMNS),
                        (String)isNull(), (String[])isNull(), (String)isNull(), (String)isNull(),
                        (String)isNull())).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);

        replay(sqliteWrapper);
        replay(cursor);
        new CacheReader(sqliteWrapper).open();
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheReaderOpenEmpty() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(
                sqliteWrapper.query(eq("CACHES"), (String[])eq(Database.READER_COLUMNS),
                        (String)isNull(), (String[])isNull(), (String)isNull(), (String)isNull(),
                        (String)isNull())).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(false);
        cursor.close();
        replay(sqliteWrapper);
        replay(cursor);
        new CacheReader(sqliteWrapper).open();
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheReaderOpenError() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(
                sqliteWrapper.query(eq("CACHES"), (String[])eq(Database.READER_COLUMNS),
                        (String)isNull(), (String[])isNull(), (String)isNull(), (String)isNull(),
                        (String)isNull())).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        replay(sqliteWrapper);
        replay(cursor);
        new CacheReader(sqliteWrapper).open();
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheWriter() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);

        sqlite.execSQL(eq(Database.SQL_INSERT_CACHE), (Object[])notNull());

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite);
        cacheWriter.insertAndUpdateCache("gc123", "a cache", 122, 37, "source");
        verify(sqlite);
    }

    public void testCacheWriterClear() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        Object params[] = new Object[] {
            "the source"
        };
        sqlite.execSQL(eq(Database.SQL_CLEAR_CACHES), (Object[])aryEq(params));

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite);
        cacheWriter.clearCaches("the source");
        verify(sqlite);
    }

    public void testCacheWriterDelete() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        Object params[] = new Object[] {
            "GC123"
        };
        sqlite.execSQL(eq(Database.SQL_DELETE_CACHE), (Object[])aryEq(params));

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite);
        cacheWriter.deleteCache("GC123");
        verify(sqlite);
    }

    public void testCacheWriterError() {
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        SQLiteException exception = createMock(SQLiteConstraintException.class);

        sqlite.execSQL(eq(Database.SQL_INSERT_CACHE), (Object[])notNull());
        expectLastCall().andThrow(exception);
        expect(exception.fillInStackTrace()).andReturn(exception);
        sqlite.execSQL(eq(Database.SQL_DELETE_CACHE), aryEq(new Object[] {
            "gc123"
        }));
        sqlite.execSQL(eq(Database.SQL_INSERT_CACHE), (Object[])notNull());

        replay(sqlite);
        replay(errorDisplayer);
        replay(exception);
        CacheWriter cacheWriter = new CacheWriter(sqlite);
        cacheWriter.insertAndUpdateCache("gc123", "a cache", 122, 37, "source");
        verify(sqlite);
        verify(errorDisplayer);
        verify(exception);
    }

    public void testDatabaseGetWritableDatabase() {
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        SQLiteOpenHelper sqliteOpenHelper = createMock(SQLiteOpenHelper.class);

        expect(sqliteOpenHelper.getWritableDatabase()).andReturn(sqlite);

        replay(sqlite);
        replay(sqliteOpenHelper);
        Database database = new Database(sqliteOpenHelper);
        assertEquals(sqlite, database.getWritableDatabase());
        verify(sqliteOpenHelper);
        verify(sqlite);
    }

    public void testSQLiteOpenHelperDelegate_onCreate() {
        SQLiteDatabase sqliteDatabase = createMock(SQLiteDatabase.class);

        sqliteDatabase.execSQL(Database.SQL_CREATE_CACHE_TABLE);

        replay(sqliteDatabase);
        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onCreate(sqliteDatabase);
        verify(sqliteDatabase);
    }

    public void testSQLiteOpenHelperDelegate_onUpgrade() {
        SQLiteDatabase sqliteDatabase = createMock(SQLiteDatabase.class);

        sqliteDatabase.execSQL(Database.SQL_DROP_CACHE_TABLE);
        sqliteDatabase.execSQL(Database.SQL_CREATE_CACHE_TABLE);

        replay(sqliteDatabase);
        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(sqliteDatabase, 0, 0);
        verify(sqliteDatabase);
    }
}
