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
import android.location.Location;

public class CacheReader {
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

    private Cursor mCursor;
    private final DatabaseDI.SQLiteWrapper mSqliteWrapper;
    private final WhereFactory mWhereFactory;

    // TODO: rename to CacheSqlReader / CacheSqlWriter
    public CacheReader(DatabaseDI.SQLiteWrapper sqliteWrapper, WhereFactory whereFactory) {
        mSqliteWrapper = sqliteWrapper;
        mWhereFactory = whereFactory;
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

    public int getTotalCount() {
        Cursor cursor = mSqliteWrapper.rawQuery(Database.SQL_COUNT_CACHES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public boolean moveToNext() {
        return mCursor.moveToNext();
    }

    public boolean open(Location location) {
        String where = mWhereFactory.getWhere(location);

        mCursor = mSqliteWrapper.query(Database.TBL_CACHES, Database.READER_COLUMNS, where, null,
                null, null, null, SQL_QUERY_LIMIT);
        final boolean result = mCursor.moveToFirst();
        if (!result)
            mCursor.close();
        return result;
    }
}
