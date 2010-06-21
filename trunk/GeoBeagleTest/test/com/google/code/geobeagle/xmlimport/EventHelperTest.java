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

import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class EventHelperTest {

    @Test
    public void testEventHelperEnd() throws IOException {
        XmlPathBuilder xmlPathBuilder = PowerMock.createMock(XmlPathBuilder.class);
        EventHandlerGpx eventHandlerGpx = PowerMock.createMock(EventHandlerGpx.class);
        XmlPullParserWrapper xmlPullParser = PowerMock.createMock(XmlPullParserWrapper.class);

        expect(xmlPathBuilder.getPath()).andReturn("/path");
        eventHandlerGpx.endTag("name", "/path");
        expect(xmlPullParser.getName()).andReturn("name");
        xmlPathBuilder.endTag("name");

        PowerMock.replayAll();
        EventHelper eventHelper = new EventHelper(xmlPathBuilder, eventHandlerGpx, xmlPullParser);
        eventHelper.handleEvent(XmlPullParser.END_TAG);
        PowerMock.verifyAll();
    }

    @Test
    public void testEventHelperStart() throws IOException {
        XmlPathBuilder xmlPathBuilder = PowerMock.createMock(XmlPathBuilder.class);
        EventHandlerGpx eventHandlerGpx = PowerMock.createMock(EventHandlerGpx.class);
        XmlPullParserWrapper xmlPullParser = PowerMock.createMock(XmlPullParserWrapper.class);

        expect(xmlPullParser.getName()).andReturn("some tag");
        xmlPathBuilder.startTag("some tag");
        expect(xmlPathBuilder.getPath()).andReturn("/foo");
        eventHandlerGpx.startTag("some tag", "/foo", xmlPullParser);

        PowerMock.replayAll();
        EventHelper eventHelper = new EventHelper(xmlPathBuilder, eventHandlerGpx, xmlPullParser);
        eventHelper.handleEvent(XmlPullParser.START_TAG);
        PowerMock.verifyAll();
    }

    @Test
    public void testEventHelperText() throws IOException {
        XmlPathBuilder xmlPathBuilder = PowerMock.createMock(XmlPathBuilder.class);
        EventHandlerGpx eventHandlerGpx = PowerMock.createMock(EventHandlerGpx.class);
        XmlPullParserWrapper xmlPullParser = PowerMock.createMock(XmlPullParserWrapper.class);

        expect(xmlPathBuilder.getPath()).andReturn("/path");
        expect(xmlPullParser.getText()).andReturn("text");
        expect(eventHandlerGpx.text("/path", "text", xmlPullParser)).andReturn(true);

        PowerMock.replayAll();
        EventHelper eventHelper = new EventHelper(xmlPathBuilder, eventHandlerGpx, xmlPullParser);
        eventHelper.handleEvent(XmlPullParser.TEXT);
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
