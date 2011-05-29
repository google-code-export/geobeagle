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
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import java.io.IOException;
import java.io.Reader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        GpxToCache.class, Log.class
})
public class GpxToCacheTest extends GeoBeagleTest {
    private LocAlreadyLoadedChecker locAlreadyLoadedChecker;
    private Provider<ImportWakeLock> importWakeLockProvider;
    private ImportWakeLock importWakeLock;
    private Reader reader;
    private EventDispatcher eventDispatcher;
    private CacheXmlTagsToSql cacheXmlTagsToSql;
    private AbortState abortState;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        importWakeLockProvider = createMock(Provider.class);
        locAlreadyLoadedChecker = createMock(LocAlreadyLoadedChecker.class);
        importWakeLock = createMock(ImportWakeLock.class);
        eventDispatcher = createMock(EventDispatcher.class);
        cacheXmlTagsToSql = createMock(CacheXmlTagsToSql.class);
        abortState = createMock(AbortState.class);
    }

    @Test
    public void testLoadAbort() throws XmlPullParserException {
        eventDispatcher.setInput(reader);
        cacheXmlTagsToSql.open("foo.gpx");
        expect(importWakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(GpxToCache.WAKELOCK_DURATION);
        expect(eventDispatcher.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(locAlreadyLoadedChecker.isAlreadyLoaded("/path/to/foo.gpx")).andReturn(false);
        expect(abortState.isAborted()).andReturn(true);
        eventDispatcher.open();
        cacheXmlTagsToSql.close(false);
        eventDispatcher.close();
        replayAll();
        GpxToCache gpxToCache = new GpxToCache(abortState, locAlreadyLoadedChecker,
                eventDispatcher, cacheXmlTagsToSql, importWakeLockProvider, null);
        try {
            gpxToCache.load("/path/to/foo.gpx", reader);
            assertFalse("expected to throw cancel exception", false);
        } catch (CancelException e) {
        }
        verifyAll();
    }

    @Test
    public void testLoadNone() throws XmlPullParserException, IOException, CancelException {
        expect(importWakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(GpxToCache.WAKELOCK_DURATION);
        eventDispatcher.setInput(reader);
        cacheXmlTagsToSql.open("foo.gpx");
        expect(locAlreadyLoadedChecker.isAlreadyLoaded("/path/to/foo.gpx")).andReturn(false);
        eventDispatcher.open();
        expect(eventDispatcher.getEventType()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventDispatcher.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);
        cacheXmlTagsToSql.close(true);
        expect(cacheXmlTagsToSql.getNumberOfCachesLoad()).andReturn(0);
        eventDispatcher.close();

        replayAll();
        GpxToCache gpxToCache = new GpxToCache(abortState, locAlreadyLoadedChecker,
                eventDispatcher, cacheXmlTagsToSql, importWakeLockProvider, null);
        assertEquals(0, gpxToCache.load("/path/to/foo.gpx", reader));
        verifyAll();
    }

    @Test
    public void testLoadOne() throws XmlPullParserException, IOException, CancelException {
        expect(importWakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(GpxToCache.WAKELOCK_DURATION);
        eventDispatcher.setInput(reader);
        cacheXmlTagsToSql.open("foo.gpx");
        expect(locAlreadyLoadedChecker.isAlreadyLoaded("/path/to/foo.gpx")).andReturn(false);
        eventDispatcher.open();
        expect(eventDispatcher.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventDispatcher.handleEvent(XmlPullParser.START_DOCUMENT)).andReturn(true);
        expect(abortState.isAborted()).andReturn(false);

        expect(eventDispatcher.next()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventDispatcher.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);
        cacheXmlTagsToSql.close(true);
        expect(cacheXmlTagsToSql.getNumberOfCachesLoad()).andReturn(12);
        eventDispatcher.close();

        replayAll();
        GpxToCache gpxToCache = new GpxToCache(abortState, locAlreadyLoadedChecker,
                eventDispatcher, cacheXmlTagsToSql, importWakeLockProvider, null);
        assertEquals(12, gpxToCache.load("/path/to/foo.gpx", reader));
        verifyAll();
    }

    @Test
    public void testLoadSkipThisFile() throws XmlPullParserException, CancelException {
        expect(importWakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(GpxToCache.WAKELOCK_DURATION);
        eventDispatcher.setInput(reader);
        cacheXmlTagsToSql.open("foo.gpx");
        expect(locAlreadyLoadedChecker.isAlreadyLoaded("/path/to/foo.gpx")).andReturn(true);
        cacheXmlTagsToSql.close(false);
        eventDispatcher.close();

        replayAll();
        GpxToCache gpxToCache = new GpxToCache(abortState, locAlreadyLoadedChecker,
                eventDispatcher, cacheXmlTagsToSql, importWakeLockProvider, null);
        assertEquals(-1, gpxToCache.load("/path/to/foo.gpx", null));
        verifyAll();
    }

    @Test
    public void testLoadTwo() throws XmlPullParserException, IOException, CancelException {
        expect(importWakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(GpxToCache.WAKELOCK_DURATION);
        eventDispatcher.setInput(reader);
        cacheXmlTagsToSql.open("foo.gpx");
        expect(locAlreadyLoadedChecker.isAlreadyLoaded("/path/to/foo.gpx")).andReturn(false);
        eventDispatcher.open();
        expect(eventDispatcher.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventDispatcher.handleEvent(XmlPullParser.START_DOCUMENT)).andReturn(true);
        expect(abortState.isAborted()).andReturn(false);

        expect(eventDispatcher.next()).andReturn(XmlPullParser.START_TAG);
        expect(eventDispatcher.handleEvent(XmlPullParser.START_TAG)).andReturn(true);
        expect(abortState.isAborted()).andReturn(false);

        expect(eventDispatcher.next()).andReturn(XmlPullParser.START_TAG);
        expect(eventDispatcher.handleEvent(XmlPullParser.START_TAG)).andReturn(true);
        expect(abortState.isAborted()).andReturn(false);
        expect(eventDispatcher.next()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventDispatcher.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);
        cacheXmlTagsToSql.close(true);
        expect(cacheXmlTagsToSql.getNumberOfCachesLoad()).andReturn(12);
        eventDispatcher.close();

        replayAll();
        GpxToCache gpxToCache = new GpxToCache(abortState, locAlreadyLoadedChecker,
                eventDispatcher, cacheXmlTagsToSql, importWakeLockProvider, null);

        assertEquals(12, gpxToCache.load("/path/to/foo.gpx", null));
        verifyAll();
    }
}
