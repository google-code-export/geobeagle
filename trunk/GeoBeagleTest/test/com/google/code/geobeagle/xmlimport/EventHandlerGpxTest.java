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
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.GeocacheFactory.Source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class EventHandlerGpxTest {

    private CachePersisterFacade cachePersisterFacade;
    private XmlPullParserWrapper xmlPullParser;

    @Before
    public void setUp() {
        cachePersisterFacade = createMock(CachePersisterFacade.class);
        xmlPullParser = createMock(XmlPullParserWrapper.class);
    }

    @Test
    public void testEndTag() throws IOException {
        cachePersisterFacade.endCache(Source.GPX);

        replayAll();
        new EventHandlerGpx().endTag("wpt", "/gpx/wpt", cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testGpxTime() throws IOException {
        expect(cachePersisterFacade.gpxTime("today")).andReturn(true);

        replayAll();
        new EventHandlerGpx().text(GpxPath.XPATH_GPXTIME.getPath(), "today", xmlPullParser,
                cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testGroundspeakName() throws IOException {
        cachePersisterFacade.groundspeakName("my wpt");

        replayAll();
        new EventHandlerGpx().text(GpxPath.XPATH_GROUNDSPEAKNAME.getPath(), "my wpt",
                xmlPullParser, cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testHint() throws IOException {
        cachePersisterFacade.hint("look under the rock");

        replayAll();
        new EventHandlerGpx().text(GpxPath.XPATH_HINT.getPath(), " look under the rock",
                xmlPullParser, cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testHintEmpty() throws IOException {
        replayAll();
        new EventHandlerGpx().text(GpxPath.XPATH_HINT.getPath(), "   ", xmlPullParser,
                cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testLogDate() throws IOException {
        cachePersisterFacade.logDate("date");

        replayAll();
        new EventHandlerGpx().text(GpxPath.XPATH_LOGDATE.getPath(), "date", xmlPullParser,
                cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testMatchNothing() throws IOException {
        replayAll();
        new EventHandlerGpx().text("/gpx/foo", "hello", xmlPullParser, cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testPlainLine() throws IOException {
        cachePersisterFacade.line("hello");

        replayAll();
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx();
        eventHandlerGpx.text(GpxPath.XPATH_AU_SUMMARY.getPath(), " hello  \t", xmlPullParser,
                cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testStartTagCache() {
        cachePersisterFacade.startCache();
        expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37");
        expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("122");
        cachePersisterFacade.wpt("37", "122");

        replayAll();
        new EventHandlerGpx().startTag("wpt", "/gpx/wpt", xmlPullParser, cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testAvailable() {
        expect(xmlPullParser.getAttributeValue(null, "available")).andReturn("true");
        expect(xmlPullParser.getAttributeValue(null, "archived")).andReturn("false");
        cachePersisterFacade.available("true");
        cachePersisterFacade.archived("false");

        replayAll();
        new EventHandlerGpx().startTag("groundspeak:cache", "/gpx/wpt/groundspeak:cache",
                xmlPullParser, cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testStartTagNotCache() {
        new EventHandlerGpx().startTag("wptNot", "/gpx/wptNot", null, cachePersisterFacade);
    }

    @Test
    public void testTextSymbol() throws IOException {
        cachePersisterFacade.symbol("Geocache Found");

        replayAll();
        new EventHandlerGpx().text(EventHandlerGpx.XPATH_SYM, "Geocache Found", xmlPullParser,
                cachePersisterFacade);
        verifyAll();
    }

    @Test
    public void testTextWptName() throws IOException {
        cachePersisterFacade.wptName("my wpt");

        replayAll();
        new EventHandlerGpx().text(EventHandlerGpx.XPATH_WPTNAME, "my wpt", xmlPullParser,
                cachePersisterFacade);
        verifyAll();
    }
}
