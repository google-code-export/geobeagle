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

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;

/**
 * Takes complete responsibility of opening and accessing a database to load
 * geocaches.
 */
public class GeocachesLoader {
    private CacheReader mCacheReader;
    private final Context mContext;
    boolean mIsDatabaseOpen;
    private GeoBeagleSqliteOpenHelper mOpenHelper;
    private ISQLiteDatabase mDatabase;

    public GeocachesLoader(Context context) {
        mContext = context;
        mIsDatabaseOpen = false;
    }

    public void closeDatabase() {
        if (!mIsDatabaseOpen)
            return;
        Log.d("GeoBeagle", "GeocachesLoader.closeDatabase()");
        mIsDatabaseOpen = false;

        mOpenHelper.close();
    }

    public ArrayList<Geocache> loadCaches(double latitude, double longitude,
            WhereFactory whereFactory) {
        openDatabase();

        CacheReaderCursor cursor = mCacheReader.open(latitude, longitude, whereFactory, null);
        ArrayList<Geocache> geocaches = new ArrayList<Geocache>();
        if (cursor != null) {
            do {
                geocaches.add(cursor.getCache());
            } while (cursor.moveToNext());
            cursor.close();
        }
        return geocaches;
    }

    public void openDatabase() {
        if (mIsDatabaseOpen)
            return;
        Log.d("GeoBeagle", "GeocachesLoader.openDatabase()");
        mIsDatabaseOpen = true;

        mOpenHelper = new GeoBeagleSqliteOpenHelper(mContext);
        final SQLiteDatabase sqDb = mOpenHelper.getReadableDatabase();
        mDatabase = new DatabaseDI.SQLiteWrapper(sqDb);
        mCacheReader = DatabaseDI.createCacheReader(mDatabase);
    }

    public ArrayList<Geocache> loadCaches(int latitude, int longitude,
            WhereFactoryFixedArea whereFactory, int limit) {
        openDatabase();

        Cursor countCursor = mDatabase.rawQuery("SELECT COUNT(*) FROM " + Database.TBL_CACHES
                + " WHERE " + whereFactory.getWhere(mDatabase, latitude, longitude), null);
        countCursor.moveToFirst();
        int count = countCursor.getInt(0);
        countCursor.close();
        Log.d("GeoBeagle", "COUNT:" + count);
        if (count > 1500)
            return null;

        CacheReaderCursor cursor = mCacheReader.open(latitude, longitude, whereFactory, null);
        ArrayList<Geocache> geocaches = new ArrayList<Geocache>();
        if (cursor != null) {
            do {
                geocaches.add(cursor.getCache());
            } while (cursor.moveToNext());
            cursor.close();
        }
        return geocaches;
    }

}
