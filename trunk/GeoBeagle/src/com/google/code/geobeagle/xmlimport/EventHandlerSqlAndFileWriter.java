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

import com.google.inject.Inject;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EventHandlerSqlAndFileWriter implements EventHandler {
    static class EventHandlerSqlAndFileWriterFactory {
        private final XmlWriter xmlWriter;

        @Inject
        public EventHandlerSqlAndFileWriterFactory(XmlWriter xmlWriter) {
            this.xmlWriter = xmlWriter;
        }

        public EventHandlerSqlAndFileWriter create(CacheXmlTagsToSql cacheXmlTagsToSql) {
            return new EventHandlerSqlAndFileWriter(xmlWriter, cacheXmlTagsToSql);
        }
    }

    private final List<EventHandler> eventHandlers;

    public EventHandlerSqlAndFileWriter(XmlWriter xmlWriter, CacheXmlTagsToSql cacheXmlTagsToSql) {
        this.eventHandlers = Arrays.asList(xmlWriter, new EventHandlerGpx(cacheXmlTagsToSql));
    }

    @Override
    public void endTag(String name, String previousFullPath) throws IOException {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.endTag(name, previousFullPath);
        }
    }

    @Override
    public void startTag(String name, String fullPath) throws IOException {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.startTag(name, fullPath);
        }
    }

    @Override
    public boolean text(String fullPath, String text) throws IOException {
        boolean ret = true;
        for (EventHandler eventHandler : eventHandlers) {
            ret &= eventHandler.text(fullPath, text);
        }
        return ret;
    }

    @Override
    public void start(XmlPullParser xmlPullParser) {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.start(xmlPullParser);
        }
    }

    @Override
    public void end() {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.end();
        }

    }
}
