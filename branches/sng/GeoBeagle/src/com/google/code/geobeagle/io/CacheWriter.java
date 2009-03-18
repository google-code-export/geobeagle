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

import com.google.code.geobeagle.io.di.DatabaseDI;

import android.database.Cursor;

public class CacheWriter {
    private static final String[] COLUMNS_NAME = new String[] {
        "Name"
    };
    private Object[] mBindArgs0 = new Object[0];
    private Object[] mBindArgs1 = new Object[1];
    private Object[] mBindArgs2 = new Object[2];
    private Object[] mBindArgs5 = new Object[5];
    private String[] mSelectionArgs2 = new String[2];
    private final DatabaseDI.SQLiteWrapper mSqlite;

    public CacheWriter(DatabaseDI.SQLiteWrapper sqlite) {
        mSqlite = sqlite;
    }

    public void clearCaches(String source) {
        mBindArgs1[0] = source;
        mSqlite.execSQL(Database.SQL_CLEAR_CACHES, mBindArgs1);
    }

    public void clearEarlierLoads() {
        mSqlite.execSQL(Database.SQL_DELETE_OLD_CACHES, mBindArgs0);
        mSqlite.execSQL(Database.SQL_DELETE_OLD_GPX, mBindArgs0);
        mSqlite.execSQL(Database.SQL_RESET_DELETE_ME_CACHES, mBindArgs0);
        mSqlite.execSQL(Database.SQL_RESET_DELETE_ME_GPX, mBindArgs0);
    }

    public void deleteCache(CharSequence id) {
        mBindArgs1[0] = id;
        mSqlite.execSQL(Database.SQL_DELETE_CACHE, mBindArgs1);
    }

    public void insertAndUpdateCache(CharSequence id, CharSequence name, double latitude,
            double longitude, String source) {
        mBindArgs5[0] = id;
        mBindArgs5[1] = name;
        mBindArgs5[2] = new Double(latitude);
        mBindArgs5[3] = new Double(longitude);
        mBindArgs5[4] = source;
        mSqlite.execSQL(Database.SQL_REPLACE_CACHE, mBindArgs5);
    }

    public boolean isGpxAlreadyLoaded(String gpxName, String gpxTime) {
        mSelectionArgs2[0] = gpxName;
        mSelectionArgs2[1] = gpxTime;
        Cursor cursor = mSqlite.query(Database.TBL_GPX, COLUMNS_NAME, "Name = ? AND ExportTime >= ?",
                mSelectionArgs2, null, null, null, null);
        int count = cursor.getCount();
        boolean gpxAlreadyLoaded = count > 0;
        cursor.close();
        if (gpxAlreadyLoaded) {
            mBindArgs1[0] = gpxName;
            mSqlite.execSQL(Database.SQL_CACHES_UNSET_DELETE_ME_FOR_SOURCE, mBindArgs1);
            mSqlite.execSQL(Database.SQL_GPX_UNSET_DELETE_ME_FOR_SOURCE, mBindArgs1);
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