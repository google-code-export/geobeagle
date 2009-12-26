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

import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.xmlimport.CachePersisterFacade.TextHandler;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

import java.io.IOException;
import java.util.HashMap;

public class EventHandlerGpx implements EventHandler {
    public static final String XPATH_CACHE_CONTAINER = "/gpx/wpt/groundspeak:cache/groundspeak:container";
    public static final String XPATH_CACHE_DIFFICULTY = "/gpx/wpt/groundspeak:cache/groundspeak:difficulty";
    public static final String XPATH_CACHE_TERRAIN = "/gpx/wpt/groundspeak:cache/groundspeak:terrain";
    public static final String XPATH_CACHE_TYPE = "/gpx/wpt/groundspeak:cache/groundspeak:type";
    public static final String XPATH_GEOCACHE_CONTAINER = "/gpx/wpt/geocache/container";
    public static final String XPATH_GEOCACHE_DIFFICULTY = "/gpx/wpt/geocache/difficulty";
    public static final String XPATH_GEOCACHE_TERRAIN = "/gpx/wpt/geocache/terrain";
    public static final String XPATH_GEOCACHE_TYPE = "/gpx/wpt/geocache/type";
    public static final String XPATH_GEOCACHEHINT = "/gpx/wpt/geocache/hints";
    public static final String XPATH_GEOCACHELOGDATE = "/gpx/wpt/geocache/logs/log/time";
    public static final String XPATH_GEOCACHENAME = "/gpx/wpt/geocache/name";
    static final String XPATH_GPXNAME = "/gpx/name";
    static final String XPATH_GPXTIME = "/gpx/time";
    public static final String XPATH_GROUNDSPEAKNAME = "/gpx/wpt/groundspeak:cache/groundspeak:name";
    public static final String XPATH_PLACEDBY = "/gpx/wpt/groundspeak:cache/groundspeak:placed_by";
    public static final String XPATH_HINT = "/gpx/wpt/groundspeak:cache/groundspeak:encoded_hints";
    public static final String XPATH_LOGDATE = "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:date";
    static final String[] XPATH_PLAINLINES = {
            "/gpx/wpt/cmt", "/gpx/wpt/desc", "/gpx/wpt/groundspeak:cache/groundspeak:type",
            "/gpx/wpt/groundspeak:cache/groundspeak:container",
            "/gpx/wpt/groundspeak:cache/groundspeak:short_description",
            "/gpx/wpt/groundspeak:cache/groundspeak:long_description",
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:type",
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:finder",
            "/gpx/wpt/groundspeak:cache/groundspeak:logs/groundspeak:log/groundspeak:text",
            /* here are the geocaching.com.au entries */
            "/gpx/wpt/geocache/owner", "/gpx/wpt/geocache/type", "/gpx/wpt/geocache/summary",
            "/gpx/wpt/geocache/description", "/gpx/wpt/geocache/logs/log/geocacher",
            "/gpx/wpt/geocache/logs/log/type", "/gpx/wpt/geocache/logs/log/text"

    };
    static final String XPATH_SYM = "/gpx/wpt/sym";
    static final String XPATH_CACHE = "/gpx/wpt/groundspeak:cache";
    static final String XPATH_WPT = "/gpx/wpt";
    public static final String XPATH_WPTDESC = "/gpx/wpt/desc";
    public static final String XPATH_WPTNAME = "/gpx/wpt/name";
    public static final String XPATH_WAYPOINT_TYPE = "/gpx/wpt/type";
    
    private final CachePersisterFacade mCachePersisterFacade;
    private final HashMap<String, TextHandler> mTextHandlers;

    public EventHandlerGpx(CachePersisterFacade cachePersisterFacade, HashMap<String, TextHandler> textHandlers) {
        mCachePersisterFacade = cachePersisterFacade;
        mTextHandlers = textHandlers;
    }

    public void endTag(String previousFullPath) throws IOException {
        if (previousFullPath.equals(XPATH_WPT)) {
            mCachePersisterFacade.endCache(Source.GPX);
        }
    }

    public void startTag(String fullPath, XmlPullParserWrapper xmlPullParser) {
        if (fullPath.equals(XPATH_WPT)) {
            mCachePersisterFacade.startCache();
            mCachePersisterFacade.wpt(xmlPullParser.getAttributeValue(null, "lat"), xmlPullParser
                    .getAttributeValue(null, "lon"));
        } else if (fullPath.equals(XPATH_CACHE)) {
            boolean available = xmlPullParser.getAttributeValue(null, "available").equalsIgnoreCase("true");
            boolean archived = xmlPullParser.getAttributeValue(null, "archived").equalsIgnoreCase("true");
            mCachePersisterFacade.setTag(Tags.UNAVAILABLE, !available);
            mCachePersisterFacade.setTag(Tags.ARCHIVED, archived);
        }
    }

    public boolean text(String fullPath, String text) throws IOException {
        text = text.trim();
        
        if (mTextHandlers.containsKey(fullPath))
            mTextHandlers.get(fullPath).text(text);
        
        //Log.d("GeoBeagle", "fullPath " + fullPath + ", text " + text);
        if (fullPath.equals(XPATH_GPXTIME)) {
            return mCachePersisterFacade.gpxTime(text);
        } else if (fullPath.equals(XPATH_SYM)) {
            if (text.equals("Geocache Found"))
                mCachePersisterFacade.setTag(Tags.FOUND, true);
        }
        
        for (String writeLineMatch : XPATH_PLAINLINES) {
            if (fullPath.equals(writeLineMatch)) {
                mCachePersisterFacade.line(text);
                return true;
            }
        }
        return true;
    }
}
