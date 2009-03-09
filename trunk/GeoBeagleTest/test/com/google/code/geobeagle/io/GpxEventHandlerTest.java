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

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.di.GpxToCacheDI;
import com.google.code.geobeagle.io.di.GpxToCacheDI.XmlPullParserWrapper;

import java.io.IOException;

import junit.framework.TestCase;

public class GpxEventHandlerTest extends TestCase {

    public void testEndTag() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.endTag();

        replay(cachePersisterFacade);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.endTag("/gpx/wpt");
        verify(cachePersisterFacade);
    }

    public void testStartTagNotCache() throws IOException {
        GpxEventHandler gpxEventHandler = new GpxEventHandler(null);
        gpxEventHandler.startTag("/gpx/wptNot", null);
    }

    public void testStartTagCache() throws IOException {
        GpxToCacheDI.XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.wpt(xmlPullParser);

        replay(cachePersisterFacade);
        replay(xmlPullParser);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.startTag("/gpx/wpt", xmlPullParser);
        verify(xmlPullParser);
        verify(cachePersisterFacade);
    }

    public void testTextWptName() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.wptName("my wpt");

        replay(cachePersisterFacade);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.text(GpxEventHandler.XPATH_WPTNAME, "my wpt");
        verify(cachePersisterFacade);
    }

    public void testGroundspeakName() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.groundspeakName("my wpt");

        replay(cachePersisterFacade);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.text(GpxEventHandler.XPATH_GROUNDSPEAKNAME, "my wpt");
        verify(cachePersisterFacade);
    }

    public void testLogDate() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.logDate("date");

        replay(cachePersisterFacade);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.text(GpxEventHandler.XPATH_LOGDATE, "date");
        verify(cachePersisterFacade);
    }

    public void testPlainLine() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.line("hello");

        replay(cachePersisterFacade);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.text(GpxEventHandler.XPATH_PLAINLINES[0], " hello  \t");
        verify(cachePersisterFacade);
    }

    public void testMatchNothing() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        replay(cachePersisterFacade);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.text("/gpx/foo", "hello");
        verify(cachePersisterFacade);
    }

    public void testHint() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        cachePersisterFacade.hint("look under the rock");

        replay(cachePersisterFacade);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.text(GpxEventHandler.XPATH_HINT, " look under the rock");
        verify(cachePersisterFacade);
    }

    public void testHintEmpty() throws IOException {
        CachePersisterFacade cachePersisterFacade = createMock(CachePersisterFacade.class);

        replay(cachePersisterFacade);
        GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        gpxEventHandler.text(GpxEventHandler.XPATH_HINT, "   ");
        verify(cachePersisterFacade);
    }
}
