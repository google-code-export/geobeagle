
package com.google.code.geobeagle.cachedetails;

import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class DetailsDatabaseWriter implements Writer {

    private final GeoBeagleEnvironment geoBeagleEnvironment;
    private final StringBuffer stringBuffer;
    private SQLiteDatabase sdDatabase;
    private String cacheId;

    @Inject
    DetailsDatabaseWriter(GeoBeagleEnvironment geoBeagleEnvironment) {
        this.geoBeagleEnvironment = geoBeagleEnvironment;
        stringBuffer = new StringBuffer();
    }

    @Override
    public void close() throws IOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Details", stringBuffer.toString());
        contentValues.put("CacheId", cacheId);
        Log.d("GeoBeagle", "INSERTING Details: " + cacheId);
        sdDatabase.insert("Details", "Details", contentValues);
    }

    @Override
    public void open(String path, String cacheId) throws IOException {
        this.cacheId = cacheId;
        if (sdDatabase != null)
            return;
        sdDatabase = SQLiteDatabase.openDatabase(geoBeagleEnvironment.getExternalStorageDir()
                + "/geobeagle.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        sdDatabase.execSQL("CREATE TABLE IF NOT EXISTS Details (CacheId TEXT, Details TEXT)");
    }

    @Override
    public void write(String str) throws IOException {
        stringBuffer.append(str);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void mkdirs(String path) {

    }

}
