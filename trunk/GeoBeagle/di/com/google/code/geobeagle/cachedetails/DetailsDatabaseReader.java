
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
        String details = cursor.getString(0);
        if (cursor.getCount() < 1)
            return "";
        cursor.close();
        Log.d("GeoBeagle", "DETAILS: " + details);
        sdDatabase.close();
        return details;
    }

}
