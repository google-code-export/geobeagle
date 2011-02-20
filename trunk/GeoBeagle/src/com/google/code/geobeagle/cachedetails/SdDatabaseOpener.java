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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

@Singleton
class SdDatabaseOpener {
    static final int DATABASE_VERSION = 6;
    private final GeoBeagleEnvironment geoBeagleEnvironment;
    private final ShowToastOnUiThread showToastOnUiThread;

    @Inject
    SdDatabaseOpener(GeoBeagleEnvironment geoBeagleEnvironment,
            ShowToastOnUiThread showToastOnUiThread) {
        this.geoBeagleEnvironment = geoBeagleEnvironment;
        this.showToastOnUiThread = showToastOnUiThread;
    }

    void delete() {
        new File(geoBeagleEnvironment.getExternalStorageDir() + "/geobeagle.db").delete();
    }

    SQLiteDatabase open() {
        SQLiteDatabase sqliteDatabase = SQLiteDatabase.openDatabase(
                geoBeagleEnvironment.getExternalStorageDir() + "/geobeagle.db", null,
                SQLiteDatabase.CREATE_IF_NECESSARY);
        int oldVersion = sqliteDatabase.getVersion();
        Log.d("GeoBeagle", "SDDatabase verson: " + oldVersion);
        if (oldVersion < DATABASE_VERSION) {
            if (oldVersion > 0)
                showToastOnUiThread.showToast(R.string.upgrading_database, Toast.LENGTH_LONG);
            if (oldVersion < 6) {
                sqliteDatabase.execSQL("DROP TABLE IF EXISTS DETAILS");
            }
            sqliteDatabase
                    .execSQL("CREATE TABLE IF NOT EXISTS Details (CacheId TEXT PRIMARY KEY, Details TEXT)");
            sqliteDatabase.setVersion(DATABASE_VERSION);
        }
        return sqliteDatabase;
    }

}
