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

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.data.GeocacheFactory;
import com.google.code.geobeagle.data.Geocaches;
import com.google.code.geobeagle.io.CacheReader;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DbToGeocacheAdapter;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.CacheReader.CacheReaderCursor;
import com.google.code.geobeagle.io.CacheReader.WhereFactory;
import com.google.code.geobeagle.io.Database.ISQLiteDatabase;
import com.google.code.geobeagle.io.Database.OpenHelperDelegate;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseDI {

    public static class CacheReaderCursorFactory {
        public CacheReaderCursor create(Cursor cursor) {
            GeocacheFactory geocacheFactory = new GeocacheFactory();
            DbToGeocacheAdapter dbToGeocacheAdapter = new DbToGeocacheAdapter();
            return new CacheReaderCursor(cursor, geocacheFactory, dbToGeocacheAdapter);
        }
    }

    public static class GeoBeagleSqliteOpenHelper extends SQLiteOpenHelper {
        private final Database.OpenHelperDelegate mOpenHelperDelegate;

        public GeoBeagleSqliteOpenHelper(Context context,
                Database.OpenHelperDelegate openHelperDelegate) {
            super(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION);
            mOpenHelperDelegate = openHelperDelegate;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mOpenHelperDelegate.onCreate(new SQLiteWrapper(db));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mOpenHelperDelegate.onUpgrade(new SQLiteWrapper(db), oldVersion, newVersion);
        }
    }

    public static class SQLiteWrapper implements ISQLiteDatabase {
        private SQLiteDatabase mSQLiteDatabase;

        public SQLiteWrapper(SQLiteDatabase db) {
            mSQLiteDatabase = db;
        }

        public void beginTransaction() {
            mSQLiteDatabase.beginTransaction();
        }

        public void close() {
            mSQLiteDatabase.close();
        }

        public int countResults(String table, String selection, String... selectionArgs) {
            Cursor cursor = mSQLiteDatabase.query(table, null, selection, selectionArgs, null,
                    null, null, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        }

        public void endTransaction() {
            mSQLiteDatabase.endTransaction();
        }

        public void execSQL(String sql) {
            mSQLiteDatabase.execSQL(sql);
        }

        public void execSQL(String sql, Object... bindArgs) {
            mSQLiteDatabase.execSQL(sql, bindArgs);
        }

        public void openReadableDatabase(Database database) {
            mSQLiteDatabase = database.getReadableDatabase();
        }

        public void openWritableDatabase(Database database) {
            mSQLiteDatabase = database.getWritableDatabase();
        }

        public Cursor query(String table, String[] columns, String selection, String groupBy,
                String having, String orderBy, String limit, String... selectionArgs) {
            return mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy,
                    orderBy, having, limit);
        }

        public Cursor rawQuery(String sql, String[] selectionArgs) {
            return mSQLiteDatabase.rawQuery(sql, selectionArgs);
        }

        public void setTransactionSuccessful() {
            mSQLiteDatabase.setTransactionSuccessful();
        }
    }

    public static Database create(Context context) {
        final OpenHelperDelegate openHelperDelegate = new Database.OpenHelperDelegate();
        final GeoBeagleSqliteOpenHelper sqliteOpenHelper = new GeoBeagleSqliteOpenHelper(context,
                openHelperDelegate);
        return new Database(sqliteOpenHelper);
    }

    public static GeocachesSql create(LocationControl locationControl, Database database) {
        final Geocaches geocaches = new Geocaches();
        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(null);
        final CacheReader cacheReader = createCacheReader(sqliteWrapper);
        return new GeocachesSql(cacheReader, geocaches, database, sqliteWrapper, locationControl);
    }

    public static CacheReader createCacheReader(SQLiteWrapper sqliteWrapper) {
        final WhereFactory whereFactory = new CacheReader.WhereFactory();
        final CacheReaderCursorFactory cacheReaderCursorFactory = new CacheReaderCursorFactory();
        return new CacheReader(sqliteWrapper, whereFactory, cacheReaderCursorFactory);
    }

    public static CacheWriter createCacheWriter(SQLiteWrapper sqliteWrapper) {
        DbToGeocacheAdapter dbToGeocacheAdapter = new DbToGeocacheAdapter();
        return new CacheWriter(sqliteWrapper, dbToGeocacheAdapter);
    }

}
