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
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;

import com.google.code.geobeagle.xmlimport.EventDispatcher.XmlPathBuilder;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;

@RunWith(PowerMockRunner.class)
public class EventHelperTest {


    private Reader reader;
    private XmlPathBuilder xmlPathBuilder;
    private EventHandlerGpx eventHandlerGpx;
    private XmlPullParser xmlPullParser;
    private Provider<XmlPullParser> xmlPullParserProvider;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        reader = createMock(Reader.class);
        xmlPathBuilder = createMock(XmlPathBuilder.class);
        eventHandlerGpx = createMock(EventHandlerGpx.class);
        xmlPullParser = createMock(XmlPullParser.class);
        xmlPullParserProvider = createMock(Provider.class);
    }

    @Test
    public void testEventHelperEnd() throws IOException, XmlPullParserException {
        expect(xmlPullParserProvider.get()).andReturn(xmlPullParser);
        xmlPullParser.setInput(reader);
        expect(xmlPathBuilder.getPath()).andReturn("/path");
        eventHandlerGpx.endTag("name", "/path");
        expect(xmlPullParser.getName()).andReturn("name");
        xmlPathBuilder.endTag("name");

        PowerMock.replayAll();
        EventDispatcher eventDispatcher = new EventDispatcher(xmlPathBuilder, eventHandlerGpx,
                xmlPullParserProvider, null);
        eventDispatcher.setInput(reader);
        eventDispatcher.handleEvent(XmlPullParser.END_TAG);
        PowerMock.verifyAll();
    }

    @Test
    public void testEventHelperStart() throws IOException, XmlPullParserException {
        expect(xmlPullParserProvider.get()).andReturn(xmlPullParser);
        xmlPullParser.setInput(reader);
        expect(xmlPullParser.getName()).andReturn("some tag");
        xmlPathBuilder.startTag("some tag");
        expect(xmlPathBuilder.getPath()).andReturn("/foo");
        eventHandlerGpx.startTag("some tag", "/foo");

        PowerMock.replayAll();
        EventDispatcher eventDispatcher = new EventDispatcher(xmlPathBuilder, eventHandlerGpx,
                xmlPullParserProvider, null);
        eventDispatcher.setInput(reader);
        eventDispatcher.handleEvent(XmlPullParser.START_TAG);
        PowerMock.verifyAll();
    }

    @Test
    public void testEventHelperText() throws IOException, XmlPullParserException {
        expect(xmlPullParserProvider.get()).andReturn(xmlPullParser);
        xmlPullParser.setInput(reader);
        expect(xmlPathBuilder.getPath()).andReturn("/path");
        expect(xmlPullParser.getText()).andReturn("text");
        expect(eventHandlerGpx.text("/path", "text")).andReturn(true);

        PowerMock.replayAll();
        EventDispatcher eventDispatcher = new EventDispatcher(xmlPathBuilder, eventHandlerGpx,
                xmlPullParserProvider, null);
        eventDispatcher.setInput(reader);
        eventDispatcher.handleEvent(XmlPullParser.TEXT);
        PowerMock.verifyAll();
    }

    @Test
    public void testXmlPathBuilderEmpty() {
        XmlPathBuilder xmlPathBuilder = new XmlPathBuilder();
        assertEquals("", xmlPathBuilder.getPath());
    }

    @Test
    public void testXmlPathBuilderOne() {
        XmlPathBuilder xmlPathBuilder = new XmlPathBuilder();
        xmlPathBuilder.startTag("test");
        assertEquals("/test", xmlPathBuilder.getPath());
        xmlPathBuilder.endTag("test");
        assertEquals("", xmlPathBuilder.getPath());
    }

    @Test
    public void testXmlPathBuilderTwo() {
        XmlPathBuilder xmlPathBuilder = new XmlPathBuilder();
        xmlPathBuilder.startTag("test");
        assertEquals("/test", xmlPathBuilder.getPath());

        xmlPathBuilder.startTag("foo");
        assertEquals("/test/foo", xmlPathBuilder.getPath());
        xmlPathBuilder.endTag("foo");

        assertEquals("/test", xmlPathBuilder.getPath());
        xmlPathBuilder.endTag("test");
        assertEquals("", xmlPathBuilder.getPath());
    }

}
