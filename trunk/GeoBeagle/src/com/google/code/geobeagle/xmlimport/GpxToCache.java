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

import com.google.code.geobeagle.xmlimport.EventDispatcher.EventDispatcherFactory;
import com.google.code.geobeagle.xmlimport.EventHandlerSqlAndFileWriter.EventHandlerSqlAndFileWriterFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

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

        public GpxToCacheFactory(Aborter aborter,
                FileAlreadyLoadedChecker fileAlreadyLoadedChecker,
                XmlWriter xmlWriter,
                EventDispatcherFactory eventHelperFactory,
                EventHandlerSqlAndFileWriterFactory eventHandlerSqlAndFileWriterFactory) {
            this.aborter = aborter;
            this.fileAlreadyLoadedChecker = fileAlreadyLoadedChecker;
            this.xmlWriter = xmlWriter;
            this.eventDispatcherFactory = eventHelperFactory;
            this.eventHandlerSqlAndFileWriterFactory = eventHandlerSqlAndFileWriterFactory;
        }

        public GpxToCache create(CacheXmlTagsToSql cacheXmlTagsToSql) {
            EventHandlerSqlAndFileWriter eventHandlerSqlAndFileWriter = eventHandlerSqlAndFileWriterFactory
                    .create(cacheXmlTagsToSql);
            return new GpxToCache(aborter, fileAlreadyLoadedChecker,
                    eventDispatcherFactory.create(eventHandlerSqlAndFileWriter), xmlWriter,
                    cacheXmlTagsToSql);
        }
    }

    private final Aborter aborter;
    private final CacheXmlTagsToSql cacheXmlTagsToSql;
    private final EventDispatcher eventDispatcher;
    private final FileAlreadyLoadedChecker fileAlreadyLoadedChecker;
    private final XmlWriter xmlWriter;

    GpxToCache(Aborter aborter,
            FileAlreadyLoadedChecker fileAlreadyLoadedChecker,
            EventDispatcher eventDispatcher,
            XmlWriter xmlWriter,
            CacheXmlTagsToSql cacheXmlTagsToSql) {
        this.aborter = aborter;
        this.fileAlreadyLoadedChecker = fileAlreadyLoadedChecker;
        this.eventDispatcher = eventDispatcher;
        this.xmlWriter = xmlWriter;
        this.cacheXmlTagsToSql = cacheXmlTagsToSql;
    }

    public void end() {
        cacheXmlTagsToSql.end();
    }

    public void load(String source, String filename, Reader reader) throws XmlPullParserException,
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
                    Log.d("GeoBeagle", "isAborted: " + aborter.isAborted());
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
