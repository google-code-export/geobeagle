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

import java.io.IOException;

interface EventHandler {

    void endTag(String name, String previousFullPath) throws IOException;

    void startTag(String name, String mFullPath, XmlPullParserWrapper mXmlPullParser)
            throws IOException;

    boolean text(String fullPath, String text, XmlPullParserWrapper xmlPullParser)
            throws IOException;

    void open(String filename);
}
