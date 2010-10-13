
package com.google.code.geobeagle.cachedetails;

import com.google.inject.Inject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class DetailsDatabaseWriter implements Writer, CacheWriterOpener {

    private final StringBuffer stringBuffer;
    private SQLiteDatabase sdDatabase;
    private String cacheId;
    private final SdDatabaseOpener sdDatabaseOpener;

    @Inject
    DetailsDatabaseWriter(SdDatabaseOpener sdDatabaseOpener) {
        this.sdDatabaseOpener = sdDatabaseOpener;
        stringBuffer = new StringBuffer();
    }

    @Override
    public void close() throws IOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Details", stringBuffer.toString());
        contentValues.put("CacheId", cacheId);
        Log.d("GeoBeagle", "INSERTING Details: " + cacheId);
        sdDatabase.insert("Details", "Details", contentValues);
        stringBuffer.setLength(0);
    }

    @Override
    public void open(String path, String cacheId) throws IOException {
        this.cacheId = cacheId;
        if (sdDatabase != null)
            return;

        sdDatabase = sdDatabaseOpener.open();
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
