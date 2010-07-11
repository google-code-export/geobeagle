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

import android.util.Log;

import java.io.IOException;

public class EventHandlerGpx implements EventHandler {
    static final String XPATH_CACHE = "/gpx/wpt/groundspeak:cache";
    static final String XPATH_CACHE_CONTAINER = "/gpx/wpt/groundspeak:cache/groundspeak:container";
    static final String XPATH_CACHE_DIFFICULTY = "/gpx/wpt/groundspeak:cache/groundspeak:difficulty";
    static final String XPATH_CACHE_TERRAIN = "/gpx/wpt/groundspeak:cache/groundspeak:terrain";
    static final String XPATH_GEOCACHE_CONTAINER = "/gpx/wpt/geocache/container";
    static final String XPATH_GEOCACHE_DIFFICULTY = "/gpx/wpt/geocache/difficulty";
    static final String XPATH_GEOCACHE_TERRAIN = "/gpx/wpt/geocache/terrain";
    static final String XPATH_GEOCACHE_TYPE = "/gpx/wpt/geocache/type";
    static final String XPATH_GEOCACHEHINT = "/gpx/wpt/geocache/hints";
    static final String XPATH_GEOCACHELOGDATE = "/gpx/wpt/geocache/logs/log/time";
    static final String XPATH_GEOCACHENAME = "/gpx/wpt/geocache/name";
    static final String XPATH_GPXNAME = "/gpx/name";
    static final String XPATH_GPXTIME = "/gpx/time";
    static final String XPATH_WPTTIME = "/gpx/wpt/time";
    static final String XPATH_TERRACACHINGGPXTIME = "/gpx/metadata/time";
    static final String XPATH_GROUNDSPEAKNAME = "/gpx/wpt/groundspeak:cache/groundspeak:name";
    static final String XPATH_PLACEDBY = "/gpx/wpt/groundspeak:cache/groundspeak:placed_by";
    static final String XPATH_HINT = "/gpx/wpt/groundspeak:cache/groundspeak:encoded_hints";
    static final String XPATH_LOGDATE = "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:date";
    static final String XPATH_LOGTEXT = "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:text";
    static final String XPATH_LOGTYPE = "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:type";
    static final String XPATH_SHORTDESC = "/gpx/wpt/groundspeak:cache/groundspeak:short_description";
    static final String XPATH_LONGDESC = "/gpx/wpt/groundspeak:cache/groundspeak:long_description";
    
    static final String[] XPATH_PLAINLINES = {
            "/gpx/wpt/cmt",
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:finder",
            /* here are the geocaching.com.au entries */
            "/gpx/wpt/geocache/owner", "/gpx/wpt/geocache/type", "/gpx/wpt/geocache/summary",
            "/gpx/wpt/geocache/description", "/gpx/wpt/geocache/logs/log/geocacher",
            "/gpx/wpt/geocache/logs/log/type", "/gpx/wpt/geocache/logs/log/text"

    };
    static final String XPATH_LAST_MODIFIED = "/gpx/wpt/bcaching:cache/bcaching:lastModified";
    static final String XPATH_SYM = "/gpx/wpt/sym";
    static final String XPATH_WPT = "/gpx/wpt";
    static final String XPATH_WPTDESC = "/gpx/wpt/desc";
    static final String XPATH_WPTNAME = "/gpx/wpt/name";
    static final String XPATH_WAYPOINT_TYPE = "/gpx/wpt/type";
    
    private boolean mLogEncrypted;
    private String mGpxTime;

    @Override
    public void endTag(String name, String previousFullPath,
            ICachePersisterFacade cachePersisterFacade) throws IOException {
        if (previousFullPath.equals(XPATH_WPT)) {
            cachePersisterFacade.endCache(Source.GPX);
        }
    }

