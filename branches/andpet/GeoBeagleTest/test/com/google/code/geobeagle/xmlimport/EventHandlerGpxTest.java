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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.xmlimport.CachePersisterFacade.TextHandler;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
public class EventHandlerGpxTest {

    private final CachePersisterFacade mCachePersisterFacade = createMock(CachePersisterFacade.class);

    @Test
    public void testEndTag() throws IOException {
        mCachePersisterFacade.endCache(Source.GPX);

        replay(mCachePersisterFacade);
        new EventHandlerGpx(mCachePersisterFacade, null).endTag("/gpx/wpt");
        verify(mCachePersisterFacade);
    }

    @Test
    public void testGpxTime() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        HashMap<String, TextHandler> textHandlers = new HashMap<String, TextHandler>();
        expect(cachePersisterFacade.gpxTime("today")).andReturn(true);

        replay(cachePersisterFacade);
        new EventHandlerGpx(cachePersisterFacade, textHandlers).text(EventHandlerGpx.XPATH_GPXTIME, "today");
        verify(cachePersisterFacade);
    }

    @Test
    public void testMatchNothing() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        HashMap<String, TextHandler> textHandlers = new HashMap<String, TextHandler>();

        replay(cachePersisterFacade);
        new EventHandlerGpx(cachePersisterFacade, textHandlers).text("/gpx/foo", "hello");
        verify(cachePersisterFacade);
    }

    @Test
    public void testPlainLine() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        HashMap<String, TextHandler> textHandlers = new HashMap<String, TextHandler>();

        cachePersisterFacade.line("hello");

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade, textHandlers);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_PLAINLINES[0], " hello  \t");
        verify(cachePersisterFacade);
    }

    @Test
    public void testStartTagCache() {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        expect(xmlPullParser.getAttributeValue(null, "available")).andReturn("true");
        expect(xmlPullParser.getAttributeValue(null, "archived")).andReturn("false");
        cachePersisterFacade.setTag(Tags.UNAVAILABLE, false);
        cachePersisterFacade.setTag(Tags.ARCHIVED, false);

        expect(xmlPullParser.getAttributeValue(null, "available")).andReturn("false");
        expect(xmlPullParser.getAttributeValue(null, "archived")).andReturn("true");
        cachePersisterFacade.setTag(Tags.UNAVAILABLE, true);
        cachePersisterFacade.setTag(Tags.ARCHIVED, true);

        replay(cachePersisterFacade);
        replay(xmlPullParser);
        final EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(
                cachePersisterFacade, null);
        eventHandlerGpx.startTag("/gpx/wpt/groundspeak:cache", xmlPullParser);
        eventHandlerGpx.startTag("/gpx/wpt/groundspeak:cache", xmlPullParser);
        verify(xmlPullParser);
        verify(cachePersisterFacade);
    }

    @Test
    public void testStartTagWaypoing() {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.startCache();
        expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37");
        expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("122");
        cachePersisterFacade.wpt("37", "122");

        replay(cachePersisterFacade);
        replay(xmlPullParser);
        new EventHandlerGpx(cachePersisterFacade, null).startTag("/gpx/wpt", xmlPullParser);
        verify(xmlPullParser);
        verify(cachePersisterFacade);
    }

    @Test
    public void testStartTagNotCache() {
        new EventHandlerGpx(null, null).startTag("/gpx/wptNot", null);
    }

    @Test
    public void testTextSymbol() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        HashMap<String, TextHandler> textHandlers = new HashMap<String, TextHandler>();

        cachePersisterFacade.setTag(Tags.FOUND, true);

        replay(cachePersisterFacade);
        new EventHandlerGpx(cachePersisterFacade, textHandlers).text(EventHandlerGpx.XPATH_SYM, "Geocache Found");
        verify(cachePersisterFacade);
    }

    @Test
    public void testTextCacheType() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);
        TextHandler cacheType = createMock(TextHandler.class);
        TextHandler cacheDifficulty = createMock(TextHandler.class);
        TextHandler cacheContainer = createMock(TextHandler.class);
        TextHandler cacheTerrain = createMock(TextHandler.class);

        HashMap<String, TextHandler> textHandlers = new HashMap<String, TextHandler>();
        textHandlers.put(EventHandlerGpx.XPATH_CACHE_TYPE, cacheType);
        textHandlers.put(EventHandlerGpx.XPATH_CACHE_DIFFICULTY,
                cacheDifficulty);
        textHandlers.put(EventHandlerGpx.XPATH_CACHE_TERRAIN, cacheTerrain);
        textHandlers.put(EventHandlerGpx.XPATH_CACHE_CONTAINER, cacheContainer);

        cacheType.text("cache type");
        cachePersisterFacade.line("cache type");
        cacheDifficulty.text("difficulty");
        cacheTerrain.text("terrain");
        cacheContainer.text("container");
        cachePersisterFacade.line("container");

        replay(cachePersisterFacade);
        final EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade, textHandlers);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_CACHE_TYPE, "cache type");
        eventHandlerGpx.text(EventHandlerGpx.XPATH_CACHE_DIFFICULTY, "difficulty");
        eventHandlerGpx.text(EventHandlerGpx.XPATH_CACHE_TERRAIN, "terrain");
        eventHandlerGpx.text(EventHandlerGpx.XPATH_CACHE_CONTAINER, "container");
        verify(cachePersisterFacade);
    }

}
