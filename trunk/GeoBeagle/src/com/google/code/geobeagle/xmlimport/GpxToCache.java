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

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.xmlimport.CacheXmlTagsToSql.CacheXmlTagsToSqlFactory;
import com.google.code.geobeagle.xmlimport.EventDispatcher.EventDispatcherFactory;
import com.google.code.geobeagle.xmlimport.EventHandlerSqlAndFileWriter.EventHandlerSqlAndFileWriterFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

public class GpxToCache {
    @SuppressWarnings("serial")
    public static class CancelException extends Exception {
    }

    public static class GpxToCacheFactory {
        private final Aborter aborter;
        private final EventDispatcherFactory eventDispatcherFactory;
        private final EventHandlerSqlAndFileWriterFactory eventHandlerSqlAndFileWriterFactory;
        private final FileAlreadyLoadedChecker fileAlreadyLoadedChecker;
        private final XmlWriter xmlWriter;
        private final Provider<ImportWakeLock> importWakeLockProvider;
        private final ErrorDisplayer errorDisplayer;
        private final CacheXmlTagsToSqlFactory cacheXmlTagsToSqlFactory;

        @Inject
        public GpxToCacheFactory(Aborter aborter,
                FileAlreadyLoadedChecker fileAlreadyLoadedChecker,
                XmlWriter xmlWriter,
                EventDispatcherFactory eventHelperFactory,
                EventHandlerSqlAndFileWriterFactory eventHandlerSqlAndFileWriterFactory,
                Provider<ImportWakeLock> importWakeLockProvider,
                ErrorDisplayer errorDisplayer,
                CacheXmlTagsToSqlFactory cacheXmlTagsToSqlFactory) {
            this.aborter = aborter;
            this.fileAlreadyLoadedChecker = fileAlreadyLoadedChecker;
            this.xmlWriter = xmlWriter;
            this.eventDispatcherFactory = eventHelperFactory;
            this.eventHandlerSqlAndFileWriterFactory = eventHandlerSqlAndFileWriterFactory;
            this.importWakeLockProvider = importWakeLockProvider;
            this.errorDisplayer = errorDisplayer;
            this.cacheXmlTagsToSqlFactory = cacheXmlTagsToSqlFactory;
        }

        public GpxToCache create(MessageHandlerInterface messageHandler) {
            CacheXmlTagsToSql cacheXmlTagsToSql = cacheXmlTagsToSqlFactory.create(messageHandler);

            EventHandlerSqlAndFileWriter eventHandlerSqlAndFileWriter = eventHandlerSqlAndFileWriterFactory
                    .create(cacheXmlTagsToSql);
            return new GpxToCache(aborter, fileAlreadyLoadedChecker,
                    eventDispatcherFactory.create(eventHandlerSqlAndFileWriter), xmlWriter,
                    cacheXmlTagsToSql, importWakeLockProvider, errorDisplayer);
        }
    }

    private final Aborter aborter;
    private final CacheXmlTagsToSql cacheXmlTagsToSql;
    private final EventDispatcher eventDispatcher;
    private final FileAlreadyLoadedChecker fileAlreadyLoadedChecker;
    private final XmlWriter xmlWriter;
    private final Provider<ImportWakeLock> importWakeLockProvider;
    public static final int WAKELOCK_DURATION = 15000;
    private final ErrorDisplayer errorDisplayer;

    GpxToCache(Aborter aborter,
            FileAlreadyLoadedChecker fileAlreadyLoadedChecker,
            EventDispatcher eventDispatcher,
            XmlWriter xmlWriter,
            CacheXmlTagsToSql cacheXmlTagsToSql,
            Provider<ImportWakeLock> importWakeLockProvider,
            ErrorDisplayer errorDisplayer) {
        this.aborter = aborter;
        this.fileAlreadyLoadedChecker = fileAlreadyLoadedChecker;
        this.eventDispatcher = eventDispatcher;
        this.xmlWriter = xmlWriter;
        this.cacheXmlTagsToSql = cacheXmlTagsToSql;
        this.importWakeLockProvider = importWakeLockProvider;
        this.errorDisplayer = errorDisplayer;
    }

    public void end() {
        cacheXmlTagsToSql.end();
    }

    public void load(String path, Reader reader) throws CancelException {
        try {
            String filename = new File(path).getName();
            loadFile(path, filename, reader);
            importWakeLockProvider.get().acquire(WAKELOCK_DURATION);
            return;
        } catch (SQLiteException e) {
            errorDisplayer.displayError(R.string.error_writing_cache, path + ": " + e.getMessage());
        } catch (XmlPullParserException e) {
            errorDisplayer.displayError(R.string.error_parsing_file, path + ": " + e.getMessage());
        } catch (FileNotFoundException e) {
            errorDisplayer.displayError(R.string.file_not_found, path + ": " + e.getMessage());
        } catch (IOException e) {
            errorDisplayer.displayError(R.string.error_reading_file, path + ": " + e.getMessage());
        } catch (CancelException e) {
        }

        throw new CancelException();
    }

    private void loadFile(String source, String filename, Reader reader)
            throws XmlPullParserException,
            IOException, CancelException {
        eventDispatcher.setInput(reader);

        // Just use the filename, not the whole path.
        cacheXmlTagsToSql.open(filename);
        boolean markAsComplete = false;
        try {
            Log.d("GeoBeagle", this + ": GpxToCache: load");
            if (fileAlreadyLoadedChecker.isAlreadyLoaded(source)) {
                return;
            }

            xmlWriter.open(filename);
            eventDispatcher.open();
            int eventType;
            for (eventType = eventDispatcher.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = eventDispatcher
                    .next()) {
                // Log.d("GeoBeagle", "event: " + eventType);
                if (aborter.isAborted()) {
                    Log.d("GeoBeagle", "GpxToCache isAborted: " + aborter.isAborted());
                    throw new CancelException();
                }
                // File already loaded.
                if (!eventDispatcher.handleEvent(eventType)) {
                    return;
                }
            }

            // Pick up END_DOCUMENT event as well.
            eventDispatcher.handleEvent(eventType);
            markAsComplete = true;
        } finally {
            cacheXmlTagsToSql.close(markAsComplete);
        }
    }

    public void start() {
        cacheXmlTagsToSql.start();
    }

}
