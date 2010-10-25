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

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ClearCachesFromSourceImpl implements ClearCachesFromSource {
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
        ISQLiteDatabase sqliteDatabase = sqliteProvider.get();
        for (String sql : CacheSqlWriter.SQLS_CLEAR_EARLIER_LOADS) {
            sqliteDatabase.execSQL(sql);
        }
    }
}