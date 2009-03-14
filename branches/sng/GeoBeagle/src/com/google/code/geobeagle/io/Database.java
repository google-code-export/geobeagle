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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class Database {
    public static class CacheReader {
        public static class WhereFactory {
            // 1 degree ~= 111km
            public static final double DEGREES_DELTA = 0.08;

            public String getWhere(Location location) {
                if (location == null)
                    return null;
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                double latLow = latitude - WhereFactory.DEGREES_DELTA;
                double latHigh = latitude + WhereFactory.DEGREES_DELTA;
                double lat_radians = Math.toRadians(latitude);
                double cos_lat = Math.cos(lat_radians);
                double lonLow = Math.max(-180, longitude - WhereFactory.DEGREES_DELTA / cos_lat);
                double lonHigh = Math.min(180, longitude + WhereFactory.DEGREES_DELTA / cos_lat);
                return "Latitude > " + latLow + " AND Latitude < " + latHigh + " AND Longitude > "
                        + lonLow + " AND Longitude < " + lonHigh;
            }
        }

        public static final String SQL_QUERY_LIMIT = "1000";

        public static CacheReader create(SQLiteWrapper sqliteWrapper) {
            final WhereFactory whereFactory = new WhereFactory();
            return new CacheReader(sqliteWrapper, whereFactory);
        }

        private Cursor mCursor;
        private final SQLiteWrapper mSqliteWrapper;

        private final WhereFactory mWhereFactory;

        // TODO: rename to CacheSqlReader / CacheSqlWriter
        public CacheReader(SQLiteWrapper sqliteWrapper, WhereFactory whereFactory) {
            mSqliteWrapper = sqliteWrapper;
            mWhereFactory = whereFactory;
        }

        public void close() {
            mCursor.close();
        }

        public String getCache() {
            String name = mCursor.getString(3);
            String id = mCursor.getString(2);
            if (name.length() > 0 && id.length() > 0) {
                name = ": " + name;
            }

            return mCursor.getString(0) + ", " + mCursor.getString(1) + " (" + id + name + ")";
        }

        public int getTotalCount() {
            Cursor cursor = mSqliteWrapper.rawQuery(SQL_COUNT_CACHES, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }

        public boolean moveToNext() {
            return mCursor.moveToNext();
        }

        public boolean open(Location location) {
            String where = mWhereFactory.getWhere(location);

            mCursor = mSqliteWrapper.query(TBL_CACHES, READER_COLUMNS, where, null, null, null,
                    null, SQL_QUERY_LIMIT);
            final boolean result = mCursor.moveToFirst();
            if (!result)
                mCursor.close();
            return result;
        }
    }

    public static class CacheWriter {
        private static final String[] COLUMNS_NAME = new String[] {
            "Name"
        };
        private Object[] mBindArgs0 = new Object[0];
        private Object[] mBindArgs1 = new Object[1];
        private Object[] mBindArgs2 = new Object[2];
        private Object[] mBindArgs5 = new Object[5];
        private String[] mSelectionArgs2 = new String[2];
        private final SQLiteWrapper mSqlite;

        public CacheWriter(SQLiteWrapper sqlite) {
            mSqlite = sqlite;
        }

        public void clearCaches(String source) {
            mBindArgs1[0] = source;
            mSqlite.execSQL(SQL_CLEAR_CACHES, mBindArgs1);
        }

        public void clearEarlierLoads() {
            mSqlite.execSQL(SQL_DELETE_OLD_CACHES, mBindArgs0);
            mSqlite.execSQL(SQL_DELETE_OLD_GPX, mBindArgs0);
            mSqlite.execSQL(SQL_RESET_DELETE_ME_CACHES, mBindArgs0);
            mSqlite.execSQL(SQL_RESET_DELETE_ME_GPX, mBindArgs0);
        }

        public void deleteCache(CharSequence id) {
            mBindArgs1[0] = id;
            mSqlite.execSQL(Database.SQL_DELETE_CACHE, mBindArgs1);
        }

        public void insertAndUpdateCache(CharSequence id, CharSequence name, double latitude,
                double longitude, String source) {
            try {
                insertCache(id, name, latitude, longitude, source);
            } catch (final SQLiteConstraintException e) {
                // TODO: What if these queries have errors?
                deleteCache(id);
                insertCache(id, name, latitude, longitude, source);
            }
        }

        private void insertCache(CharSequence id, CharSequence name, double latitude,
                double longitude, String source) {
            mBindArgs5[0] = id;
            mBindArgs5[1] = name;
            mBindArgs5[2] = new Double(latitude);
            mBindArgs5[3] = new Double(longitude);
            mBindArgs5[4] = source;
            mSqlite.execSQL(Database.SQL_INSERT_CACHE, mBindArgs5);
        }

        public boolean isGpxAlreadyLoaded(String gpxName, String gpxTime) {
            mSelectionArgs2[0] = gpxName;
            mSelectionArgs2[1] = gpxTime;
            Cursor cursor = mSqlite.query(TBL_GPX, COLUMNS_NAME, "Name = ? AND ExportTime >= ?",
                    mSelectionArgs2, null, null, null, null);
            int count = cursor.getCount();
            boolean gpxAlreadyLoaded = count > 0;
            cursor.close();
            if (gpxAlreadyLoaded) {
                mBindArgs1[0] = gpxName;
                mSqlite.execSQL(SQL_CACHES_UNSET_DELETE_ME_FOR_SOURCE, mBindArgs1);
                mSqlite.execSQL(SQL_GPX_UNSET_DELETE_ME_FOR_SOURCE, mBindArgs1);
            }
            return gpxAlreadyLoaded;
        }

        public void startWriting() {
            mSqlite.beginTransaction();
        }

        public void stopWriting() {
            // TODO: abort if no writes--otherwise sqlite is unhappy.
            mSqlite.setTransactionSuccessful();
            mSqlite.endTransaction();
        }

        public void writeGpx(String gpxName, String pocketQueryExportTime) {
            mBindArgs2[0] = gpxName;
            mBindArgs2[1] = pocketQueryExportTime;
            mSqlite.execSQL(Database.SQL_REPLACE_GPX, mBindArgs2);
        }
    }

    public static class GeoBeagleSqliteOpenHelper extends SQLiteOpenHelper {
        private final OpenHelperDelegate mOpenHelperDelegate;

        public GeoBeagleSqliteOpenHelper(Context context, OpenHelperDelegate openHelperDelegate) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mOpenHelperDelegate = openHelperDelegate;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mOpenHelperDelegate.onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mOpenHelperDelegate.onUpgrade(db, oldVersion, newVersion);
        }
    }

    public static class OpenHelperDelegate {
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_CACHE_TABLE);
            db.execSQL(SQL_CREATE_GPX_TABLE);
            db.execSQL(SQL_CREATE_IDX_LATITUDE);
            db.execSQL(SQL_CREATE_IDX_LONGITUDE);
            db.execSQL(SQL_CREATE_IDX_SOURCE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 9) {
                db.execSQL(SQL_DROP_CACHE_TABLE);
                db.execSQL(SQL_CREATE_CACHE_TABLE);
                db.execSQL(SQL_CREATE_IDX_LATITUDE);
                db.execSQL(SQL_CREATE_IDX_LONGITUDE);
                db.execSQL(SQL_CREATE_IDX_SOURCE);
            }
            if (oldVersion < 10) {
                db.execSQL(SQL_CREATE_GPX_TABLE);
                db.execSQL(SQL_ADD_RECENTLY_LOADED_COLUMN_TO_CACHES);
            }
        }
    }

    public static class SQLiteWrapper {
        private SQLiteDatabase mSQLiteDatabase;

        public void beginTransaction() {
            mSQLiteDatabase.beginTransaction();
        }

        public void close() {
            mSQLiteDatabase.close();
        }

        public void endTransaction() {
            mSQLiteDatabase.endTransaction();
        }

        public void execSQL(String sql) {
            mSQLiteDatabase.execSQL(sql);
        }

        public void execSQL(String sql, Object[] bindArgs) {
            mSQLiteDatabase.execSQL(sql, bindArgs);
        }

        public void openReadableDatabase(Database database) {
            mSQLiteDatabase = database.getReadableDatabase();
        }

        public void openWritableDatabase(Database database) {
            mSQLiteDatabase = database.getWritableDatabase();
        }

        public Cursor query(String table, String[] columns, String selection,
                String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
            return mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy,
                    orderBy, having, limit);
        }

        /**
         * @param sql
         * @param mSelectionArgs1
         * @return
         */
        public Cursor rawQuery(String sql, String[] selectionArgs) {
            return mSQLiteDatabase.rawQuery(sql, selectionArgs);
        }

        public void setTransactionSuccessful() {
            mSQLiteDatabase.setTransactionSuccessful();
        }
    }

    /**
     * SCHEMA LOG:
     * 
     * <pre>
     * version 6
     * CREATE TABLE IF NOT EXISTS CACHES (Id VARCHAR PRIMARY KEY,
     *          Description VARCHAR Latitude DOUBLE, Longitude DOUBLE, Source
     *          VARCHAR)
     *          
     * version 7
     * CREATE TABLE IF NOT EXISTS CACHES (Id VARCHAR PRIMARY
     *          KEY, Description VARCHAR Latitude DOUBLE, Longitude DOUBLE,
     *          Source VARCHAR)
     * CREATE INDEX IDX_LATITUDE on CACHES (Latitude)
     * CREATE INDEX IDX_LONGITUDE on CACHES (Longitude)
     * CREATE INDEX IDX_SOURCE on CACHES (Source)
     * 
     * version 8
     * same as version 7 but rebuilds everything because a released version mistakenly puts 
     * *intent* into imported caches.
     * 
     * version 9
     * fixes bug where INDEX wasn't being created on upgrade.
     * 
     * version 10 -- not released
     * CREATE TABLE IF NOT EXISTS CACHES (Id VARCHAR PRIMARY
     *          KEY, Description VARCHAR Latitude DOUBLE, Longitude DOUBLE,
     *          Source VARCHAR, DeleteMe BOOLEAN NOT NULL)
     * CREATE TABLE IF NOT EXISTS GPX (Name VARCHAR PRIMARY KEY NOT NULL, ExportTime DATETIME NOT NULL, 
     *          DeleteMe BOOLEAN NOT NULL)
     * CREATE INDEX IDX_LATITUDE on CACHES (Latitude)
     * CREATE INDEX IDX_LONGITUDE on CACHES (Longitude)
     * CREATE INDEX IDX_SOURCE on CACHES (Source)
     * 
     * </pre>
     */

    public static final String DATABASE_NAME = "GeoBeagle.db";
    public static final int DATABASE_VERSION = 10;

    public static final String[] READER_COLUMNS = new String[] {
            "Latitude", "Longitude", "Id", "Description"
    };

    public static final String SQL_ADD_RECENTLY_LOADED_COLUMN_TO_CACHES = "ALTER TABLE CACHES ADD COLUMN DeleteMe BOOLEAN NOT NULL Default 1";
    public static final String SQL_CACHES_UNSET_DELETE_ME_FOR_SOURCE = "UPDATE CACHES SET DeleteMe = 0 WHERE Source = ?";
    public static final String SQL_CLEAR_CACHES = "DELETE FROM CACHES WHERE Source=?";
    public static final String SQL_COUNT_CACHES = "SELECT COUNT(*) FROM CACHES";
    public static final String SQL_CREATE_CACHE_TABLE = "CREATE TABLE IF NOT EXISTS CACHES ("
            + "Id VARCHAR PRIMARY KEY, Description VARCHAR, "
            + "Latitude DOUBLE, Longitude DOUBLE, Source VARCHAR, DeleteMe BOOLEAN NOT NULL)";
    public static final String SQL_CREATE_GPX_TABLE = "CREATE TABLE IF NOT EXISTS GPX ("
            + "Name VARCHAR PRIMARY KEY NOT NULL, ExportTime DATETIME NOT NULL, DeleteMe BOOLEAN NOT NULL)";
    public static final String SQL_CREATE_IDX_LATITUDE = "CREATE INDEX IF NOT EXISTS IDX_LATITUDE on CACHES (Latitude)";
    public static final String SQL_CREATE_IDX_LONGITUDE = "CREATE INDEX IF NOT EXISTS IDX_LONGITUDE on CACHES (Longitude)";
    public static final String SQL_CREATE_IDX_SOURCE = "CREATE INDEX IF NOT EXISTS IDX_SOURCE on CACHES (Source)";
    public static final String SQL_DELETE_CACHE = "DELETE FROM CACHES WHERE Id=?";
    public static final String SQL_DELETE_OLD_CACHES = "DELETE FROM CACHES WHERE DeleteMe = 1";
    public static final String SQL_DELETE_OLD_GPX = "DELETE FROM GPX WHERE DeleteMe = 1";
    public static final String SQL_DROP_CACHE_TABLE = "DROP TABLE IF EXISTS CACHES";
    public static final String SQL_GPX_UNSET_DELETE_ME_FOR_SOURCE = "UPDATE GPX SET DeleteMe = 0 WHERE Name = ?";
    public static final String SQL_INSERT_CACHE = "INSERT INTO CACHES "
            + "(Id, Description, Latitude, Longitude, Source, DeleteMe) VALUES (?, ?, ?, ?, ?, 0)";
    public static final String SQL_REPLACE_GPX = "REPLACE INTO GPX (Name, ExportTime, DeleteMe) VALUES (?, ?, 0)";
    public static final String SQL_RESET_DELETE_ME_CACHES = "UPDATE CACHES SET DeleteMe = 1 WHERE Source != 'Intent'";
    public static final String SQL_RESET_DELETE_ME_GPX = "UPDATE GPX SET DeleteMe = 1";
    public static final String TBL_CACHES = "CACHES";
    public static final String TBL_GPX = "GPX";

    public static Database create(Context context) {
        final OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        final GeoBeagleSqliteOpenHelper sqliteOpenHelper = new GeoBeagleSqliteOpenHelper(context,
                openHelperDelegate);
        return new Database(sqliteOpenHelper);
    }

    private final SQLiteOpenHelper mSqliteOpenHelper;

    public Database(SQLiteOpenHelper sqliteOpenHelper) {
        mSqliteOpenHelper = sqliteOpenHelper;
    }

    public CacheWriter createCacheWriter(SQLiteWrapper sqliteWrapper) {
        return new CacheWriter(sqliteWrapper);
    }

    public SQLiteDatabase getReadableDatabase() {
        return mSqliteOpenHelper.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        return mSqliteOpenHelper.getWritableDatabase();
    }
}
