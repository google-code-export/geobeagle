
package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

import java.io.IOException;

interface EventHandler {

    void endTag(String previousFullPath) throws IOException;

    void startTag(String mFullPath, XmlPullParserWrapper mXmlPullParser) throws IOException;

    boolean text(String mFullPath, String text) throws IOException;
}
