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

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author sng
 */
public class CacheWriter {
    public static final String SQLS_CLEAR_EARLIER_LOADS[] = {
            Database.SQL_DELETE_OLD_CACHES, Database.SQL_DELETE_OLD_GPX,
            Database.SQL_RESET_DELETE_ME_CACHES, Database.SQL_RESET_DELETE_ME_GPX
    };
    private final DbToGeocacheAdapter mDbToGeocacheAdapter;
    private String mGpxTime;
    private final Provider<ISQLiteDatabase> sqliteProvider;

    public static class ClearCachesFromSourceImpl implements ClearCachesFromSource {
        private final Provider<ISQLiteDatabase> sqliteProvider;

        @Inject
        public
        ClearCachesFromSourceImpl(Provider<ISQLiteDatabase> sqliteProvider) {
            this.sqliteProvider = sqliteProvider;
        }

        @Override
        public void clearCaches(String source) {
            sqliteProvider.get().execSQL(Database.SQL_CLEAR_CACHES, source);
        }

        /**
         * Deletes any cache/gpx entries marked delete_me, then marks all remaining
         * gpx-based caches, and gpx entries with delete_me = 1.
         */
        @Override
        public void clearEarlierLoads() {
            for (String sql : CacheWriter.SQLS_CLEAR_EARLIER_LOADS) {
                sqliteProvider.get().execSQL(sql);
            }
        }
    }

    public static class ClearCachesFromSourceNull implements ClearCachesFromSource {

        @Inject
        public
        ClearCachesFromSourceNull() {
        }
        
        @Override
        public void clearCaches(String source) {
        }

        @Override
        public void clearEarlierLoads() {
        }

    }

    @Inject
    CacheWriter(Provider<ISQLiteDatabase> writableDatabaseProvider,
            DbToGeocacheAdapter dbToGeocacheAdapter) {
        this.sqliteProvider = writableDatabaseProvider;
        mDbToGeocacheAdapter = dbToGeocacheAdapter;
    }


    public void deleteCache(CharSequence id) {
        sqliteProvider.get().execSQL(Database.SQL_DELETE_CACHE, id);
    }

    public void insertAndUpdateCache(CharSequence id, CharSequence name, double latitude,
            double longitude, Source sourceType, String sourceName, CacheType cacheType,
            int difficulty, int terrain, int container) {
        sqliteProvider.get().execSQL(Database.SQL_REPLACE_CACHE, id, name, new Double(latitude),
                new Double(longitude),
                mDbToGeocacheAdapter.sourceTypeToSourceName(sourceType, sourceName),
                cacheType.toInt(), difficulty, terrain, container);
    }

    /**
     * Return True if the gpx is already loaded. Mark this gpx and its caches in
     * the database to protect them from being nuked when the load is complete.
     * 
     * @param gpxName
     * @param gpxTime
     * @return
     */
    public boolean isGpxAlreadyLoaded(String gpxName, String gpxTime) {
        mGpxTime = gpxTime;
        // TODO:countResults is slow; replace with a query, and moveToFirst.
        ISQLiteDatabase sqliteDatabase = sqliteProvider.get();
        boolean gpxAlreadyLoaded = sqliteDatabase.countResults(Database.TBL_GPX,
                Database.SQL_MATCH_NAME_AND_EXPORTED_LATER, gpxName, gpxTime) > 0;
        if (gpxAlreadyLoaded) {
            sqliteDatabase.execSQL(Database.SQL_CACHES_DONT_DELETE_ME, gpxName);
            sqliteDatabase.execSQL(Database.SQL_GPX_DONT_DELETE_ME, gpxName);
        }
        return gpxAlreadyLoaded;
    }

    public void startWriting() {
        sqliteProvider.get().beginTransaction();
    }

    public void stopWriting() {
        // TODO: abort if no writes--otherwise sqlite is unhappy.
        ISQLiteDatabase sqliteDatabase = sqliteProvider.get();
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
    }

    public void writeGpx(String gpxName) {
        sqliteProvider.get().execSQL(Database.SQL_REPLACE_GPX, gpxName, mGpxTime);
    }
}
