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

import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {
    public static class CacheReader {
        private Cursor mCursor;
        private final SQLiteDatabase mSqliteDatabase;
        private final SQLiteWrapper mSqliteWrapper;

        public CacheReader(SQLiteDatabase sqliteDatabase, SQLiteWrapper sqliteWrapper) {
            mSqliteDatabase = sqliteDatabase;
            mSqliteWrapper = sqliteWrapper;
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

        public boolean moveToNext() {
            return mCursor.moveToNext();
        }

        public boolean open() {
            try {
                mCursor = mSqliteWrapper.query(mSqliteDatabase, TBL_CACHES, READER_COLUMNS, null,
                        null, null, null, null);
            } catch (SQLiteException e) {
                e.printStackTrace();
                return false;
            }
            final boolean result = mCursor.moveToFirst();
            if (!result)
                mCursor.close();
            return result;
        }
    }

    public static class CacheWriter {
        private final ErrorDisplayer mErrorDisplayer;
        private final SQLiteWrapper mSqlite;

        public CacheWriter(SQLiteWrapper sqlite, ErrorDisplayer errorDisplayer) {
            mSqlite = sqlite;
            mErrorDisplayer = errorDisplayer;
        }

        public void clear(String source) {
            mSqlite.execSQL(SQL_CLEAR_CACHES, new Object[] {
                source
            });
        }

        public void delete(CharSequence id) {
            mSqlite.execSQL(Database.SQL_DELETE_CACHE, new Object[] {
                id
            });
        }

        private void insert(CharSequence id, CharSequence name, double latitude, double longitude,
                String source) {
            mSqlite.execSQL(Database.SQL_INSERT_CACHE, new Object[] {
                    id, name, new Double(latitude), new Double(longitude), source
            });
        }

        public void startWriting() {
            mSqlite.beginTransaction();
        }

        public void stopWriting() {
            mSqlite.setTransactionSuccessful();
            mSqlite.endTransaction();
        }

        private void tryInsertAndUpdate(CharSequence id, CharSequence name, double latitude,
                double longitude, String source) {
            try {
                insert(id, name, latitude, longitude, source);
            } catch (final SQLiteConstraintException e) {
                delete(id);
                insert(id, name, latitude, longitude, source);
            }
        }

        public boolean write(CharSequence id, CharSequence name, double latitude, double longitude,
                String source) {
            try {
                tryInsertAndUpdate(id, name, latitude, longitude, source);
            } catch (final SQLiteException e) {
                mErrorDisplayer.displayError("Error writing cache: " + e.toString());
                return false;
            }
            return true;
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
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DROP_CACHE_TABLE);
            onCreate(db);
        }
    }

    public static class SQLiteWrapper {
        SQLiteDatabase mSQLiteDatabase;

        public void beginTransaction() {
            mSQLiteDatabase.beginTransaction();
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

        public void open(SQLiteDatabase sqliteDatabase) {
            mSQLiteDatabase = sqliteDatabase;
        }

        public void close() {
            mSQLiteDatabase.close();
        }

        public Cursor query(SQLiteDatabase db, String table, String[] columns, String selection,
                String[] selectionArgs, String groupBy, String having, String orderBy) {
            return db.query(table, columns, selection, selectionArgs, groupBy, orderBy, having);
        }

        public void setTransactionSuccessful() {
            mSQLiteDatabase.setTransactionSuccessful();
        }
    }

    public static final String DATABASE_NAME = "GeoBeagle.db";

    public static final int DATABASE_VERSION = 6;
    public static final String[] READER_COLUMNS = new String[] {
            "Latitude", "Longitude", "Id", "Description"
    };
    public static final String SQL_CLEAR_CACHES = "DELETE FROM CACHES WHERE Source=?";
    public static final String SQL_CREATE_CACHE_TABLE = "CREATE TABLE IF NOT EXISTS CACHES ("
            + "Id VARCHAR PRIMARY KEY, Description VARCHAR, "
            + "Latitude DOUBLE, Longitude DOUBLE, Source VARCHAR)";
    public static final String SQL_DELETE_CACHE = "DELETE FROM CACHES WHERE Id=?";
    public static final String SQL_DROP_CACHE_TABLE = "DROP TABLE CACHES";
    public static final String SQL_INSERT_CACHE = "INSERT INTO CACHES "
            + "(Id, Description, Latitude, Longitude, Source) " + "VALUES (?, ?, ?, ?, ?)";
    public static final String TBL_CACHES = "CACHES";

    public static Database create(Context context) {
        return new Database(new SQLiteWrapper(), new GeoBeagleSqliteOpenHelper(context,
                new OpenHelperDelegate()));
    }

    private final SQLiteOpenHelper mSqliteOpenHelper;
    private final SQLiteWrapper mSqliteWrapper;

    public Database(SQLiteWrapper sqliteWrapper, SQLiteOpenHelper sqliteOpenHelper) {
        mSqliteWrapper = sqliteWrapper;
        mSqliteOpenHelper = sqliteOpenHelper;
    }

    public CacheReader createCacheReader(SQLiteDatabase sqlite) {
        return new CacheReader(sqlite, mSqliteWrapper);
    }

    public CacheWriter createCacheWriter(SQLiteWrapper sqlite, ErrorDisplayer errorDisplayer) {
        return new CacheWriter(sqlite, errorDisplayer);
    }

    public SQLiteDatabase openOrCreateCacheDatabase() {
        // TODO: need to create read-only database too.
        return mSqliteOpenHelper.getWritableDatabase();
    }

    public SQLiteWrapper getWritableDatabase() {
        SQLiteWrapper sqliteWrapper = new SQLiteWrapper();
        sqliteWrapper.open(mSqliteOpenHelper.getWritableDatabase());
        return sqliteWrapper;
        
    }
}
