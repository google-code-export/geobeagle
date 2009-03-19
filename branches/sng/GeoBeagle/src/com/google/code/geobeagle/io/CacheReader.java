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

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.di.GeocacheFactory;
import com.google.code.geobeagle.io.di.DatabaseDI;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;

import android.database.Cursor;
import android.location.Location;

public class CacheReader {
    public static class CacheReaderCursor {
        private final Cursor mCursor;
        private final GeocacheFactory mGeocacheFactory;

        public CacheReaderCursor(Cursor cursor, GeocacheFactory geocacheFactory) {
            mCursor = cursor;
            mGeocacheFactory = geocacheFactory;
        }

        void close() {
            mCursor.close();
        }

        public Geocache getCache() {
            return mGeocacheFactory.create(Geocache.PROVIDER_GROUNDSPEAK, mCursor.getString(2),
                    mCursor.getString(3), mCursor.getDouble(0), mCursor.getDouble(1));
        }

        boolean moveToNext() {
            return mCursor.moveToNext();
        }
    }

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
    private final DatabaseDI.CacheReaderCursorFactory mCacheReaderCursorFactory;
    private final SQLiteWrapper mSqliteWrapper;
    private final WhereFactory mWhereFactory;

    // TODO: rename to CacheSqlReader / CacheSqlWriter
    public CacheReader(DatabaseDI.SQLiteWrapper sqliteWrapper, WhereFactory whereFactory,
            DatabaseDI.CacheReaderCursorFactory cacheReaderCursorFactory) {
        mSqliteWrapper = sqliteWrapper;
        mWhereFactory = whereFactory;
        mCacheReaderCursorFactory = cacheReaderCursorFactory;
    }

    public int getTotalCount() {
        return mSqliteWrapper.countResults(Database.TBL_CACHES, null);
    }

    public CacheReaderCursor open(Location location) {
        String where = mWhereFactory.getWhere(location);

        Cursor cursor = mSqliteWrapper.query(Database.TBL_CACHES, Database.READER_COLUMNS, where,
                null, null, null, SQL_QUERY_LIMIT);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return mCacheReaderCursorFactory.create(cursor);
    }
}
