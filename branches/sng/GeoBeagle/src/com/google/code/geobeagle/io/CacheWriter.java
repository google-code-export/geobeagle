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

import com.google.code.geobeagle.io.Database.ISQLiteDatabase;

/**
 * @author sng
 */
public class CacheWriter {
    private final ISQLiteDatabase mSqlite;

    public CacheWriter(ISQLiteDatabase sqlite) {
        mSqlite = sqlite;
    }

    public void clearCaches(String source) {
        mSqlite.execSQL(Database.SQL_CLEAR_CACHES, source);
    }

    /**
     * Deletes any cache/gpx entries marked delete_me, then marks all remaining
     * gpx-based caches, and gpx entries with delete_me = 1.
     */
    public void clearEarlierLoads() {
        mSqlite.execSQL(Database.SQL_CLEAR_EARLIER_LOADS);
    }

    public void deleteCache(CharSequence id) {
        mSqlite.execSQL(Database.SQL_DELETE_CACHE, id);
    }

    public void insertAndUpdateCache(CharSequence id, CharSequence name, double latitude,
            double longitude, String source) {
        mSqlite.execSQL(Database.SQL_REPLACE_CACHE, id, name, new Double(latitude), new Double(
                longitude), source);
    }

    public boolean isGpxAlreadyLoaded(String gpxName, String gpxTime) {
        boolean gpxAlreadyLoaded = mSqlite.countResults(Database.TBL_GPX,
                Database.SQL_MATCH_NAME_AND_EXPORTED_LATER, gpxName, gpxTime) > 0;
        if (gpxAlreadyLoaded) {
            mSqlite.execSQL(Database.SQL_CACHES_DONT_DELETE_ME, gpxName);
            mSqlite.execSQL(Database.SQL_GPX_DONT_DELETE_ME, gpxName);
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
        mSqlite.execSQL(Database.SQL_REPLACE_GPX, gpxName, pocketQueryExportTime);
    }
}
