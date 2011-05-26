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
package com.google.code.geobeagle.cachedetails;

import com.google.inject.Inject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DetailsDatabaseReader {
    private static final String TABLE_DETAILS = "Details";
    private static final String COLUMN_DETAILS = "Details";
    private final SdDatabaseOpener sdDatabaseOpener;
    private final String[] columns = new String[] {
        COLUMN_DETAILS
    };

    @Inject
    DetailsDatabaseReader(SdDatabaseOpener sdDatabaseOpener) {
        this.sdDatabaseOpener = sdDatabaseOpener;
    }

    public String read(CharSequence cacheId) {
        SQLiteDatabase sdDatabase = sdDatabaseOpener.open();
        String[] selectionArgs = {
            (String)cacheId
        };
        Cursor cursor = sdDatabase.query(TABLE_DETAILS, columns, "CacheId=?", selectionArgs, null,
                null, null);
        Log.d("GeoBeagle", "count: " + cursor.getCount() + ", " + cursor.getColumnCount());
        cursor.moveToFirst();
        if (cursor.getCount() < 1)
            return null;
        String details = cursor.getString(0);
        cursor.close();
        Log.d("GeoBeagle", "DETAILS: " + details);
        sdDatabase.close();
        return details;
    }

}
