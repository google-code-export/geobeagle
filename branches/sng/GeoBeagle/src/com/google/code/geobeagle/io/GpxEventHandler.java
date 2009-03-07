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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.io.di.GpxToCacheDI;

import java.io.IOException;

public class GpxEventHandler {
    public static final String XPATH_GROUNDSPEAKNAME = "/gpx/wpt/groundspeak:cache/groundspeak:name";
    public static final String XPATH_HINT = "/gpx/wpt/groundspeak:cache/groundspeak:encoded_hints";
    public static final String XPATH_LOGDATE = "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:date";
    public static final String[] XPATH_PLAINLINES = {
            "/gpx/wpt/desc", "/gpx/wpt/groundspeak:cache/groundspeak:type",
            "/gpx/wpt/groundspeak:cache/groundspeak:container",
            "/gpx/wpt/groundspeak:cache/groundspeak:short_description",
            "/gpx/wpt/groundspeak:cache/groundspeak:long_description",
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:type",
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:finder",
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:text"
    };
    public static final String XPATH_WPT = "/gpx/wpt";
    public static final String XPATH_WPTNAME = "/gpx/wpt/name";

    private final CachePersisterFacade mCachePersisterFacade;

    public GpxEventHandler(CachePersisterFacade cachePersisterFacade) {
        mCachePersisterFacade = cachePersisterFacade;
    }

    public void endTag(String previousFullPath) throws IOException {
        if (previousFullPath.equals(XPATH_WPT)) {
            mCachePersisterFacade.endTag();
        }
    }

    public void startTag(String mFullPath, GpxToCacheDI.XmlPullParserWrapper mXmlPullParser) {
        if (mFullPath.equals(XPATH_WPT)) {
            mCachePersisterFacade.wpt(mXmlPullParser);
        }
    }

    public void text(String mFullPath, String text) throws IOException {
        text = text.trim();
        if (mFullPath.equals(XPATH_WPTNAME)) {
            mCachePersisterFacade.wptName(text);
        } else if (mFullPath.equals(XPATH_GROUNDSPEAKNAME)) {
            mCachePersisterFacade.groundspeakName(text);
        } else if (mFullPath.equals(XPATH_LOGDATE)) {
            mCachePersisterFacade.logDate(text);
        } else if (mFullPath.equals(XPATH_HINT)) {
            if (!text.equals("")) {
                mCachePersisterFacade.hint(text);
            }
        } else {
            for (String writeLineMatch : XPATH_PLAINLINES) {
                if (mFullPath.equals(writeLineMatch)) {
                    mCachePersisterFacade.line(text);
                    return;
                }
            }
        }
    }
}
