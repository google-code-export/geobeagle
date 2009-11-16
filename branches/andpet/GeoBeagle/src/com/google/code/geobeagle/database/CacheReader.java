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

import android.database.Cursor;

//TODO: Merge into DbFrontend
public class CacheReader {
    public static final String[] READER_COLUMNS = new String[] {
            "Latitude", "Longitude", "Id", "Description", "Source", "CacheType", "Difficulty",
            "Terrain", "Container"
    };

    //public static final String SQL_QUERY_LIMIT = "1000";
    private final CacheReaderCursorFactory mCacheReaderCursorFactory;
    private final ISQLiteDatabase mSqliteWrapper;

    CacheReader(ISQLiteDatabase sqliteWrapper, CacheReaderCursorFactory cacheReaderCursorFactory) {
        mSqliteWrapper = sqliteWrapper;
        mCacheReaderCursorFactory = cacheReaderCursorFactory;
    }

    public int getTotalCount() {
        Cursor cursor = mSqliteWrapper
                .rawQuery("SELECT COUNT(*) FROM " + Database.TBL_CACHES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /** Returns null if no caches were found to match. */
    public CacheReaderCursor open(String where, String limit) {
        Cursor cursor = mSqliteWrapper.query(Database.TBL_CACHES, CacheReader.READER_COLUMNS,
                where, null, null, null, limit);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return mCacheReaderCursorFactory.create(cursor);
    }
}
