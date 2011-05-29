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

import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;

public class EventDispatcher {
    public static class EventDispatcherFactory {
        private final XmlPathBuilder xmlPathBuilder;
        private final Provider<XmlPullParser> xmlPullParserProvider;
        private final StringWriterWrapper stringWriterWrapper;

        @Inject
        public EventDispatcherFactory(XmlPathBuilder xmlPathBuilder,
                Provider<XmlPullParser> xmlPullParserProvider,
                StringWriterWrapper stringWriterWrapper) {
            this.xmlPathBuilder = xmlPathBuilder;
            this.xmlPullParserProvider = xmlPullParserProvider;
            this.stringWriterWrapper = stringWriterWrapper;
        }

        public EventDispatcher create(EventHandler eventHandler) {
            return new EventDispatcher(xmlPathBuilder, eventHandler, xmlPullParserProvider,
                    stringWriterWrapper);
        }
    }

    public static class XmlPathBuilder {
        private String mPath = "";

        public void endTag(String currentTag) {
            mPath = mPath.substring(0, mPath.length() - (currentTag.length() + 1));
        }

        public String getPath() {
            return mPath;
        }

        public void reset() {
            mPath = "";
        }

        public void startTag(String mCurrentTag) {
            mPath += "/" + mCurrentTag;
        }
    }

    private final EventHandler eventHandler;
    private final XmlPathBuilder xmlPathBuilder;
    private XmlPullParser xmlPullParser;
    private final Provider<XmlPullParser> xmlPullParserProvider;
    private final StringWriterWrapper stringWriterWrapper;

    public EventDispatcher(XmlPathBuilder xmlPathBuilder,
            EventHandler eventHandler,
            Provider<XmlPullParser> xmlPullParserProvider,
            StringWriterWrapper stringWriterWrapper) {
        this.xmlPathBuilder = xmlPathBuilder;
        this.eventHandler = eventHandler;
        this.xmlPullParserProvider = xmlPullParserProvider;
        this.stringWriterWrapper = stringWriterWrapper;
    }

    public int getEventType() throws XmlPullParserException {
        return xmlPullParser.getEventType();
    }

    public boolean handleEvent(int eventType)
            throws IOException {
        switch (eventType) {
            case XmlPullParser.START_TAG: {
                String name = xmlPullParser.getName();
                xmlPathBuilder.startTag(name);
                eventHandler.startTag(name, xmlPathBuilder.getPath());
                break;
            }
            case XmlPullParser.END_TAG: {
                String name = xmlPullParser.getName();
                eventHandler.endTag(name, xmlPathBuilder.getPath());
                xmlPathBuilder.endTag(name);
                break;
            }
            case XmlPullParser.TEXT:
                return eventHandler.text(xmlPathBuilder.getPath(), xmlPullParser.getText());
        }
        return true;
    }

    public int next() throws XmlPullParserException, IOException {
        return xmlPullParser.next();
    }

    public void open() {
        xmlPathBuilder.reset();
        eventHandler.start(xmlPullParser);
    }

    public void setInput(Reader reader) throws XmlPullParserException {
        xmlPullParser = xmlPullParserProvider.get();
        xmlPullParser.setInput(reader);
    }

    public String getString() {
        return stringWriterWrapper.getString();
    }

    public void close() {
        eventHandler.end();
    }
}
