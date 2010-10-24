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

package com.google.code.geobeagle.cacheloader;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.DetailsDatabaseReader;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.code.geobeagle.cachedetails.reader.DetailsReader;
import com.google.code.geobeagle.xmlimport.CacheTagsToDetails;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.XmlPullParserWrapper;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

@PrepareForTest({
        Activity.class, DetailsOpener.class, DetailsReader.class, Environment.class,
        File.class, CacheDetailsLoader.class, XmlPullParserException.class
})
@RunWith(PowerMockRunner.class)
public class CacheDetailsLoaderTest {

    private Activity activity;
    private BufferedReader bufferedReader;
    private CacheTagsToDetails cacheTagsToDetails;
    private DetailsOpener detailsOpener;
    private DetailsReader detailsReader;
    private EventHandlerGpx eventHandler;
    private EventHelper eventHelper;
    private File file;
    private FileDataVersionChecker fileDataVersionChecker;
    private FilePathStrategy filePathStrategy;
    private FileReader fileReader;
    private Reader reader;
    private StringWriterWrapper stringWriterWrapper;
    private XmlPullParserWrapper xmlPullParser;
    private XmlPullParserException xmlPullParserException;
    private XmlPullParserWrapper xmlPullParserWrapper;
    private DetailsDatabaseReader detailsDatabaseReader;

    @Before
    public void setUp() {
        fileDataVersionChecker = createMock(FileDataVersionChecker.class);
        file = createMock(File.class);
        eventHandler = createMock(EventHandlerGpx.class);
        cacheTagsToDetails = createMock(CacheTagsToDetails.class);
        detailsReader = createMock(DetailsReader.class);
        activity = createMock(Activity.class);
        fileReader = createMock(FileReader.class);
        eventHelper = createMock(EventHelper.class);
        xmlPullParser = createMock(XmlPullParserWrapper.class);
        stringWriterWrapper = createMock(StringWriterWrapper.class);
        bufferedReader = createMock(BufferedReader.class);
        xmlPullParserWrapper = createMock(XmlPullParserWrapper.class);
        reader = createMock(Reader.class);
        xmlPullParserException = createMock(XmlPullParserException.class);
        detailsOpener = createMock(DetailsOpener.class);
        filePathStrategy = createMock(FilePathStrategy.class);
        detailsDatabaseReader = createMock(DetailsDatabaseReader.class);
    }

    @Test
    public void testDetailsOpener() throws Exception {
        PowerMock.mockStatic(Environment.class);
        expect(Environment.getExternalStorageState()).andReturn(Environment.MEDIA_MOUNTED);
        expect(file.getAbsolutePath()).andReturn("/sdcard/foo.gpx");
        expectNew(FileReader.class, "/sdcard/foo.gpx").andReturn(fileReader);
        expectNew(BufferedReader.class, fileReader).andReturn(bufferedReader);
        expectNew(DetailsReader.class, eq(activity), EasyMock.anyObject(), eq("/sdcard/foo.gpx"),
                eq(eventHelper), eq(eventHandler), eq(xmlPullParser), eq(stringWriterWrapper))
                .andReturn(detailsReader);
        expect(detailsDatabaseReader.read("GC123")).andReturn("the details");

        replayAll();
        assertEquals(detailsReader, new DetailsOpener(activity, fileDataVersionChecker,
                eventHelper, eventHandler, xmlPullParser, stringWriterWrapper,
                detailsDatabaseReader).open(file, "GC123"));
        verifyAll();
    }

    @Test
    public void testDetailsOpenerFileNotFound() throws Exception {
        PowerMock.mockStatic(Environment.class);
        expect(Environment.getExternalStorageState()).andReturn(Environment.MEDIA_MOUNTED);
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(file.getAbsolutePath()).andReturn("/sdcard/foo.gpx");
        expectNew(FileReader.class, "/sdcard/foo.gpx").andThrow(
                new FileNotFoundException("/sdcard/foo.gpx"));
        expect(detailsDatabaseReader.read("GC123")).andReturn("the details");
        replayAll();
        try {
            new DetailsOpener(activity, fileDataVersionChecker, null, null, null, null,
                    detailsDatabaseReader).open(file, "GC123");
            fail("expected exception");
        } catch (CacheLoaderException cle) {
            assertEquals(R.string.error_opening_details_file, cle.getError());
            assertEquals("/sdcard/foo.gpx", cle.getArgs()[0]);
        }
        verifyAll();
    }

    @Test
    public void testDetailsReader() throws Exception {
        eventHelper.open("/sdcard/foo.gpx", eventHandler);
        StringWriterWrapper stringWriterWrapper = new StringWriterWrapper();
        stringWriterWrapper.write("DETAILS");
        xmlPullParserWrapper.open("/sdcard/foo.gpx", reader);
        expect(xmlPullParserWrapper.getEventType()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(
                eventHelper.handleEvent(XmlPullParser.END_DOCUMENT, eventHandler,
                        cacheTagsToDetails)).andReturn(true);

        replayAll();
        assertEquals("DETAILS",
                new DetailsReader(activity, reader, "/sdcard/foo.gpx", eventHelper,
                        eventHandler, xmlPullParserWrapper, stringWriterWrapper)
                        .read(cacheTagsToDetails));
        verifyAll();
    }

    @Test
    public void testDetailsReaderXmlPullParserException() throws Exception {
        expect(activity.getString(R.string.error_reading_details_file, "/sdcard/foo.gpx"))
                .andReturn("Can't open file /sdcard/foo.gpx");
        eventHelper.open("/sdcard/foo.gpx", eventHandler);
        xmlPullParser.open("/sdcard/foo.gpx", reader);
        expectLastCall().andThrow(xmlPullParserException);
        expect(xmlPullParserException.fillInStackTrace()).andStubReturn(xmlPullParserException);

        replayAll();
        assertEquals("Can't open file /sdcard/foo.gpx",
                new DetailsReader(activity, reader, "/sdcard/foo.gpx", eventHelper,
                        eventHandler, xmlPullParser, stringWriterWrapper).read(cacheTagsToDetails));
        verifyAll();
    }

    @Test
    public void testLoad() throws Exception {
        expect(filePathStrategy.getPath("foo.gpx", "GC123", "gpx")).andReturn(
                "/sdcard/details/foo.gpx/GC123.html");
        expectNew(File.class, "/sdcard/details/foo.gpx/GC123.html").andReturn(file);
        expect(detailsOpener.open(file, "GC123")).andReturn(detailsReader);
        expect(detailsReader.read(cacheTagsToDetails)).andReturn("cache details");
        // stringWriterWrapper.open();

        replayAll();
        assertEquals("cache details", new CacheDetailsLoader(detailsOpener, filePathStrategy,
                cacheTagsToDetails).load("foo.gpx", "GC123"));
        verifyAll();
    }
}
