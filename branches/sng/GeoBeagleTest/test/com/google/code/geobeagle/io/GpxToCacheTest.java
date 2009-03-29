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

import com.google.code.geobeagle.io.GpxToCache.CancelException;
import com.google.code.geobeagle.io.GpxToCacheDI.XmlPullParserWrapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import junit.framework.TestCase;

public class GpxToCacheTest extends TestCase {

    public void testGetSource() {
        XmlPullParserWrapper xmlPullParserWrapper = createMock(XmlPullParserWrapper.class);

        expect(xmlPullParserWrapper.getSource()).andReturn("/my/path");

        replay(xmlPullParserWrapper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParserWrapper, null);
        assertEquals("/my/path", gpxToCache.getSource());
        verify(xmlPullParserWrapper);
    }

    public void testLoadAbort() throws XmlPullParserException, IOException, ParseException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);

        replay(xmlPullParser);
        replay(eventHelper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, eventHelper);
        gpxToCache.abort();
        try {
            gpxToCache.load();
            assertFalse("expected to throw cancel exception", false);
        } catch (CancelException e) {
        }
        verify(xmlPullParser);
        verify(eventHelper);
    }

    public void testLoadNone() throws XmlPullParserException, IOException, ParseException,
            CancelException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);

        replay(xmlPullParser);
        replay(eventHelper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, eventHelper);
        assertEquals(false, gpxToCache.load());
        verify(xmlPullParser);
        verify(eventHelper);
    }

    public void testLoadOne() throws XmlPullParserException, IOException, ParseException,
            CancelException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.START_DOCUMENT)).andReturn(true);
        expect(xmlPullParser.next()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);

        replay(xmlPullParser);
        replay(eventHelper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, eventHelper);
        assertEquals(false, gpxToCache.load());
        verify(xmlPullParser);
        verify(eventHelper);
    }

    public void testLoadSkipThisFile() throws XmlPullParserException, IOException, ParseException,
            CancelException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.START_DOCUMENT)).andReturn(false);

        replay(xmlPullParser);
        replay(eventHelper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, eventHelper);
        assertEquals(true, gpxToCache.load());
        verify(xmlPullParser);
        verify(eventHelper);
    }

    public void testLoadTwo() throws XmlPullParserException, IOException, ParseException,
            CancelException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.START_DOCUMENT)).andReturn(true);

        expect(xmlPullParser.next()).andReturn(XmlPullParser.START_TAG);
        expect(eventHelper.handleEvent(XmlPullParser.START_TAG)).andReturn(true);

        expect(xmlPullParser.next()).andReturn(XmlPullParser.START_TAG);
        expect(eventHelper.handleEvent(XmlPullParser.START_TAG)).andReturn(true);
        expect(xmlPullParser.next()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);

        replay(xmlPullParser);
        replay(eventHelper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, eventHelper);
        assertEquals(false, gpxToCache.load());
        verify(xmlPullParser);
        verify(eventHelper);
    }

    public void testOpen() throws FileNotFoundException, XmlPullParserException {
        XmlPullParserWrapper xmlPullParserWrapper = createMock(XmlPullParserWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        xmlPullParserWrapper.open("/my/path");
        eventHelper.reset();

        replay(xmlPullParserWrapper);
        replay(eventHelper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParserWrapper, eventHelper);
        gpxToCache.open("/my/path");
        verify(xmlPullParserWrapper);
        verify(eventHelper);
    }

}
