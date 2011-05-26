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
import com.google.inject.Singleton;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.util.HashMap;

@Singleton
public class XmlWriter implements EventHandler {
    private final TagWriter tagWriter;
    private Tag tagWpt;
    private String time;
    private XmlPullParser xmlPullParser;
    private static String GPX_WPT = "/gpx/wpt";
    private static String GPX_WPTTIME = "/gpx/wpt/time";
    private static String GPX_WPTNAME = "/gpx/wpt/name";
    private final HashMap<String, String> emptyHashMap;
    private boolean isWritingCache;

    @Inject
    public XmlWriter(TagWriter tagWriter) {
        this.tagWriter = tagWriter;
        emptyHashMap = new HashMap<String, String>();
        isWritingCache = false;
    }

    @Override
    public void endTag(String name, String previousFullPath) throws IOException {
        if (!previousFullPath.startsWith(GPX_WPT))
            return;

        if (isWritingCache)
            tagWriter.endTag(name);

        if (previousFullPath.equals(GPX_WPT)) {
            tagWriter.endTag("gpx");
            tagWriter.close();
            isWritingCache = false;
        }
    }

    @Override
    public void startTag(String name, String fullPath)
            throws IOException {
        if (!fullPath.startsWith(GPX_WPT))
            return;

        HashMap<String, String> attributes = new HashMap<String, String>();

        int attributeCount = xmlPullParser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            attributes.put(xmlPullParser.getAttributeName(i), xmlPullParser.getAttributeValue(i));
        }
        Tag tag = new Tag(name, attributes);

        if (fullPath.equals(GPX_WPT)) {
            tagWpt = tag;
        } else if (isWritingCache) {
            tagWriter.startTag(tag);
        }
    }

    @Override
    public boolean text(String fullPath, String text) throws IOException {
        if (!fullPath.startsWith(GPX_WPT))
            return true;

        if (text.trim().length() == 0)
            return true;

        if (fullPath.equals(GPX_WPTTIME)) {
            time = text;
        } else if (fullPath.equals(GPX_WPTNAME)) {
            tagWriter.open(text);
            tagWriter.startTag(new Tag("gpx", emptyHashMap));
            tagWriter.startTag(tagWpt);
            if (time != null) {
                tagWriter.startTag(new Tag("time", emptyHashMap));
                tagWriter.text(time);
                tagWriter.endTag("time");
            }
            tagWriter.startTag(new Tag("name", emptyHashMap));
            isWritingCache = true;
        }
        if (isWritingCache)
            tagWriter.text(text);
        return true;
    }

    @Override
    public void start(XmlPullParser xmlPullParser) {
        this.xmlPullParser = xmlPullParser;
        tagWriter.start();
    }

    @Override
    public void end() {
        tagWriter.end();
    }
}
