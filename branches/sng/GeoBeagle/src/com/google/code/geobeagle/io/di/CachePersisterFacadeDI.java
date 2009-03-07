package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.io.CachePersisterFacade;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.CacheDetailsWriter.CacheDetailsWriterFactory;
import com.google.code.geobeagle.io.Database.CacheWriter;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.io.HtmlWriter.HtmlWriterFactory;

import java.io.File;

public class CachePersisterFacadeDI {

    public static class FileFactory {
        public File createFile(String path) {
            return new File(path);
        }
    }

    public static CachePersisterFacade create(GpxImporterDI.MessageHandler messageHandler, Database database,
            SQLiteWrapper sqliteWrapper) {
        final CacheWriter cacheWriter = database.createCacheWriter(sqliteWrapper);
        final HtmlWriterFactory htmlWriterFactory = new HtmlWriterFactory();
        final CachePersisterFacadeDI.FileFactory fileFactory = new CachePersisterFacadeDI.FileFactory();
        final CacheDetailsWriterFactory cacheDetailsWriterFactory = new CacheDetailsWriterFactory();
        final CachePersisterFacade.Cache cache = new CachePersisterFacade.Cache();
    
        return new CachePersisterFacade(cacheWriter, fileFactory, cacheDetailsWriterFactory, null,
                htmlWriterFactory, messageHandler, cache);
    }

}
