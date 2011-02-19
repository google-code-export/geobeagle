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

    private final StringBuffer stringBuffer;
    private SQLiteDatabase sdDatabase;
    private String cacheId;
    private final SdDatabaseOpener sdDatabaseOpener;

    @Inject
    DetailsDatabaseWriter(SdDatabaseOpener sdDatabaseOpener) {
        this.sdDatabaseOpener = sdDatabaseOpener;
        stringBuffer = new StringBuffer();
    }

    public void close() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Details", stringBuffer.toString());
        contentValues.put("CacheId", cacheId);
        // Log.d("GeoBeagle",
        // "INSERTING Details: " + cacheId + "\n" +
        // contentValues.getAsString("Details"));
        sdDatabase.replace("Details", "Details", contentValues);
        stringBuffer.setLength(0);
        this.cacheId = null;
    }

    public void deleteAll() {
        if (sdDatabase != null)
            return;

        sdDatabase = sdDatabaseOpener.open();
        Log.d("GeoBeagle", "deleting details");
        sdDatabase.delete("Details", null, null);
        Log.d("GeoBeagle", "DONE deleting details");
    }

    public void open(String cacheId) {
        this.cacheId = cacheId;
        if (sdDatabase != null)
            return;

        sdDatabase = sdDatabaseOpener.open();
    }

    public boolean isOpen() {
        return this.cacheId != null;
    }

    public void write(String str) {
        if (cacheId != null)
            stringBuffer.append(str);
    }
}
