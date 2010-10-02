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
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParserException;

import android.database.sqlite.SQLiteException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

@RunWith(PowerMockRunner.class)
public class GpxLoaderTest {

    private Provider<ImportWakeLock> wakeLockProvider;
    private ImportWakeLock importWakeLock;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        wakeLockProvider = PowerMock.createMock(Provider.class);
        importWakeLock = PowerMock.createMock(ImportWakeLock.class);
    }

    private <T> void loadRaiseAndDisplayCustomMessage(Class<T> exceptionClass, int errorResource)
            throws XmlPullParserException, IOException, CancelException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);
        GpxToCache gpxToCache = PowerMock.createMock(GpxToCache.class);
        Throwable e = (Throwable)PowerMock.createMock(exceptionClass);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        expect(wakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(15000);
        expect(gpxToCache.load(eventHelper, null, importCacheActions)).andStubReturn(false);
        expectLastCall().andThrow(e);
        expect(gpxToCache.getSource()).andReturn("foo.gpx");
        expect(e.fillInStackTrace()).andReturn(e);
        expect(e.getMessage()).andReturn("line 'blah'");
        errorDisplayer.displayError(errorResource, "foo.gpx: line 'blah'");
        importCacheActions.close(false);

        PowerMock.replayAll();
        assertFalse(new GpxLoader(importCacheActions, errorDisplayer, gpxToCache, wakeLockProvider)
                .load(eventHelper, null));
        PowerMock.verifyAll();
    }

    private <T> void loadRaiseAndDisplayExceptionMessage(int errorMessage, Class<T> exceptionClass)
            throws XmlPullParserException, IOException, CancelException {
        ImportCacheActions importCacheActions = createMock(ImportCacheActions.class);
        GpxToCache gpxToCache = PowerMock.createMock(GpxToCache.class);
        Throwable e = (Throwable)PowerMock.createMock(exceptionClass);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        expect(wakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(15000);
        gpxToCache.load(eventHelper, null, importCacheActions);
        expectLastCall().andThrow(e);
        expect(e.getMessage()).andReturn("a problem of some sort");
        expect(e.fillInStackTrace()).andReturn(e);
        expect(gpxToCache.getSource()).andReturn("/sdcard/foo.gpx");
        errorDisplayer.displayError(errorMessage, "/sdcard/foo.gpx: a problem of some sort");
        importCacheActions.close(false);

        PowerMock.replayAll();
        assertFalse(new GpxLoader(importCacheActions, errorDisplayer, gpxToCache, wakeLockProvider)
                .load(
                eventHelper, null));
        PowerMock.verifyAll();
    }

    private <T> void loadRaiseAndDisplayNothing(Class<T> exceptionClass)
            throws XmlPullParserException, IOException, CancelException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);
        GpxToCache gpxToCache = PowerMock.createMock(GpxToCache.class);
        Throwable e = (Throwable)PowerMock.createMock(exceptionClass);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        expect(wakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(15000);
        expect(gpxToCache.load(eventHelper, null, importCacheActions)).andStubReturn(false);
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        importCacheActions.close(false);

        PowerMock.replayAll();
        assertFalse(new GpxLoader(importCacheActions, errorDisplayer, gpxToCache, wakeLockProvider)
                .load(eventHelper, null));
        PowerMock.verifyAll();
    }

    @Test
    public void testAbortLoad() {
        GpxToCache gpxToCache = PowerMock.createMock(GpxToCache.class);

        gpxToCache.abort();

        PowerMock.replayAll();
        new GpxLoader(null, null, gpxToCache, null).abort();
        PowerMock.verifyAll();
    }

    @Test
    public void testEnd() {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);

        importCacheActions.end();

        PowerMock.replayAll();
        new GpxLoader(importCacheActions, null, null, null).end();
        PowerMock.verifyAll();
    }

    @Test
    public void testLoad() throws XmlPullParserException, IOException, CancelException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);
        GpxToCache gpxToCache = PowerMock.createMock(GpxToCache.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        expect(wakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(15000);
        expect(gpxToCache.load(eventHelper, null, importCacheActions)).andReturn(false);
        importCacheActions.close(true);

        PowerMock.replayAll();
        assertTrue(new GpxLoader(importCacheActions, null, gpxToCache, wakeLockProvider).load(
                eventHelper,
                null));
        PowerMock.verifyAll();
    }

    @Test
    public void testLoadAlreadyLoaded() throws XmlPullParserException, IOException, CancelException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);
        GpxToCache gpxToCache = PowerMock.createMock(GpxToCache.class);
        EventHelper eventHelper = PowerMock.createMock(EventHelper.class);

        expect(wakeLockProvider.get()).andReturn(importWakeLock);
        importWakeLock.acquire(15000);
        expect(gpxToCache.load(eventHelper, null, importCacheActions)).andReturn(true);
        importCacheActions.close(false);

        PowerMock.replayAll();
        assertTrue(new GpxLoader(importCacheActions, null, gpxToCache, wakeLockProvider).load(
                eventHelper,
                null));
        PowerMock.verifyAll();
    }

    @Test
    public void testLoadCancelException() throws XmlPullParserException, IOException,
            CancelException {
        loadRaiseAndDisplayNothing(CancelException.class);
    }

    @Test
    public void testLoadFileNotFoundException() throws XmlPullParserException, IOException,
            CancelException {
        loadRaiseAndDisplayCustomMessage(FileNotFoundException.class, R.string.file_not_found);
    }

    @Test
    public void testLoadIOException() throws XmlPullParserException, IOException, CancelException {
        loadRaiseAndDisplayCustomMessage(IOException.class, R.string.error_reading_file);
    }

    @Test
    public void testLoadPullParserException() throws XmlPullParserException, IOException,
            CancelException {
        loadRaiseAndDisplayExceptionMessage(R.string.error_parsing_file,
                XmlPullParserException.class);
    }

    @Test
    public void testLoadSqliteException() throws XmlPullParserException, IOException,
            CancelException {
        loadRaiseAndDisplayExceptionMessage(R.string.error_writing_cache, SQLiteException.class);
    }

    @Test
    public void testOpen() throws XmlPullParserException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);
        GpxToCache gpxToCache = PowerMock.createMock(GpxToCache.class);
        Reader reader = PowerMock.createMock(Reader.class);

        gpxToCache.open("/sdcard/foo.gpx", "foo.gpx", reader);
        importCacheActions.open("foo.gpx");

        PowerMock.replayAll();
        new GpxLoader(importCacheActions, null, gpxToCache, null).open("/sdcard/foo.gpx", reader);
        PowerMock.verifyAll();
    }

    @Test
    public void testStart() {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);

        importCacheActions.start();

        PowerMock.replayAll();
        new GpxLoader(importCacheActions, null, null, null).start();
        PowerMock.verifyAll();
    }
}
