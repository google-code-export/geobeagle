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

import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.BoundingBox;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.Search;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.SearchDown;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.SearchUp;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.WhereStringFactory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;

public class DatabaseDI {

    public static class CacheReaderCursorFactory {
        public CacheReaderCursor create(Cursor cursor) {
            final GeocacheFactory geocacheFactory = new GeocacheFactory();
            final DbToGeocacheAdapter dbToGeocacheAdapter = new DbToGeocacheAdapter();
            return new CacheReaderCursor(cursor, geocacheFactory, dbToGeocacheAdapter);
        }
    }

    public static class GeoBeagleSqliteOpenHelper extends SQLiteOpenHelper {
        private final OpenHelperDelegate mOpenHelperDelegate;

        public GeoBeagleSqliteOpenHelper(Context context) {
            super(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION);
            mOpenHelperDelegate = new OpenHelperDelegate();
        }

        public SQLiteWrapper getWritableSqliteWrapper() {
            return new SQLiteWrapper(this.getWritableDatabase());
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(db);
            mOpenHelperDelegate.onCreate(sqliteWrapper);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(db);
            mOpenHelperDelegate.onUpgrade(sqliteWrapper, oldVersion);
        }
    }

    public static class SQLiteWrapper implements ISQLiteDatabase {
        private final SQLiteDatabase mSQLiteDatabase;

        public SQLiteWrapper(SQLiteDatabase writableDatabase) {
            mSQLiteDatabase = writableDatabase;
        }

        public void beginTransaction() {
            mSQLiteDatabase.beginTransaction();
        }

        public int countResults(String table, String selection, String... selectionArgs) {
            Log.d("GeoBeagle", "SQL count results: " + selection + ", "
                    + Arrays.toString(selectionArgs));

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
            Log.d("GeoBeagle", "SQL: " + sql);
            mSQLiteDatabase.execSQL(sql);
        }

        public void execSQL(String sql, Object... bindArgs) {
            Log.d("GeoBeagle", "SQL: " + sql + ", " + Arrays.toString(bindArgs));
            mSQLiteDatabase.execSQL(sql, bindArgs);
        }

        public Cursor query(String table, String[] columns, String selection, String groupBy,
                String having, String orderBy, String limit, String... selectionArgs) {
            final Cursor query = mSQLiteDatabase.query(table, columns, selection, selectionArgs,
                    groupBy, orderBy, having, limit);
            Log.d("GeoBeagle", "limit: " + limit + ", count: " + query.getCount() + ", query: "
                    + selection);
            return query;
        }

        public Cursor rawQuery(String sql, String[] selectionArgs) {
            return mSQLiteDatabase.rawQuery(sql, selectionArgs);
        }

        public void setTransactionSuccessful() {
            mSQLiteDatabase.setTransactionSuccessful();
        }

        public void close() {
            Log.d("GeoBeagle", "----------closing sqlite------");
            mSQLiteDatabase.close();
        }

        public boolean isOpen() {
            return mSQLiteDatabase.isOpen();
        }

    }

    static public class SearchFactory {
        public Search createSearch(double latitude, double longitude, float min, float max,
                ISQLiteDatabase sqliteWrapper) {
            WhereStringFactory whereStringFactory = new WhereStringFactory();
            BoundingBox boundingBox = new BoundingBox(latitude, longitude, sqliteWrapper,
                    whereStringFactory);
            SearchDown searchDown = new SearchDown(boundingBox, min);
            SearchUp searchUp = new SearchUp(boundingBox, max);
            return new WhereFactoryNearestCaches.Search(boundingBox, searchDown, searchUp);
        }

    }

    public static CacheReader createCacheReader(ISQLiteDatabase sqliteWrapper) {
        final CacheReaderCursorFactory cacheReaderCursorFactory = new CacheReaderCursorFactory();
        return new CacheReader(sqliteWrapper, cacheReaderCursorFactory);
    }

    public static CacheWriter createCacheWriter(ISQLiteDatabase writableDatabase) {
        // final SQLiteWrapper sqliteWrapper = new
        // DatabaseDI.SQLiteWrapper(sqliteDatabaseWritable);
        final DbToGeocacheAdapter dbToGeocacheAdapter = new DbToGeocacheAdapter();
        return new CacheWriter(writableDatabase, dbToGeocacheAdapter);
    }

}
