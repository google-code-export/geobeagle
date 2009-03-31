
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporter.GpxFile;
import com.google.code.geobeagle.io.GpxImporter.GpxFiles;
import com.google.code.geobeagle.io.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.ui.CacheListDelegate;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpxImporter.GpxFilenameFactory.class, File.class, GpxFiles.class, GpxFile.class
})
public class GpxImporterTest {

    @Test
    public void testAbort() throws InterruptedException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);

        gpxLoader.abort();
        expect(importThreadWrapper.isAlive()).andReturn(false);
        messageHandler.abortLoad();

        replay(messageHandler);
        replay(gpxLoader);
        replay(importThreadWrapper);
        new GpxImporter(gpxLoader, null, null, null, importThreadWrapper, messageHandler, null,
                null).abort();
        verify(gpxLoader);
        verify(importThreadWrapper);
        verify(messageHandler);
    }

    @Test
    public void testAbortThreadAlive() throws InterruptedException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        ToastFactory toastFactory = createMock(ToastFactory.class);
        ListActivity listActivity = createMock(ListActivity.class);

        gpxLoader.abort();
        expect(importThreadWrapper.isAlive()).andReturn(true);
        messageHandler.abortLoad();
        importThreadWrapper.join();
        sqliteWrapper.close();
        toastFactory.showToast(listActivity, R.string.import_canceled, Toast.LENGTH_SHORT);

        replay(messageHandler);
        replay(gpxLoader);
        replay(importThreadWrapper);
        replay(sqliteWrapper);
        replay(toastFactory);
        GpxImporter gpxImporter = new GpxImporter(gpxLoader, null, sqliteWrapper, listActivity,
                importThreadWrapper, messageHandler, null, toastFactory);
        gpxImporter.abort();
        verify(gpxLoader);
        verify(importThreadWrapper);
        verify(messageHandler);
        verify(sqliteWrapper);
        verify(toastFactory);
    }

    @Test
    public void testFilenameFilter() {
        assertFalse(GpxImporter.filenameFilter.accept(null, ".appledetritus010.gpx"));
        assertFalse(GpxImporter.filenameFilter.accept(null, "foo.bar"));
        assertTrue(GpxImporter.filenameFilter.accept(null, "01243.gpx"));
        // assertTrue(GpxImporter.filenameFilter.accept(null, "567.zip"));
    }

    @Test
    public void testGpxFilenameFactory() throws Exception {
        FilenameFilter filenameFilter = PowerMock.createMock(FilenameFilter.class);
        File file = PowerMock.createMock(File.class);

        String files[] = new String[] {
                "foo.gpx", "bar.gpx"
        };
        PowerMock.expectNew(File.class, "/sdcard").andReturn(file);
        EasyMock.expect(file.list(filenameFilter)).andReturn(files);

        PowerMock.replayAll();
        GpxImporter.GpxFilenameFactory gpxFilenameFactory = new GpxImporter.GpxFilenameFactory(
                filenameFilter);
        gpxFilenameFactory.getFilenames();
        PowerMock.verifyAll();
    }

    @Test
    public void testImportGpxs() throws FileNotFoundException, XmlPullParserException, IOException {
        CacheListDelegate cacheListDelegate = createMock(CacheListDelegate.class);
        Database database = createMock(Database.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = createMock(ImportThreadWrapper.class);

        sqliteWrapper.openReadableDatabase(database);
        importThreadWrapper.open(cacheListDelegate, gpxLoader, null);
        importThreadWrapper.start();

        replay(database);
        replay(importThreadWrapper);
        replay(cacheListDelegate);
        GpxImporter gpxImporter = new GpxImporter(gpxLoader, database, sqliteWrapper, null,
                importThreadWrapper, null, null, null);
        gpxImporter.importGpxs(cacheListDelegate);
        verify(cacheListDelegate);
        verify(importThreadWrapper);
        verify(database);
    }



    @Test
    public void testReaderFactory() throws Exception {
        File file = PowerMock.createMock(File.class);
        FilenameFilter filenameFilter = PowerMock.createMock(FilenameFilter.class);
        FileReader fileReader = PowerMock.createMock(FileReader.class);

        PowerMock.expectNew(File.class, "/sdcard").andReturn(file);
        PowerMock.expectNew(FileReader.class, "/sdcard/foo.gpx").andReturn(fileReader);
        expect(file.list(filenameFilter)).andReturn(new String[] {
            "foo.gpx"
        });

        PowerMock.replayAll();
        GpxFiles gpxFiles = new GpxFiles(filenameFilter);
        int nFiles = 0;
        for (GpxFile gpxFile : gpxFiles) {
            nFiles++;
            assertEquals("foo.gpx", gpxFile.getFilename());
            assertEquals(fileReader, gpxFile.open());
        }
        assertEquals(1, nFiles);
        PowerMock.verifyAll();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReaderFactoryRemove() throws Exception {
        File file = PowerMock.createMock(File.class);
        FilenameFilter filenameFilter = PowerMock.createMock(FilenameFilter.class);
        FileReader fileReader = PowerMock.createMock(FileReader.class);

        PowerMock.expectNew(File.class, "/sdcard").andReturn(file);
        PowerMock.expectNew(FileReader.class, "/sdcard/foo.gpx").andReturn(fileReader);
        expect(file.list(filenameFilter)).andReturn(new String[] {
            "foo.gpx"
        });

        PowerMock.replayAll();
        GpxFiles gpxFiles = new GpxFiles(filenameFilter);
        gpxFiles.iterator().remove();
        PowerMock.verifyAll();
    }
}
