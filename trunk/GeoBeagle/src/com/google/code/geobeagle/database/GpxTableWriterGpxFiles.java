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

import com.google.code.geobeagle.xmlimport.SyncCollectingParameter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class GpxTableWriterGpxFiles implements GpxTableWriter {
    private String currentGpxTimeString;
    private final Provider<ISQLiteDatabase> sqliteProvider;
    private final String[] queryArgs = new String[1];
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM-dd HH:mm");
    private final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SyncCollectingParameter syncCollectingParameter;

    @Inject
    GpxTableWriterGpxFiles(Provider<ISQLiteDatabase> sqliteProvider,
            SyncCollectingParameter syncCollectingParameter) {

        this.sqliteProvider = sqliteProvider;
        this.currentGpxTimeString = "2000-01-01 01:00:00.000";
        this.syncCollectingParameter = syncCollectingParameter;
    }

    /**
     * Return True if the gpx is already loaded. Mark this gpx and its caches in
     * the database to protect them from being nuked when the load is complete.
     *
     * @param gpxName
     * @param gpxTimeString
     * @return
     */
    @Override
    public boolean isGpxAlreadyLoaded(String gpxName, String gpxTimeString) {
        Cursor cursor = null;
        ISQLiteDatabase sqliteDatabase;
        String dbTimeString = "";
        try {
            currentGpxTimeString = gpxTimeString;

            sqliteDatabase = sqliteProvider.get();
            queryArgs[0] = gpxName;
            cursor = sqliteDatabase.rawQuery(Database.SQL_GET_EXPORT_TIME, queryArgs);
            if (!cursor.moveToFirst()) {
                syncCollectingParameter.Log("  initial sync");
                return false;
            }
            dbTimeString = cursor.getString(0);
            Date gpxTime = sqlDateFormat.parse(gpxTimeString);
            Date dbTime = sqlDateFormat.parse(dbTimeString);
            if (gpxTime.after(dbTime)) {
                syncCollectingParameter.Log(displayDateFormat.format(dbTime) + " --> "
                        + displayDateFormat.format(gpxTime));
                return false;
            }
            syncCollectingParameter.Log("  no changes since " + displayDateFormat.format(dbTime));
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
        sqliteProvider.get().execSQL(Database.SQL_REPLACE_GPX, gpxName, currentGpxTimeString);
        currentGpxTimeString = "2000-01-01 01:00:00.000";
    }

}
