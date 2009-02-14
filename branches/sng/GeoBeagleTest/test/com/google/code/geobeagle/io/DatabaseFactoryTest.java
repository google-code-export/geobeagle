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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.startsWith;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.DatabaseFactory.CacheReader;
import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;
import com.google.code.geobeagle.io.DatabaseFactory.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import junit.framework.TestCase;

public class DatabaseFactoryTest extends TestCase {

    public void testCacheCursorOpen() {
        SQLiteDatabase db = createMock(SQLiteDatabase.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(sqliteWrapper.query(db, "CACHES", null, null, null, null, null, null)).andReturn(
                cursor);
        expect(cursor.moveToFirst()).andReturn(true);

        replay(db);
        replay(sqliteWrapper);
        replay(cursor);
        DatabaseFactory factory = new DatabaseFactory(sqliteWrapper);
        final DatabaseFactory.CacheReader cacheReader = factory.new CacheReader(db);
        cacheReader.open();
        verify(db);
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheCursorOpenEmpty() {
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(sqliteWrapper.query(sqlite, "CACHES", null, null, null, null, null, null))
                .andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(false);
        cursor.close();
        replay(sqlite);
        replay(sqliteWrapper);
        replay(cursor);
        DatabaseFactory factory = new DatabaseFactory(sqliteWrapper);
        final CacheReader cacheReader = factory.new CacheReader(sqlite);
        cacheReader.open();
        verify(sqlite);
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheCursorOpenError() {
        SQLiteDatabase db = createMock(SQLiteDatabase.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        Cursor cursor = createMock(Cursor.class);

        expect(sqliteWrapper.query(db, "CACHES", null, null, null, null, null, null)).andReturn(
                cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        replay(db);
        replay(sqliteWrapper);
        replay(cursor);
        DatabaseFactory factory = new DatabaseFactory(sqliteWrapper);
        final CacheReader cacheReader = factory.new CacheReader(db);
        cacheReader.open();
        verify(db);
        verify(sqliteWrapper);
        verify(cursor);
    }

    public void testCacheWriter() {
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);

        sqlite.execSQL("INSERT INTO CACHES (Description) VALUES (\"test\")");

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite);
        cacheWriter.write("test");
        verify(sqlite);
    }

    public void testDatabaseOpen() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        expect(
                sqliteWrapper.openDatabase(DatabaseFactory.DATABASE_FILE, null,
                        SQLiteDatabase.OPEN_READONLY)).andReturn(sqlite);

        replay(sqliteWrapper);
        DatabaseFactory database = new DatabaseFactory(sqliteWrapper);
        database.openCacheDatabase(null);
        verify(sqliteWrapper);
    }

    public void testDatabaseOpenError() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        SQLiteException e = createMock(SQLiteException.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        expect(
                sqliteWrapper.openDatabase(DatabaseFactory.DATABASE_FILE, null,
                        SQLiteDatabase.OPEN_READONLY)).andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        expect(e.getMessage()).andReturn("can't open file");
        errorDisplayer.displayError(startsWith("Error opening database"));

        replay(sqliteWrapper);
        replay(sqlite);
        replay(e);
        DatabaseFactory database = new DatabaseFactory(sqliteWrapper);
        database.openCacheDatabase(errorDisplayer);
        verify(sqliteWrapper);
        verify(sqlite);
        verify(e);
    }

    public void testDatabaseOpenOrCreate() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);

        expect(sqliteWrapper.openOrCreateDatabase(DatabaseFactory.DATABASE_FILE, null)).andReturn(
                sqlite);
        sqlite.execSQL(DatabaseFactory.CREATE_CACHE_TABLE);

        replay(sqliteWrapper);
        DatabaseFactory database = new DatabaseFactory(sqliteWrapper);
        database.openOrCreateCacheDatabase(null);
        verify(sqliteWrapper);
    }

    public void testDatabaseOpenOrCreateOpenError() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        SQLiteException e = createMock(SQLiteException.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        expect(sqliteWrapper.openOrCreateDatabase(DatabaseFactory.DATABASE_FILE, null)).andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        expect(e.getMessage()).andReturn("can't open file");
        errorDisplayer.displayError(startsWith("Error opening or creating database"));

        replay(sqliteWrapper);
        replay(errorDisplayer);
        replay(e);
        DatabaseFactory database = new DatabaseFactory(sqliteWrapper);
        database.openOrCreateCacheDatabase(errorDisplayer);
        verify(sqliteWrapper);
        verify(errorDisplayer);
        verify(e);
    }

    public void testDatabaseOpenOrCreateSqlErrorOnCreate() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        SQLiteException e = createMock(SQLiteException.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        expect(sqliteWrapper.openOrCreateDatabase(DatabaseFactory.DATABASE_FILE, null)).andReturn(
                sqlite);
        sqlite.execSQL(DatabaseFactory.CREATE_CACHE_TABLE);
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        expect(e.getMessage()).andReturn("can't open file");
        errorDisplayer.displayError(startsWith("Error opening or creating database"));

        replay(sqliteWrapper);
        replay(sqlite);
        replay(errorDisplayer);
        replay(e);
        DatabaseFactory database = new DatabaseFactory(sqliteWrapper);
        database.openOrCreateCacheDatabase(errorDisplayer);
        verify(sqliteWrapper);
        verify(sqlite);
        verify(errorDisplayer);
        verify(e);
    }
}
