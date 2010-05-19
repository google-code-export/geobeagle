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

import com.google.code.geobeagle.database.DatabaseDI.CacheReaderCursorFactory;
import com.google.inject.Provider;

import android.database.Cursor;

public class CacheReader {
    public static final String[] READER_COLUMNS = new String[] {
            "Latitude", "Longitude", "Id", "Description", "Source", "CacheType", "Difficulty",
            "Terrain", "Container"
    };

    public static final String SQL_QUERY_LIMIT = "1000";
    private final CacheReaderCursorFactory mCacheReaderCursorFactory;
    private final Provider<ISQLiteDatabase> mSqliteWrapper;

    CacheReader(Provider<ISQLiteDatabase> sqliteDatabaseProvider, CacheReaderCursorFactory cacheReaderCursorFactory) {
        mSqliteWrapper = sqliteDatabaseProvider;
        mCacheReaderCursorFactory = cacheReaderCursorFactory;
    }

    public int getTotalCount() {
        Cursor cursor = mSqliteWrapper.get()
                .rawQuery("SELECT COUNT(*) FROM " + Database.TBL_CACHES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public CacheReaderCursor open(double latitude, double longitude, WhereFactory whereFactory,
            String limit) {
        String where = whereFactory.getWhere(mSqliteWrapper.get(), latitude, longitude);
        Cursor cursor = mSqliteWrapper.get().query(Database.TBL_CACHES, CacheReader.READER_COLUMNS,
                where, null, null, null, limit);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return mCacheReaderCursorFactory.create(cursor);
    }

    public CacheReaderCursor open(CharSequence cacheId) {
        Cursor cursor = mSqliteWrapper.get().query(Database.TBL_CACHES, CacheReader.READER_COLUMNS,
                "Id='" + cacheId + "'", null, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return mCacheReaderCursorFactory.create(cursor);   
    }
}
