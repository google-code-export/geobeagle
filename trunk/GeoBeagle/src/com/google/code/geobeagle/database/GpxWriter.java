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

import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class GpxWriter {
    private String gpxTime;
    private final Provider<ISQLiteDatabase> sqliteProvider;
    private final String[] queryArgs = new String[1];
    private final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    GpxWriter(Provider<ISQLiteDatabase> sqliteProvider) {
        this.sqliteProvider = sqliteProvider;
        this.gpxTime = "2000-01-01 01:00:00.000";
    }

    /**
     * Return True if the gpx is already loaded. Mark this gpx and its caches in
     * the database to protect them from being nuked when the load is complete.
     *
     * @param gpxName
     * @param gpxTimeString
     * @return
     */
    public boolean isGpxAlreadyLoaded(String gpxName, String gpxTimeString) {
        Cursor cursor = null;
        ISQLiteDatabase sqliteDatabase;
        String dbTimeString = "";
        try {
            gpxTime = gpxTimeString;
            sqliteDatabase = sqliteProvider.get();
            queryArgs[0] = gpxName;
            cursor = sqliteDatabase.rawQuery(Database.SQL_GET_EXPORT_TIME, queryArgs);
            if (!cursor.moveToFirst()) {
                return false;
            }
            dbTimeString = cursor.getString(0);
            Date gpxTime = sqlDateFormat.parse(gpxTimeString);
            Date dbTime = sqlDateFormat.parse(dbTimeString);
            if (gpxTime.after(dbTime)) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            Log.d("GeoBeagle", "error parsing dates:" + gpxTimeString + ", " + dbTimeString);
            return false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void writeGpx(String gpxName) {
        sqliteProvider.get().execSQL(Database.SQL_REPLACE_GPX, gpxName, gpxTime);
        gpxTime = "2000-01-01 01:00:00.000";
    }

}
