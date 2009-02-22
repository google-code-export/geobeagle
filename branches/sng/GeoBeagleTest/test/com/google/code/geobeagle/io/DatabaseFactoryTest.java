/*
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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.startsWith;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.DatabaseFactory.CacheReader;
import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;
import com.google.code.geobeagle.io.DatabaseFactory.OpenHelperDelegate;
import com.google.code.geobeagle.io.DatabaseFactory.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import junit.framework.TestCase;

public class DatabaseFactoryTest extends TestCase {

    public void testCacheReaderOpen() {
        SQLiteDatabase sqliteDatabase = createMock(SQLiteDatabase.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(
                sqliteWrapper.query(eq(sqliteDatabase), eq("CACHES"),
                        (String[])eq(DatabaseFactory.READER_COLUMNS), (String)isNull(),
                        (String[])isNull(), (String)isNull(), (String)isNull(), (String)isNull()))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);

        replay(sqliteDatabase);
        replay(sqliteWrapper);
        replay(cursor);
        new CacheReader(sqliteDatabase, sqliteWrapper).open();
        verify(sqliteDatabase);
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheReaderOpenEmpty() {
        SQLiteDatabase sqliteDatabase = createMock(SQLiteDatabase.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(
                sqliteWrapper.query(eq(sqliteDatabase), eq("CACHES"),
                        (String[])eq(DatabaseFactory.READER_COLUMNS), (String)isNull(),
                        (String[])isNull(), (String)isNull(), (String)isNull(), (String)isNull()))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(false);
        cursor.close();
        replay(sqliteDatabase);
        replay(sqliteWrapper);
        replay(cursor);
        new CacheReader(sqliteDatabase, sqliteWrapper).open();
        verify(sqliteDatabase);
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheReaderOpenError() {
        SQLiteDatabase sqliteDatabase = createMock(SQLiteDatabase.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(
                sqliteWrapper.query(eq(sqliteDatabase), eq("CACHES"),
                        (String[])eq(DatabaseFactory.READER_COLUMNS), (String)isNull(),
                        (String[])isNull(), (String)isNull(), (String)isNull(), (String)isNull()))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        replay(sqliteDatabase);
        replay(sqliteWrapper);
        replay(cursor);
        new CacheReader(sqliteDatabase, sqliteWrapper).open();
        verify(sqliteDatabase);
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheReaderGetCache() {
        SQLiteDatabase sqliteDatabase = createMock(SQLiteDatabase.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(
                sqliteWrapper.query(eq(sqliteDatabase), eq("CACHES"),
                        (String[])eq(DatabaseFactory.READER_COLUMNS), (String)isNull(),
                        (String[])isNull(), (String)isNull(), (String)isNull(), (String)isNull()))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("122");
        expect(cursor.getString(1)).andReturn("37");
        expect(cursor.getString(2)).andReturn("the_name");
        expect(cursor.getString(3)).andReturn("description");

        replay(sqliteDatabase);
        replay(sqliteWrapper);
        replay(cursor);
        final CacheReader cacheReader = new CacheReader(sqliteDatabase, sqliteWrapper);
        cacheReader.open();
        assertEquals("122, 37 (the_name: description)", cacheReader.getCache());
        verify(sqliteDatabase);
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheWriter() {
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);

        sqlite.execSQL(eq(DatabaseFactory.SQL_INSERT_CACHE_FULL), (Object[])notNull());

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite, null);
        cacheWriter.write("gc123", "a cache", 122, 37);
        verify(sqlite);
    }

    public void testCacheWriterError() {
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        SQLiteException exception = createMock(SQLiteException.class);

        sqlite.execSQL(eq(DatabaseFactory.SQL_INSERT_CACHE_FULL), (Object[])notNull());
        expectLastCall().andThrow(exception);
        expect(exception.fillInStackTrace()).andReturn(exception);
        expect(exception.getMessage()).andReturn("sql problem");
        errorDisplayer.displayError((String)notNull());

        replay(sqlite);
        replay(errorDisplayer);
        replay(exception);
        CacheWriter cacheWriter = new CacheWriter(sqlite, errorDisplayer);
        cacheWriter.write("gc123", "a cache", 122, 37);
        verify(sqlite);
        verify(errorDisplayer);
        verify(exception);
    }

    public void testDatabaseOpenOrCreate() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        SQLiteOpenHelper sqliteOpenHelper = createMock(SQLiteOpenHelper.class);

        expect(sqliteOpenHelper.getWritableDatabase()).andReturn(sqlite);

        replay(sqlite);
        replay(sqliteOpenHelper);
        DatabaseFactory database = new DatabaseFactory(sqliteWrapper, sqliteOpenHelper);
        assertEquals(sqlite, database.openOrCreateCacheDatabase(null));
        verify(sqliteOpenHelper);
        verify(sqlite);
    }

    public void testDatabaseOpenOrCreateOpenError() {
        SQLiteException e = createMock(SQLiteException.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        SQLiteOpenHelper sqliteOpenHelper = createMock(SQLiteOpenHelper.class);

        expect(sqliteOpenHelper.getWritableDatabase()).andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        expect(e.getMessage()).andReturn("can't open file");
        errorDisplayer.displayError(startsWith("Error opening or creating database"));

        replay(sqliteOpenHelper);
        replay(errorDisplayer);
        replay(e);
        DatabaseFactory database = new DatabaseFactory(null, sqliteOpenHelper);
        database.openOrCreateCacheDatabase(errorDisplayer);
        verify(sqliteOpenHelper);
        verify(errorDisplayer);
        verify(e);
    }

    public void testSQLiteOpenHelperDelegate_onCreate() {
        SQLiteDatabase sqliteDatabase = createMock(SQLiteDatabase.class);

        sqliteDatabase.execSQL(DatabaseFactory.SQL_CREATE_CACHE_TABLE);

        replay(sqliteDatabase);
        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onCreate(sqliteDatabase);
        verify(sqliteDatabase);
    }

    public void testSQLiteOpenHelperDelegate_onUpgrade() {
        SQLiteDatabase sqliteDatabase = createMock(SQLiteDatabase.class);

        sqliteDatabase.execSQL(DatabaseFactory.SQL_DROP_CACHE_TABLE);
        sqliteDatabase.execSQL(DatabaseFactory.SQL_CREATE_CACHE_TABLE);

        replay(sqliteDatabase);
        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(sqliteDatabase, 0, 0);
        verify(sqliteDatabase);
    }
}
