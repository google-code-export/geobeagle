
package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.io.CacheDetailsWriter;
import com.google.code.geobeagle.io.CachePersisterFacade;
import com.google.code.geobeagle.io.CacheTagWriter;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.HtmlWriter;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.di.GpxImporterDI.MessageHandler;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CachePersisterFacadeDI {

    public static class FileFactory {
        public File createFile(String path) {
            return new File(path);
        }
    }

    public static class WriterWrapper {
        Writer mWriter;

        public void close() throws IOException {
            mWriter.close();
        }

        public void open(String path) throws IOException {
            mWriter = new BufferedWriter(new FileWriter(path), 4000);
        }

        public void write(String str) throws IOException {
            mWriter.write(str);
        }
    }

    public static CachePersisterFacade create(Activity activity, MessageHandler messageHandler,
            Database database, SQLiteWrapper sqliteWrapper) {
        final CacheWriter cacheWriter = DatabaseDI.createCacheWriter(sqliteWrapper);
        final CacheTagWriter cacheTagWriter = new CacheTagWriter(cacheWriter);
        final FileFactory fileFactory = new FileFactory();
        final WriterWrapper writerWrapper = new WriterWrapper();
        final HtmlWriter htmlWriter = new HtmlWriter(writerWrapper);
        final CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter);
        final PowerManager powerManager = (PowerManager)activity
                .getSystemService(Context.POWER_SERVICE);
        final WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "Importing");
        return new CachePersisterFacade(cacheTagWriter, fileFactory, cacheDetailsWriter,
                messageHandler, wakeLock);
    }

}
