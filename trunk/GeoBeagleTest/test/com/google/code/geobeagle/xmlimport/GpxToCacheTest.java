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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpxToCache.class, Log.class
})
public class GpxToCacheTest {
    private FileAlreadyLoadedChecker fileAlreadyLoadedChecker;

    @Before
    public void setUp() {
        fileAlreadyLoadedChecker = PowerMock.createMock(FileAlreadyLoadedChecker.class);
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
    }

    @Test
    public void testLoadAbort() throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = PowerMock.createMock(XmlPullParser.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(fileAlreadyLoadedChecker.isAlreadyLoaded(null)).andReturn(false);
        eventHelper.open(null, null);

        PowerMock.replayAll();
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, new Aborter(),
                fileAlreadyLoadedChecker);
        gpxToCache.abort();
        try {
            gpxToCache.load(eventHelper, null, null);
            assertFalse("expected to throw cancel exception", false);
        } catch (CancelException e) {
        }
        PowerMock.verifyAll();
    }

    @Test
    public void testLoadNone() throws XmlPullParserException, IOException, CancelException {
        XmlPullParser xmlPullParser = PowerMock.createMock(XmlPullParser.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.END_DOCUMENT, null, null, xmlPullParser))
                .andReturn(true);
        expect(fileAlreadyLoadedChecker.isAlreadyLoaded(null)).andReturn(false);
        eventHelper.open(null, null);

        PowerMock.replayAll();
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, new Aborter(),
                fileAlreadyLoadedChecker);
        assertEquals(false, gpxToCache.load(eventHelper, null, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testLoadOne() throws XmlPullParserException, IOException, CancelException {
        XmlPullParser xmlPullParser = PowerMock.createMock(XmlPullParser.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        eventHelper.open(null, null);
        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.START_DOCUMENT, null, null, xmlPullParser))
                .andReturn(true);
        expect(xmlPullParser.next()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.END_DOCUMENT, null, null, xmlPullParser))
                .andReturn(true);
        expect(fileAlreadyLoadedChecker.isAlreadyLoaded(null)).andReturn(false);

        PowerMock.replayAll();
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, new Aborter(),
                fileAlreadyLoadedChecker);
        assertEquals(false, gpxToCache.load(eventHelper, null, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testAborterReset() {
        final Aborter aborter = new Aborter();
        assertFalse(aborter.isAborted());
        aborter.abort();
        assertTrue(aborter.isAborted());
        aborter.reset();
        assertFalse(aborter.isAborted());
    }

    @Test
    public void testLoadSkipThisFile() throws XmlPullParserException, IOException, CancelException {
        XmlPullParser xmlPullParser = PowerMock.createMock(XmlPullParser.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        eventHelper.open(null, null);
        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.START_DOCUMENT, null, null, xmlPullParser))
                .andReturn(false);
        expect(fileAlreadyLoadedChecker.isAlreadyLoaded(null)).andReturn(false);

        PowerMock.replayAll();
        GpxToCache gpxToCache = new GpxToCache(xmlPullParser, new Aborter(),
                fileAlreadyLoadedChecker);
        assertEquals(true, gpxToCache.load(eventHelper, null, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testLoadTwo() throws XmlPullParserException, IOException, CancelException {
        XmlPullParser xmlPullParser = PowerMock.createMock(XmlPullParser.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        eventHelper.open(null, null);
        expect(fileAlreadyLoadedChecker.isAlreadyLoaded(null)).andReturn(false);
        expect(xmlPullParser.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.START_DOCUMENT, null, null, xmlPullParser))
                .andReturn(true);

        expect(xmlPullParser.next()).andReturn(XmlPullParser.START_TAG);
        expect(eventHelper.handleEvent(XmlPullParser.START_TAG, null, null, xmlPullParser))
                .andReturn(true);

        expect(xmlPullParser.next()).andReturn(XmlPullParser.START_TAG);
        expect(eventHelper.handleEvent(XmlPullParser.START_TAG, null, null, xmlPullParser))
                .andReturn(true);
        expect(xmlPullParser.next()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.END_DOCUMENT, null, null, xmlPullParser))
                .andReturn(true);

        PowerMock.replayAll();
        assertEquals(false,
                new GpxToCache(xmlPullParser, new Aborter(), fileAlreadyLoadedChecker).load(
                        eventHelper, null, null));
        PowerMock.verifyAll();
    }
}
