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

    @Override
    public void endTag(String name, String previousFullPath, CacheTagHandler cacheTagHandler)
            throws IOException {
        GpxPath.fromString(previousFullPath).endTag(cacheTagHandler);
    }

    @Override
    public void startTag(String name,
            String fullPath,
            XmlPullParser xmlPullParser,
            CacheTagHandler cacheTagHandler) throws IOException {
        GpxPath.fromString(fullPath).startTag(xmlPullParser, cacheTagHandler);
    }

    @Override
    public boolean text(String fullPath,
            String text,
            XmlPullParser xmlPullParser,
            CacheTagHandler cacheTagHandler) throws IOException {
        return GpxPath.fromString(fullPath).text(text, cacheTagHandler);
    }

    @Override
    public void open(String filename) throws IOException {
    }
}
