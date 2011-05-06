
package com.google.code.geobeagle.cachedetails;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

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

    SQLiteDatabase open() {
        SQLiteDatabase sqliteDatabase = SQLiteDatabase.openDatabase(
                geoBeagleEnvironment.getExternalStorageDir() + "/geobeagle.db", null,
                SQLiteDatabase.CREATE_IF_NECESSARY);
        int oldVersion = sqliteDatabase.getVersion();
        Log.d("GeoBeagle", "SDDatabase verson: " + oldVersion);
        if (oldVersion < DATABASE_VERSION) {
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
