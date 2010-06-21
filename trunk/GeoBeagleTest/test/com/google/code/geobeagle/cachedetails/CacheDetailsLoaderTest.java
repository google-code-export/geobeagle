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

package com.google.code.geobeagle.cachedetails;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.Details;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsError;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsImpl;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsOpener;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReader;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReaderError;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReaderImpl;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.XmlPullParserWrapper;

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

@PrepareForTest( {
        Activity.class, DetailsOpener.class, DetailsReaderImpl.class, Environment.class,
        File.class, CacheDetailsLoader.class, XmlPullParserException.class
})
@RunWith(PowerMockRunner.class)
public class CacheDetailsLoaderTest {

    private FileDataVersionChecker fileDataVersionChecker;

    @Before
    public void setUp() {
        fileDataVersionChecker = createMock(FileDataVersionChecker.class);
    }

    @Test
    public void testDetailsError() {
        Activity activity = createMock(Activity.class);

        expect(activity.getString(57, "foo.gpx")).andReturn("error msg");

        replayAll();
        assertEquals("error msg", new DetailsError(activity, 57, "foo.gpx").getString());
        verifyAll();
    }

    @Test
    public void testDetailsImpl() {
        assertEquals("test", new DetailsImpl("test").getString());
    }

    @Test
    public void testDetailsOpener() throws Exception {
        File file = createMock(File.class);
        DetailsReaderImpl detailsReaderImpl = createMock(DetailsReaderImpl.class);
        Activity activity = createMock(Activity.class);
        FileReader fileReader = createMock(FileReader.class);
        EventHelper eventHelper = createMock(EventHelper.class);
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        StringWriterWrapper stringWriterWrapper = createMock(StringWriterWrapper.class);
        BufferedReader bufferedReader = createMock(BufferedReader.class);

        PowerMock.mockStatic(Environment.class);
        expect(Environment.getExternalStorageState()).andReturn(Environment.MEDIA_MOUNTED);
        expect(file.getAbsolutePath()).andReturn("/sdcard/foo.gpx");
        expectNew(FileReader.class, "/sdcard/foo.gpx").andReturn(fileReader);
        expectNew(BufferedReader.class, fileReader).andReturn(bufferedReader);
        expectNew(DetailsReaderImpl.class, eq(activity), eq(bufferedReader), eq("/sdcard/foo.gpx"),
                eq(eventHelper), eq(xmlPullParser), eq(stringWriterWrapper)).andReturn(
                detailsReaderImpl);

        replayAll();
        assertEquals(detailsReaderImpl, new DetailsOpener(activity, fileDataVersionChecker,
                eventHelper, xmlPullParser, stringWriterWrapper).open(file));
        verifyAll();
    }

    @Test
    public void testDetailsOpenerFileNotFound() throws Exception {
        File file = createMock(File.class);
        DetailsReaderError detailsReaderError = createMock(DetailsReaderError.class);
        Activity activity = createMock(Activity.class);

        PowerMock.mockStatic(Environment.class);
        expect(Environment.getExternalStorageState()).andReturn(Environment.MEDIA_MOUNTED);
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(file.getAbsolutePath()).andReturn("/sdcard/foo.gpx");
        expectNew(FileReader.class, "/sdcard/foo.gpx").andThrow(
                new FileNotFoundException("/sdcard/foo.gpx"));
        expectNew(DetailsReaderError.class, activity, R.string.error_opening_details_file,
                "/sdcard/foo.gpx").andReturn(detailsReaderError);

        replayAll();
        assertEquals(detailsReaderError, new DetailsOpener(activity, fileDataVersionChecker, null,
                null, null).open(file));
        verifyAll();
    }

    @Test
    public void testDetailsReader() throws Exception {
        Activity activity = createMock(Activity.class);
        DetailsImpl details = createMock(DetailsImpl.class);
        XmlPullParserWrapper xmlPullParserWrapper = createMock(XmlPullParserWrapper.class);
        Reader reader = createMock(Reader.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        eventHelper.open("/sdcard/foo.gpx");
        StringWriterWrapper stringWriterWrapper = new StringWriterWrapper();
        stringWriterWrapper.write("DETAILS");
        xmlPullParserWrapper.open("/sdcard/foo.gpx", reader);
        expect(xmlPullParserWrapper.getEventType()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventHelper.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);
        expectNew(DetailsImpl.class, "DETAILS").andReturn(details);

        replayAll();
        assertEquals(details, new DetailsReaderImpl(activity, reader, "/sdcard/foo.gpx",
                eventHelper, xmlPullParserWrapper, stringWriterWrapper).read());
        verifyAll();
    }

    @Test
    public void testDetailsReaderFileNotFound() throws Exception {
        Activity activity = createMock(Activity.class);
        DetailsError detailsError = createMock(DetailsError.class);

        expectNew(DetailsError.class, activity, R.string.error_opening_details_file,
                "/sdcard/foo.html").andReturn(detailsError);

        replayAll();
        assertEquals(detailsError, new DetailsReaderError(activity,
                R.string.error_opening_details_file, "/sdcard/foo.html").read());
        verifyAll();
    }

    @Test
    public void testDetailsReaderXmlPullParserException() throws Exception {
        Activity activity = createMock(Activity.class);
        DetailsError details = createMock(DetailsError.class);
        XmlPullParserWrapper xmlPullParser = createMock(XmlPullParserWrapper.class);
        Reader reader = createMock(Reader.class);
        XmlPullParserException xmlPullParserException = createMock(XmlPullParserException.class);
        StringWriterWrapper stringWriterWrapper = createMock(StringWriterWrapper.class);
        EventHelper eventHelper = createMock(EventHelper.class);

        eventHelper.open("/sdcard/foo.gpx");
        xmlPullParser.open("/sdcard/foo.gpx", reader);
        expectLastCall().andThrow(xmlPullParserException);
        expect(xmlPullParserException.fillInStackTrace()).andStubReturn(xmlPullParserException);
        expectNew(DetailsError.class, activity, R.string.error_reading_details_file,
                "/sdcard/foo.gpx").andReturn(details);

        replayAll();
        assertEquals(details, new DetailsReaderImpl(activity, reader, "/sdcard/foo.gpx", eventHelper,
                xmlPullParser, stringWriterWrapper).read());
        verifyAll();
    }

    @Test
    public void testLoad() throws Exception {
        DetailsOpener detailsOpener = createMock(DetailsOpener.class);
        File file = createMock(File.class);
        DetailsReader detailsReader = createMock(DetailsReader.class);
        Details details = createMock(Details.class);
        FilePathStrategy filePathStrategy = createMock(FilePathStrategy.class);

        expect(filePathStrategy.getPath("foo.gpx", "GC123", "gpx")).andReturn(
                "/sdcard/details/foo.gpx/GC123.html");
        expectNew(File.class, "/sdcard/details/foo.gpx/GC123.html").andReturn(file);
        expect(detailsOpener.open(file)).andReturn(detailsReader);
        expect(detailsReader.read()).andReturn(details);
        expect(details.getString()).andReturn("cache details");

        replayAll();
        assertEquals("cache details", new CacheDetailsLoader(detailsOpener, filePathStrategy).load(
                "foo.gpx", "GC123"));
        verifyAll();
    }
}
