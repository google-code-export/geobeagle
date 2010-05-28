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

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.Details;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsError;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsImpl;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsOpener;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReader;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReaderError;
import com.google.code.geobeagle.cachedetails.CacheDetailsLoader.DetailsReaderImpl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@PrepareForTest( {
        Activity.class, DetailsOpener.class, DetailsReaderImpl.class, File.class,
        CacheDetailsLoader.class
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
        byte[] buffer = {
                't', 'e', 's', 't'
        };
        assertEquals("test", new DetailsImpl(buffer).getString());
    }

    @Test
    public void testDetailsOpener() throws Exception {
        File file = createMock(File.class);
        FileInputStream fileInputStream = createMock(FileInputStream.class);
        DetailsReaderImpl detailsReaderImpl = createMock(DetailsReaderImpl.class);
        Activity activity = createMock(Activity.class);
        File detailsDir = createMock(File.class);

        expectNew(File.class, CacheDetailsLoader.SDCARD_DIR).andReturn(detailsDir);
        expect(detailsDir.isDirectory()).andReturn(true);
        expect(file.getAbsolutePath()).andReturn("/sdcard/foo.gpx");
        expectNew(FileInputStream.class, file).andReturn(fileInputStream);
        expect(file.length()).andReturn(27L);
        expectNew(DetailsReaderImpl.class, eq(activity), eq("/sdcard/foo.gpx"),
                eq(fileInputStream), isA(byte[].class)).andReturn(detailsReaderImpl);

        replayAll();
        assertEquals(detailsReaderImpl, new DetailsOpener(activity, fileDataVersionChecker)
                .open(file));
        verifyAll();
    }

    @Test
    public void testDetailsOpenerFileNotFound() throws Exception {
        File file = createMock(File.class);
        DetailsReaderError detailsReaderError = createMock(DetailsReaderError.class);
        Activity activity = createMock(Activity.class);
        File detailsDir = createMock(File.class);

        expectNew(File.class, CacheDetailsLoader.SDCARD_DIR).andReturn(detailsDir);
        expect(detailsDir.isDirectory()).andReturn(true);
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(file.getAbsolutePath()).andReturn("/sdcard/foo.html");
        expectNew(FileInputStream.class, file).andThrow(
                new FileNotFoundException("/sdcard/foo.html"));
        expectNew(DetailsReaderError.class, activity, R.string.error_opening_details_file,
                "/sdcard/foo.html").andReturn(detailsReaderError);

        replayAll();
        assertEquals(detailsReaderError, new DetailsOpener(activity, fileDataVersionChecker)
                .open(file));
        verifyAll();
    }

    @Test
    public void testDetailsReader() throws Exception {
        FileInputStream fileInputStream = createMock(FileInputStream.class);
        Activity activity = createMock(Activity.class);
        DetailsImpl details = createMock(DetailsImpl.class);

        byte[] buffer = new byte[10];
        expect(fileInputStream.read(buffer)).andReturn(10);
        fileInputStream.close();
        expectNew(DetailsImpl.class, buffer).andReturn(details);

        replayAll();
        assertEquals(details, new DetailsReaderImpl(activity, "/sdcard/foo.gpx", fileInputStream,
                buffer).read());
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
    public void testDetailsReaderIOError() throws Exception {
        FileInputStream fileInputStream = createMock(FileInputStream.class);
        Activity activity = createMock(Activity.class);
        DetailsError details = createMock(DetailsError.class);

        byte[] buffer = new byte[10];
        expect(fileInputStream.read(buffer)).andThrow(new IOException("/sdcard/foo.gpx"));
        expectNew(DetailsError.class, activity, R.string.error_reading_details_file,
                "/sdcard/foo.gpx").andReturn(details);

        replayAll();
        assertEquals(details, new DetailsReaderImpl(activity, "/sdcard/foo.gpx", fileInputStream,
                buffer).read());
        verifyAll();
    }

    @Test
    public void testLoad() throws Exception {
        DetailsOpener detailsOpener = createMock(DetailsOpener.class);
        File file = createMock(File.class);
        DetailsReader detailsReader = createMock(DetailsReader.class);
        Details details = createMock(Details.class);
        FilePathStrategy filePathStrategy = createMock(FilePathStrategy.class);

        expect(filePathStrategy.getPath("foo.gpx", "GC123")).andReturn(
                CacheDetailsLoader.DETAILS_DIR + "foo.gpx/GC123.html");
        expectNew(File.class, CacheDetailsLoader.DETAILS_DIR + "foo.gpx/GC123.html")
                .andReturn(file);
        expect(detailsOpener.open(file)).andReturn(detailsReader);
        expect(detailsReader.read()).andReturn(details);
        expect(details.getString()).andReturn("cache details");

        replayAll();
        assertEquals("cache details", new CacheDetailsLoader(detailsOpener, filePathStrategy).load(
                "foo.gpx", "GC123"));
        verifyAll();
    }
}
