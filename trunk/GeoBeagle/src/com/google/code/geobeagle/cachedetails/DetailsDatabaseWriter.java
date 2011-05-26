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
import com.google.inject.Singleton;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

@Singleton
public class DetailsDatabaseWriter {

    private SQLiteDatabase sdDatabase;
    private final SdDatabaseOpener sdDatabaseOpener;
    private final ContentValues contentValues;

    @Inject
    DetailsDatabaseWriter(SdDatabaseOpener sdDatabaseOpener) {
        this.sdDatabaseOpener = sdDatabaseOpener;
        contentValues = new ContentValues();
    }

    public void write(String cacheId, String details) {
        contentValues.put("Details", details);
        contentValues.put("CacheId", cacheId);
        sdDatabase.replace("Details", "Details", contentValues);
    }

    public void deleteAll() {
        sdDatabaseOpener.delete();
    }

    public void start() {
        sdDatabase = sdDatabaseOpener.open();
        Log.d("GeoBeagle", "STARTING TRANSACTION");
        sdDatabase.beginTransaction();
    }

    public void end() {
        if (sdDatabase == null)
            return;
        Log.d("GeoBeagle", "DetailsDatabaseWriter::end()");
        sdDatabase.setTransactionSuccessful();
        sdDatabase.endTransaction();
        sdDatabase.close();
        sdDatabase = null;
    }
}
