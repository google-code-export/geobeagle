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
    }

    private final XmlPathBuilder mXmlPathBuilder;

    @Inject
    public EventHelper(XmlPathBuilder xmlPathBuilder) {
        mXmlPathBuilder = xmlPathBuilder;
    }

    public boolean handleEvent(int eventType,
            EventHandler eventHandler,
            XmlPullParser mXmlPullParser) throws IOException {
        switch (eventType) {
            case XmlPullParser.START_TAG: {
                final String name = mXmlPullParser.getName();
                mXmlPathBuilder.startTag(name);
                eventHandler.startTag(name, mXmlPathBuilder.getPath());
                break;
            }
            case XmlPullParser.END_TAG: {
                final String name = mXmlPullParser.getName();
                eventHandler.endTag(name, mXmlPathBuilder.getPath());
                mXmlPathBuilder.endTag(name);
                break;
            }
            case XmlPullParser.TEXT:
                return eventHandler.text(mXmlPathBuilder.getPath(), mXmlPullParser.getText());
        }
        return true;
    }

    public void open(String filename, EventHandler eventHandler) throws IOException {
        eventHandler.open(filename);
    }

}
