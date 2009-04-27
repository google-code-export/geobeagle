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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.data.GeocacheFactory.Source;
import com.google.code.geobeagle.io.GpxToCacheDI.XmlPullParserWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.text.ParseException;

@RunWith(PowerMockRunner.class)
public class EventHandlerGpxTest {

    private final CachePersisterFacade mCachePersisterFacade = createMock(CachePersisterFacade.class);

    @Test
    public void testEndTag() throws IOException {
        mCachePersisterFacade.endTag(Source.GPX);

        replay(mCachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(mCachePersisterFacade);
        eventHandlerGpx.endTag("/gpx/wpt");
        verify(mCachePersisterFacade);
    }

    @Test
    public void testGpxTime() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        expect(cachePersisterFacade.gpxTime("today")).andReturn(true);

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_GPXTIME, "today");
        verify(cachePersisterFacade);
    }

    @Test
    public void testGroundspeakName() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.groundspeakName("my wpt");

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_GROUNDSPEAKNAME, "my wpt");
        verify(cachePersisterFacade);
    }

    @Test
    public void testHint() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.hint("look under the rock");

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_HINT, " look under the rock");
        verify(cachePersisterFacade);
    }

    @Test
    public void testHintEmpty() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_HINT, "   ");
        verify(cachePersisterFacade);
    }

    @Test
    public void testLogDate() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.logDate("date");

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_LOGDATE, "date");
        verify(cachePersisterFacade);
    }

    @Test
    public void testMatchNothing() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text("/gpx/foo", "hello");
        verify(cachePersisterFacade);
    }

    @Test
    public void testPlainLine() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.line("hello");
        cachePersisterFacade.wptDesc("hello");

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_PLAINLINES[0], " hello  \t");
        verify(cachePersisterFacade);
    }

    @Test
    public void testStartTagCache() throws IOException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.newCache();
        expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37");
        expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("122");
        cachePersisterFacade.wpt("37", "122");

        replay(cachePersisterFacade);
        replay(xmlPullParser);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.startTag("/gpx/wpt", xmlPullParser);
        verify(xmlPullParser);
        verify(cachePersisterFacade);
    }

    @Test
    public void testStartTagNotCache() throws IOException {
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(null);
        eventHandlerGpx.startTag("/gpx/wptNot", null);
    }

    @Test
    public void testTextSymbol() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.symbol("Geocache Found");

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_SYM, "Geocache Found");
        verify(cachePersisterFacade);
    }

    @Test
    public void testTextWptName() throws IOException, ParseException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.wptName("my wpt");

        replay(cachePersisterFacade);
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHandlerGpx.text(EventHandlerGpx.XPATH_WPTNAME, "my wpt");
        verify(cachePersisterFacade);
    }
}
