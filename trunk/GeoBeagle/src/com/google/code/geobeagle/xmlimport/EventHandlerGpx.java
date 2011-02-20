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


import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

public class EventHandlerGpx implements EventHandler {

    private final CacheXmlTagHandler cacheXmlTagHandler;
    private XmlPullParser xmlPullParser;

    public EventHandlerGpx(CacheXmlTagHandler cacheXmlTagHandler) {
        this.cacheXmlTagHandler = cacheXmlTagHandler;
    }

    @Override
    public void endTag(String name, String previousFullPath)
            throws IOException {
        GpxPath.fromString(previousFullPath).endTag(cacheXmlTagHandler);
    }

    @Override
    public void startTag(String name, String fullPath)
            throws IOException {
        GpxPath.fromString(fullPath).startTag(xmlPullParser, cacheXmlTagHandler);
    }

    @Override
    public boolean text(String fullPath, String text) throws IOException {
        return GpxPath.fromString(fullPath).text(text, cacheXmlTagHandler);
    }

    @Override
    public void start(XmlPullParser xmlPullParser) {
        this.xmlPullParser = xmlPullParser;
    }

    @Override
    public void end() {
    }

}
