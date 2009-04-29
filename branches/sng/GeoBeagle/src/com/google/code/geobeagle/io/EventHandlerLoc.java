
package com.google.code.geobeagle.io;

import com.google.code.geobeagle.data.GeocacheFactory.Source;
import com.google.code.geobeagle.io.GpxToCacheDI.XmlPullParserWrapper;

import java.io.IOException;

class EventHandlerLoc implements EventHandler {

    static final String XPATH_COORD = "/loc/waypoint/coord";
    static final String XPATH_GROUNDSPEAKNAME = "/loc/waypoint/name";
    static final String XPATH_LOC = "/loc";
    static final String XPATH_WPT = "/loc/waypoint";
    static final String XPATH_WPTNAME = "/loc/waypoint/name";

    private final CachePersisterFacade mCachePersisterFacade;

    EventHandlerLoc(CachePersisterFacade cachePersisterFacade) {
        mCachePersisterFacade = cachePersisterFacade;
    }

    public void endTag(String previousFullPath) throws IOException {
        if (previousFullPath.equals(XPATH_WPT)) {
            mCachePersisterFacade.endTag(Source.LOC);
        }
    }

    public void startTag(String mFullPath, XmlPullParserWrapper mXmlPullParser) throws IOException {
        if (mFullPath.equals(XPATH_COORD)) {
            mCachePersisterFacade.wpt(mXmlPullParser.getAttributeValue(null, "lat"), mXmlPullParser
                    .getAttributeValue(null, "lon"));
        } else if (mFullPath.equals(XPATH_WPTNAME)) {
            mCachePersisterFacade.newCache();
            mCachePersisterFacade.wptName(mXmlPullParser.getAttributeValue(null, "id"));
        }
    }

    public boolean text(String mFullPath, String text) throws IOException {
        if (mFullPath.equals(XPATH_LOC))
            return mCachePersisterFacade.gpxTime("2000-01-01T12:00:00");
        if (mFullPath.equals(XPATH_WPTNAME))
            mCachePersisterFacade.groundspeakName(text.trim());

        return true;
    }
}
