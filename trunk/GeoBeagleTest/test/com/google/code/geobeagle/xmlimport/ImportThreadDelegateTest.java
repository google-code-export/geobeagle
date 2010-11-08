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
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.code.geobeagle.bcaching.preferences.BCachingStartTime;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FileDataVersionWriter;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.xmlimport.EventHelperDI.EventDispatcher;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilesAndZipFilesIter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpxAndZipFilenameFilter.class, File.class, GpxAndZipFiles.class, IGpxReader.class,
        Log.class
})
public class ImportThreadDelegateTest extends GeoBeagleTest {

    private FileDataVersionWriter fileDataVersionWriter;
    private OldCacheFilesCleaner oldCacheFilesCleaner;
    private FileDataVersionChecker fileDataVersionChecker;
    private DbFrontend dbFrontend;

    @Before
    public void setUp() {
        fileDataVersionWriter = PowerMock.createMock(FileDataVersionWriter.class);
        oldCacheFilesCleaner = PowerMock.createMock(OldCacheFilesCleaner.class);
        fileDataVersionChecker = PowerMock.createMock(FileDataVersionChecker.class);
        dbFrontend = PowerMock.createMock(DbFrontend.class);
    }

    @Test
    public void testHelperCleanup() {
        MessageHandlerInterface messageHandler = PowerMock.createMock(MessageHandler.class);

        messageHandler.loadComplete();

        PowerMock.replayAll();
        new ImportThreadHelper(null, messageHandler, null, null, null, null, null).cleanup();
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperEndNoFiles() {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        GeoBeagleEnvironment geoBeagleEnvironment = PowerMock
                .createMock(GeoBeagleEnvironment.class);
        EasyMock.expect(sharedPreferences.getString(BCachingModule.BCACHING_USERNAME, ""))
                .andReturn("");
        gpxLoader.end();
        EasyMock.expect(geoBeagleEnvironment.getImportFolder()).andReturn("/sdcard/download");

        PowerMock.replayAll();
        try {
            new ImportThreadHelper(gpxLoader, null, null, null, oldCacheFilesCleaner,
                    sharedPreferences, geoBeagleEnvironment)
                    .end();
            assertTrue("Expected ImportException, but didn't get one.", false);
        } catch (ImportException e) {
        }
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperProcessFileAndEnd() throws XmlPullParserException, IOException,
            ImportException {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        IGpxReader gpxFile = PowerMock.createMock(IGpxReader.class);
        Reader reader = PowerMock.createMock(Reader.class);
        EventHelperFactory eventHelperFactory = PowerMock.createMock(EventHelperFactory.class);
        EventDispatcher eventDispatcher = PowerMock.createMock(EventDispatcher.class);
        EventHandler eventHandler = PowerMock.createMock(EventHandler.class);

        EasyMock.expect(gpxFile.getFilename()).andReturn("foo.gpx");
        EasyMock.expect(eventHelperFactory.create()).andReturn(eventDispatcher);
        EasyMock.expect(gpxFile.open()).andReturn(reader);
        gpxLoader.open("foo.gpx", reader);
        EasyMock.expect(gpxLoader.load(eventDispatcher, eventHandler)).andReturn(true);
        gpxLoader.end();

        PowerMock.replayAll();
        ImportThreadHelper importThreadHelper = new ImportThreadHelper(gpxLoader, null,
                eventHelperFactory, eventHandler, null, null, null);
        assertTrue(importThreadHelper.processFile(gpxFile));
        importThreadHelper.end();
        PowerMock.verifyAll();
    }

    @Test
    public void testHelperStart() {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);

        oldCacheFilesCleaner.clean();
        gpxLoader.start();

        PowerMock.replayAll();
        new ImportThreadHelper(gpxLoader, null, null, null, oldCacheFilesCleaner, null, null)
                .start();
        PowerMock.verifyAll();
    }

    @Test
    public void testRun() throws FileNotFoundException, XmlPullParserException, IOException,
            ImportException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = PowerMock
                .createMock(GpxFilesAndZipFilesIter.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        IGpxReader iGpxFile = PowerMock.createMock(IGpxReader.class);
        BCachingStartTime bcachingStartTime = PowerMock.createMock(BCachingStartTime.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(false);
        bcachingStartTime.clearStartTime();
        expect(fileDataVersionChecker.needsUpdating()).andReturn(true);
        dbFrontend.forceUpdate();
        expect(gpxAndZipFiles.iterator()).andReturn(gpxFilesAndZipFilesIter);
        importThreadHelper.start();
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(true);
        expect(gpxFilesAndZipFilesIter.next()).andReturn(iGpxFile);
        expect(importThreadHelper.processFile(iGpxFile)).andReturn(true);
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(false);
        importThreadHelper.end();
        importThreadHelper.cleanup();
        fileDataVersionWriter.writeVersion();
        importThreadHelper.startBCachingImport();
        updateFlag.setUpdatesEnabled(true);

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null, fileDataVersionWriter,
                fileDataVersionChecker, dbFrontend, bcachingStartTime, updateFlag).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunAborted() throws FileNotFoundException, XmlPullParserException, IOException,
            ImportException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = PowerMock
                .createMock(GpxFilesAndZipFilesIter.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        IGpxReader iGpxFile = PowerMock.createMock(IGpxReader.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(false);
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andReturn(gpxFilesAndZipFilesIter);
        importThreadHelper.start();
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(true);
        expect(gpxFilesAndZipFilesIter.next()).andReturn(iGpxFile);
        expect(importThreadHelper.processFile(iGpxFile)).andReturn(false);
        importThreadHelper.cleanup();
        updateFlag.setUpdatesEnabled(true);

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null, fileDataVersionWriter,
                fileDataVersionChecker, dbFrontend, null, updateFlag).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunFileNotFound() {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        final FileNotFoundException e = new FileNotFoundException("foo.gpx");
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(false);
        errorDisplayer.displayError(R.string.error_opening_file, "foo.gpx");
        importThreadHelper.cleanup();
        updateFlag.setUpdatesEnabled(true);

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer, fileDataVersionWriter, null, null, null,
                updateFlag) {
            @Override
            protected void tryRun() throws IOException {
                throw e;
            }
        };
        importThreadDelegate.run();
        PowerMock.verifyAll();

    }

    @Test
    public void testRunXmlPullParserException() {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        final XmlPullParserException e = PowerMock.createMock(XmlPullParserException.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(false);
        EasyMock.expect(e.getMessage()).andReturn("xml exception");
        errorDisplayer.displayError(R.string.error_parsing_file, "xml exception");
        importThreadHelper.cleanup();
        updateFlag.setUpdatesEnabled(true);

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer, fileDataVersionWriter, null, null, null,
                updateFlag) {
            @Override
            protected void tryRun() throws XmlPullParserException {
                throw e;
            }
        };
        importThreadDelegate.run();
        PowerMock.verifyAll();

    }

    @Test
    public void testRunIOException() {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        final IOException e = PowerMock.createMock(IOException.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(false);
        EasyMock.expect(e.getMessage()).andReturn("problem reading file");
        importThreadHelper.cleanup();
        errorDisplayer.displayError(R.string.error_reading_file, "problem reading file");
        updateFlag.setUpdatesEnabled(true);

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer, fileDataVersionWriter, null, null, null,
                updateFlag) {
            @Override
            protected void tryRun() throws IOException {
                throw e;
            }
        };
        importThreadDelegate.run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunIteratorFail() throws ImportException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(false);

        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andThrow(
                new ImportException(R.string.error_cant_read_sd, "/sdcard-path"));
        errorDisplayer.displayError(R.string.error_cant_read_sd, "/sdcard-path");
        importThreadHelper.cleanup();
        updateFlag.setUpdatesEnabled(true);

        PowerMock.replayAll();
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxAndZipFiles,
                importThreadHelper, errorDisplayer, fileDataVersionWriter, fileDataVersionChecker,
                dbFrontend, null, updateFlag);
        importThreadDelegate.run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunNoFiles() throws IOException, ImportException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = PowerMock
                .createMock(GpxFilesAndZipFilesIter.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(true);
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andReturn(gpxFilesAndZipFilesIter);
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(false);
        importThreadHelper.start();
        fileDataVersionWriter.writeVersion();
        importThreadHelper.end();
        importThreadHelper.cleanup();
        importThreadHelper.startBCachingImport();
        updateFlag.setUpdatesEnabled(false);

        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null, fileDataVersionWriter,
                fileDataVersionChecker, dbFrontend, null, updateFlag).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunNoFilesToImport() throws FileNotFoundException, IOException, ImportException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        GpxFilesAndZipFilesIter gpxFilesAndZipFilesIter = PowerMock
                .createMock(GpxFilesAndZipFilesIter.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(false);
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andReturn(gpxFilesAndZipFilesIter);
        importThreadHelper.start();
        expect(gpxFilesAndZipFilesIter.hasNext()).andReturn(false);
        fileDataVersionWriter.writeVersion();
        importThreadHelper.end();
        importThreadHelper.cleanup();
        importThreadHelper.startBCachingImport();
        updateFlag.setUpdatesEnabled(true);


        PowerMock.replayAll();
        new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null, fileDataVersionWriter,
                fileDataVersionChecker, dbFrontend, null, updateFlag).run();
        PowerMock.verifyAll();
    }

    @Test
    public void testRunThrowRightAway() throws ImportException {
        GpxAndZipFiles gpxAndZipFiles = PowerMock.createMock(GpxAndZipFiles.class);
        ImportThreadHelper importThreadHelper = PowerMock.createMock(ImportThreadHelper.class);
        UpdateFlag updateFlag = PowerMock.createMock(UpdateFlag.class);

        updateFlag.setUpdatesEnabled(false);
        expect(fileDataVersionChecker.needsUpdating()).andReturn(false);
        expect(gpxAndZipFiles.iterator()).andThrow(new RuntimeException());
        importThreadHelper.cleanup();
        updateFlag.setUpdatesEnabled(true);

        PowerMock.replayAll();
        try {
            new ImportThreadDelegate(gpxAndZipFiles, importThreadHelper, null,
                    fileDataVersionWriter, fileDataVersionChecker, dbFrontend, null, updateFlag)
                    .run();
        } catch (Exception e) {

        }
        PowerMock.verifyAll();
    }
}
