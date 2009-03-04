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

import com.google.code.geobeagle.io.GpxLoader.Cache;
import com.google.code.geobeagle.io.GpxToCache.XmlPullParserWrapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

public class GpxToCacheTest extends TestCase {
    public void testGpxCachesEmpty() throws XmlPullParserException, IOException {
        GpxToCache gpxToCache = createMock(GpxToCache.class);

        expect(gpxToCache.load()).andReturn(null);

        replay(gpxToCache);
        GpxCaches gpxCaches = new GpxCaches(gpxToCache, null);
        Iterator<Cache> iterator = gpxCaches.iterator();
        assertFalse(iterator.hasNext());
        assertNull(iterator.next());
        verify(gpxToCache);
    }

    public void testGpxCachesOne() throws XmlPullParserException, IOException {
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        Cache cache = createMock(Cache.class);

        expect(gpxToCache.load()).andReturn(cache);
        expect(gpxToCache.load()).andReturn(null);

        replay(gpxToCache);
        GpxCaches gpxCaches = new GpxCaches(gpxToCache, null);
        Iterator<Cache> iterator = gpxCaches.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(cache, iterator.next());
        assertFalse(iterator.hasNext());
        verify(gpxToCache);
    }

    public void testGetSource() {
        XmlPullParserWrapper xmlPullParserWrapper = createMock(XmlPullParserWrapper.class);
        
        expect(xmlPullParserWrapper.getSource()).andReturn("/my/path");
        
        replay(xmlPullParserWrapper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParserWrapper, null);
        assertEquals("/my/path", gpxToCache.getSource());
        verify(xmlPullParserWrapper);
    }
    
    public void testOpen() throws FileNotFoundException, XmlPullParserException {
        XmlPullParserWrapper xmlPullParserWrapper = createMock(XmlPullParserWrapper.class);
        xmlPullParserWrapper.open("/my/path");
        
        replay(xmlPullParserWrapper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParserWrapper, null);
        gpxToCache.open("/my/path");
        verify(xmlPullParserWrapper);
    }
    
    public void testLoad() throws XmlPullParserException, IOException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);
        Cache cache = createMock(Cache.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.START_DOCUMENT)).andReturn(null);

        expect(xmlPullParser.next()).andReturn(XmlPullParser.START_TAG);
        expect(eventHelper.handleEvent(XmlPullParser.START_TAG)).andReturn(null);

        expect(xmlPullParser.next()).andReturn(XmlPullParser.START_TAG);
        expect(eventHelper.handleEvent(XmlPullParser.START_TAG)).andReturn(cache);
        expect(xmlPullParser.next()).andReturn(XmlPullParser.START_TAG);

        replay(xmlPullParser);
        replay(eventHelper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, eventHelper);
        assertEquals(cache, gpxToCache.load());
        verify(xmlPullParser);
        verify(eventHelper);
    }

    public void testLoadEmpty() throws XmlPullParserException, IOException {
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.END_DOCUMENT);

        replay(xmlPullParser);
        replay(eventHelper);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, eventHelper);
        assertEquals(null, gpxToCache.load());
        verify(xmlPullParser);
        verify(eventHelper);
    }
}
