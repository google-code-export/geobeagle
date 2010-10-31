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

public class EventHelper {
    public static class EventHelperFactory {
        private final XmlPathBuilder xmlPathBuilder;

        @Inject
        public EventHelperFactory(XmlPathBuilder xmlPathBuilder) {
            this.xmlPathBuilder = xmlPathBuilder;
        }

        public EventHelper create(EventHandler eventHandler) {
            return new EventHelper(xmlPathBuilder, eventHandler);
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

        public void startTag(String mCurrentTag) {
            mPath += "/" + mCurrentTag;
        }

        public void reset() {
            mPath = "";
        }
    }

    private final XmlPathBuilder xmlPathBuilder;
    private final EventHandler eventHandler;
    private XmlPullParser xmlPullParser;

    public EventHelper(XmlPathBuilder xmlPathBuilder, EventHandler eventHandler) {
        this.xmlPathBuilder = xmlPathBuilder;
        this.eventHandler = eventHandler;
    }

    public boolean handleEvent(int eventType) throws IOException {
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

    public void open(XmlPullParser xmlPullParser) {
        this.xmlPullParser = xmlPullParser;
        xmlPathBuilder.reset();
        eventHandler.start(xmlPullParser);
    }
}
