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
import com.google.code.geobeagle.database.filter.Filter;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author sng
 */
public class CacheSqlWriter {
    static final String ANALYZE = "ANALYZE";
    public static final String SQLS_CLEAR_EARLIER_LOADS[] = {
            Database.SQL_DELETE_OLD_CACHES, Database.SQL_DELETE_OLD_GPX,
            Database.SQL_RESET_DELETE_ME_CACHES, Database.SQL_RESET_DELETE_ME_GPX
    };
    private final DbToGeocacheAdapter dbToGeocacheAdapter;
    private final Provider<ISQLiteDatabase> sqliteProvider;
    private final Filter filter;

    @Inject
    CacheSqlWriter(Provider<ISQLiteDatabase> writableDatabaseProvider,
            DbToGeocacheAdapter dbToGeocacheAdapter,
            Filter filter) {
        sqliteProvider = writableDatabaseProvider;
        this.dbToGeocacheAdapter = dbToGeocacheAdapter;
        this.filter = filter;
    }


    public void deleteCache(CharSequence id) {
        sqliteProvider.get().execSQL(Database.SQL_DELETE_CACHE, id);
    }

    public void insertAndUpdateCache(CharSequence id,
            CharSequence name,
            double latitude,
            double longitude,
            Source sourceType,
            String sourceName,
            CacheType cacheType,
            int difficulty,
            int terrain,
            int container,
            boolean available,
            boolean archived,
            boolean found) {
        boolean visible = filter.showBasedOnFoundState(found)
                && filter.showBasedOnAvailableState(available)
                && filter.showBasedOnCacheType(cacheType) && filter.showBasedOnDnfState(id);

        sqliteProvider.get().execSQL(Database.SQL_REPLACE_CACHE, id, name, new Double(latitude),
                new Double(longitude),
                dbToGeocacheAdapter.sourceTypeToSourceName(sourceType, sourceName),
                cacheType.toInt(), difficulty, terrain, container, available, archived, visible);
    }

    public void startWriting() {
        sqliteProvider.get().beginTransaction();
    }

    public void stopWriting() {
        // TODO: abort if no writes--otherwise sqlite is unhappy.
        ISQLiteDatabase sqliteDatabase = sqliteProvider.get();
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.execSQL(ANALYZE);
    }
}
