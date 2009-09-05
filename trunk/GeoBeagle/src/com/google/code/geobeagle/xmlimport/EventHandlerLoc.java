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

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

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
            mCachePersisterFacade.endCache(Source.LOC);
        }
    }

    public void startTag(String mFullPath, XmlPullParserWrapper mXmlPullParser) throws IOException {
        if (mFullPath.equals(XPATH_COORD)) {
            mCachePersisterFacade.wpt(mXmlPullParser.getAttributeValue(null, "lat"), mXmlPullParser
                    .getAttributeValue(null, "lon"));
        } else if (mFullPath.equals(XPATH_WPTNAME)) {
            mCachePersisterFacade.startCache();
            mCachePersisterFacade.wptName(mXmlPullParser.getAttributeValue(null, "id"));
        }
    }

    public boolean text(String mFullPath, String text) throws IOException {
        if (mFullPath.equals(XPATH_WPTNAME))
            mCachePersisterFacade.groundspeakName(text.trim());

        return true;
    }
}
