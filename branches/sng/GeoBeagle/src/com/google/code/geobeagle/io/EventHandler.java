
package com.google.code.geobeagle.io;

import com.google.code.geobeagle.io.GpxToCacheDI.XmlPullParserWrapper;

import java.io.IOException;

public interface EventHandler {

    public void endTag(String previousFullPath) throws IOException;

    public void startTag(String mFullPath, XmlPullParserWrapper mXmlPullParser);

    public boolean text(String mFullPath, String text) throws IOException;
}
