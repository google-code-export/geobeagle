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

import com.google.code.geobeagle.io.EventHelper.XmlPathBuilder;
import com.google.code.geobeagle.io.GpxLoader.Cache;
import com.google.code.geobeagle.io.GpxToCache.XmlPullParserWrapper;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

import junit.framework.TestCase;

public class EventHelperTest extends TestCase {

    public void testEventHelperEnd() throws IOException {
        XmlPathBuilder xmlPathBuilder = createMock(XmlPathBuilder.class);
        GpxEventHandler gpxEventHandler = createMock(GpxEventHandler.class);
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        Cache cache = createMock(Cache.class);

        expect(xmlPathBuilder.getPath()).andReturn("/path");
        expect(gpxEventHandler.endTag("/path")).andReturn(cache);
        expect(xmlPullParser.getName()).andReturn("name");
        xmlPathBuilder.endTag("name");

        replay(xmlPathBuilder);
        replay(gpxEventHandler);
        replay(xmlPullParser);
        EventHelper eventHelper = new EventHelper(xmlPathBuilder, gpxEventHandler, xmlPullParser);
        assertEquals(cache, eventHelper.handleEvent(XmlPullParser.END_TAG));
        verify(xmlPathBuilder);
        verify(gpxEventHandler);
        verify(xmlPullParser);
    }

    public void testEventHelperStart() throws IOException {
        XmlPathBuilder xmlPathBuilder = createMock(XmlPathBuilder.class);
        GpxEventHandler gpxEventHandler = createMock(GpxEventHandler.class);
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);

        expect(xmlPullParser.getName()).andReturn("some tag");
        xmlPathBuilder.startTag("some tag");
        expect(xmlPathBuilder.getPath()).andReturn("/foo");
        gpxEventHandler.startTag("/foo", xmlPullParser);

        replay(xmlPathBuilder);
        replay(gpxEventHandler);
        replay(xmlPullParser);
        EventHelper eventHelper = new EventHelper(xmlPathBuilder, gpxEventHandler, xmlPullParser);
        assertEquals(null, eventHelper.handleEvent(XmlPullParser.START_TAG));
        verify(xmlPathBuilder);
        verify(gpxEventHandler);
        verify(xmlPullParser);
    }

    public void testEventHelperText() throws IOException {
        XmlPathBuilder xmlPathBuilder = createMock(XmlPathBuilder.class);
        GpxEventHandler gpxEventHandler = createMock(GpxEventHandler.class);
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);

        expect(xmlPathBuilder.getPath()).andReturn("/path");
        expect(xmlPullParser.getText()).andReturn("text");
        gpxEventHandler.text("/path", "text");

        replay(xmlPathBuilder);
        replay(gpxEventHandler);
        replay(xmlPullParser);
        EventHelper eventHelper = new EventHelper(xmlPathBuilder, gpxEventHandler, xmlPullParser);
        assertEquals(null, eventHelper.handleEvent(XmlPullParser.TEXT));
        verify(xmlPathBuilder);
        verify(gpxEventHandler);
        verify(xmlPullParser);
    }

    public void testXmlPathBuilderEmpty() {
        XmlPathBuilder xmlPathBuilder = new XmlPathBuilder();
        assertEquals("", xmlPathBuilder.getPath());
    }

    public void testXmlPathBuilderOne() {
        XmlPathBuilder xmlPathBuilder = new XmlPathBuilder();
        xmlPathBuilder.startTag("test");
        assertEquals("/test", xmlPathBuilder.getPath());
        xmlPathBuilder.endTag("test");
        assertEquals("", xmlPathBuilder.getPath());
    }

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
