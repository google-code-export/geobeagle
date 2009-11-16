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
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;

import android.database.Cursor;

//TODO: Merge class CacheReaderCursor into DbFrontend?
public class CacheReaderCursor {
    private final Cursor mCursor;
    private final DbToGeocacheAdapter mDbToGeocacheAdapter;
    private final GeocacheFactory mGeocacheFactory;

    public CacheReaderCursor(Cursor cursor, GeocacheFactory geocacheFactory,
            DbToGeocacheAdapter dbToGeocacheAdapter) {
        mCursor = cursor;
        mGeocacheFactory = geocacheFactory;
        mDbToGeocacheAdapter = dbToGeocacheAdapter;
    }

    public void close() {
        mCursor.close();
    }

    public Geocache getCache() {
        String sourceName = mCursor.getString(4);

        CacheType cacheType = mGeocacheFactory.cacheTypeFromInt(Integer.parseInt(mCursor
                .getString(5)));
        int difficulty = Integer.parseInt(mCursor.getString(6));
        int terrain = Integer.parseInt(mCursor.getString(7));
        int container = Integer.parseInt(mCursor.getString(8));
        return mGeocacheFactory.create(mCursor.getString(2), mCursor.getString(3), mCursor
                .getDouble(0), mCursor.getDouble(1), mDbToGeocacheAdapter
                .sourceNameToSourceType(sourceName), sourceName, cacheType, difficulty, terrain,
                container);
    }

    public int count() {
        return mCursor.getCount();
    }

    public boolean moveToNext() {
        return mCursor.moveToNext();
    }
}
