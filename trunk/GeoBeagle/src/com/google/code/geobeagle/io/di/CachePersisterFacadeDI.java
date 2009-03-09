
package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.io.CachePersisterFacade;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.CacheDetailsWriter.CacheDetailsWriterFactory;
import com.google.code.geobeagle.io.Database.CacheWriter;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.io.File;

public class CachePersisterFacadeDI {

    public static class FileFactory {
        public File createFile(String path) {
            return new File(path);
        }
    }

    public static CachePersisterFacade create(Activity activity,
            GpxImporterDI.MessageHandler messageHandler, Database database,
            SQLiteWrapper sqliteWrapper) {
        final CacheWriter cacheWriter = database.createCacheWriter(sqliteWrapper);
        final HtmlWriterFactory htmlWriterFactory = new HtmlWriterFactory();
        final CachePersisterFacadeDI.FileFactory fileFactory = new CachePersisterFacadeDI.FileFactory();
        final CacheDetailsWriterFactory cacheDetailsWriterFactory = new CacheDetailsWriterFactory();
        final CachePersisterFacade.Cache cache = new CachePersisterFacade.Cache();

        final PowerManager powerManager = (PowerManager)activity
                .getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = powerManager
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Importing");
        return new CachePersisterFacade(cacheWriter, fileFactory, cacheDetailsWriterFactory, null,
                htmlWriterFactory, messageHandler, cache, wakeLock);
    }

}
