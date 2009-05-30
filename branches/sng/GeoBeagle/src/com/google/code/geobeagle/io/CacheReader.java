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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.io.DatabaseDI.CacheReaderCursorFactory;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.mainactivity.GeocacheFactory;

import android.database.Cursor;
import android.location.Location;

public class CacheReader {
    public static class CacheReaderCursor {
        private final Cursor mCursor;
        private final DbToGeocacheAdapter mDbToGeocacheAdapter;
        private final GeocacheFactory mGeocacheFactory;

        public CacheReaderCursor(Cursor cursor, GeocacheFactory geocacheFactory,
                DbToGeocacheAdapter dbToGeocacheAdapter) {
            mCursor = cursor;
            mGeocacheFactory = geocacheFactory;
            mDbToGeocacheAdapter = dbToGeocacheAdapter;
        }

        void close() {
            mCursor.close();
        }

        public Geocache getCache() {
            String sourceName = mCursor.getString(4);
            return mGeocacheFactory.create(mCursor.getString(2), mCursor.getString(3), mCursor
                    .getDouble(0), mCursor.getDouble(1), mDbToGeocacheAdapter
                    .sourceNameToSourceType(sourceName), sourceName);
        }

        boolean moveToNext() {
            return mCursor.moveToNext();
        }
    }

    public static class WhereFactoryAllCaches implements WhereFactory {
        public String getWhere(Location location) {
            return null;
        }
    }

    public static class WhereFactoryNearestCaches implements WhereFactory {
        // 1 degree ~= 111km
        public static final double DEGREES_DELTA = 0.08;

        public String getWhere(Location location) {
            if (location == null)
                return null;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            double latLow = latitude - WhereFactoryNearestCaches.DEGREES_DELTA;
            double latHigh = latitude + WhereFactoryNearestCaches.DEGREES_DELTA;
            double lat_radians = Math.toRadians(latitude);
            double cos_lat = Math.cos(lat_radians);
            double lonLow = Math.max(-180, longitude - WhereFactoryNearestCaches.DEGREES_DELTA
                    / cos_lat);
            double lonHigh = Math.min(180, longitude + WhereFactoryNearestCaches.DEGREES_DELTA
                    / cos_lat);
            return "Latitude > " + latLow + " AND Latitude < " + latHigh + " AND Longitude > "
                    + lonLow + " AND Longitude < " + lonHigh;
        }
    }

    public static final String SQL_QUERY_LIMIT = "1000";
    private final CacheReaderCursorFactory mCacheReaderCursorFactory;
    private final SQLiteWrapper mSqliteWrapper;

    // TODO: rename to CacheSqlReader / CacheSqlWriter
    CacheReader(SQLiteWrapper sqliteWrapper, CacheReaderCursorFactory cacheReaderCursorFactory) {
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

    public CacheReaderCursor open(Location location, WhereFactory whereFactory) {
        String where = whereFactory.getWhere(location);

        Cursor cursor = mSqliteWrapper.query(Database.TBL_CACHES, Database.READER_COLUMNS, where,
                null, null, null, SQL_QUERY_LIMIT);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return mCacheReaderCursorFactory.create(cursor);
    }
}