    @Override
    public void startTag(String name, String fullPath, XmlPullParserWrapper xmlPullParser,
            ICachePersisterFacade cachePersisterFacade) {
        if (fullPath.equals(XPATH_WPT)) {
            cachePersisterFacade.startCache();
            cachePersisterFacade.wpt(xmlPullParser.getAttributeValue(null, "lat"), xmlPullParser
                    .getAttributeValue(null, "lon"));
        } else if (fullPath.equals(XPATH_CACHE)) {
            cachePersisterFacade.available(xmlPullParser.getAttributeValue(null, "available"));
            cachePersisterFacade.archived(xmlPullParser.getAttributeValue(null, "archived"));
        } else if (fullPath.equals(XPATH_LOGTEXT)) {
            mLogEncrypted = "true".equalsIgnoreCase(xmlPullParser
                    .getAttributeValue(null, "encoded"));
        }
    }

    @Override
    public boolean text(String fullPath, String text, XmlPullParserWrapper xmlPullParser,
            ICachePersisterFacade cachePersisterFacade) throws IOException {
        String trimmedText = text.trim();
        // Log.d("GeoBeagle", "fullPath " + fullPath + ", text " + text);
        if (fullPath.equals(XPATH_WPTNAME)) {
            cachePersisterFacade.wptName(trimmedText);
        } else if (fullPath.equals(XPATH_WPTDESC)) {
            cachePersisterFacade.wptDesc(trimmedText);
        } else if (fullPath.equals(XPATH_GPXTIME) || fullPath.equals(XPATH_TERRACACHINGGPXTIME)) {
            return cachePersisterFacade.gpxTime(trimmedText);
        } else if (fullPath.equals(XPATH_GROUNDSPEAKNAME) || fullPath.equals(XPATH_GEOCACHENAME)) {
            cachePersisterFacade.groundspeakName(trimmedText);
        } else if (fullPath.equals(XPATH_LOGDATE) || fullPath.equals(XPATH_GEOCACHELOGDATE)) {
            cachePersisterFacade.logDate(trimmedText);
        } else if (fullPath.equals(XPATH_SYM)) {
            cachePersisterFacade.symbol(trimmedText);
        } else if (fullPath.equals(XPATH_HINT) || fullPath.equals(XPATH_GEOCACHEHINT)) {
            if (!trimmedText.equals("")) {
                cachePersisterFacade.hint(trimmedText);
            }
        } else if (fullPath.equals(XPATH_GEOCACHE_TYPE) || fullPath.equals(XPATH_WAYPOINT_TYPE)) {
            cachePersisterFacade.cacheType(trimmedText);
        } else if (fullPath.equals(XPATH_CACHE_DIFFICULTY)
                || fullPath.equals(XPATH_GEOCACHE_DIFFICULTY)) {
            cachePersisterFacade.difficulty(trimmedText);
        } else if (fullPath.equals(XPATH_CACHE_TERRAIN) || fullPath.equals(XPATH_GEOCACHE_TERRAIN)) {
            cachePersisterFacade.terrain(trimmedText);
        } else if (fullPath.equals(XPATH_CACHE_CONTAINER)
                || fullPath.equals(XPATH_GEOCACHE_CONTAINER)) {
            cachePersisterFacade.container(trimmedText);
        } else if (fullPath.equals(XPATH_LAST_MODIFIED)) {
            cachePersisterFacade.lastModified(trimmedText);
        } else if (fullPath.equals(XPATH_LOGTEXT)) {
            cachePersisterFacade.logText(trimmedText, mLogEncrypted);
        } else if (fullPath.equals(XPATH_LOGTYPE)) {
            cachePersisterFacade.logType(trimmedText);
        } else if (fullPath.equals(XPATH_WPTTIME)) {
            mGpxTime = trimmedText;
            cachePersisterFacade.wptTime(trimmedText);
        } else if (fullPath.equals(XPATH_PLACEDBY)) {
            Log.d("GeoBeagle", "PB: " + trimmedText + ", " + mGpxTime);
            cachePersisterFacade.placedBy(trimmedText);
        } else if (fullPath.equals(XPATH_SHORTDESC)) {
            cachePersisterFacade.shortDescription(trimmedText);
        } else if (fullPath.equals(XPATH_LONGDESC)) {
            cachePersisterFacade.longDescription(trimmedText);
        }
        
        for (String writeLineMatch : XPATH_PLAINLINES) {
            if (fullPath.equals(writeLineMatch)) {
                cachePersisterFacade.line(trimmedText);
                return true;
            }
        }
        return true;
    }
    
    @Override
    public void open(String filename) throws IOException {
    }
}
