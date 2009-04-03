
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.gpx.IGpxReader;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilesIter;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.gpx.zip.ZipFileOpener;
import com.google.code.geobeagle.gpx.zip.ZipFileOpener.ZipFileIter;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.ui.CacheListDelegate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpxAndZipFilenameFilter.class, File.class, GpxAndZipFiles.class, IGpxReader.class,
        GpxFileOpener.class, ZipFileIter.class, ZipFileOpener.class
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
    public void GpxFilesIterator() throws Exception {
        FilenameFilter filenameFilter = PowerMock.createMock(FilenameFilter.class);
        com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilesIterFactory gpxAndZipFilesIterFactory = PowerMock
                .createMock(com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilesIterFactory.class);
        GpxAndZipFilesIter gpxAndZipFilesIter = PowerMock.createMock(GpxAndZipFilesIter.class);
        File file = PowerMock.createMock(File.class);

        PowerMock.expectNew(File.class, GpxAndZipFiles.GPX_DIR).andReturn(file);
        String[] fileList = new String[] {
                "foo.gpx", "bar.gpx"
        };
        expect(file.list(filenameFilter)).andReturn(fileList);
        PowerMock.expectNew(GpxAndZipFilesIter.class, fileList, gpxAndZipFilesIterFactory)
                .andReturn(gpxAndZipFilesIter);

        PowerMock.replayAll();
        new GpxAndZipFiles(filenameFilter, gpxAndZipFilesIterFactory).iterator();
        PowerMock.verifyAll();
    }

    @Test
    public void testZippedGpxFileIterIterator() throws Exception {
        FileInputStream fileInputStream = PowerMock.createMock(FileInputStream.class);
        BufferedInputStream bufferedInputStream = PowerMock.createMock(BufferedInputStream.class);
        ZipInputStream zipInputStream = PowerMock.createMock(ZipInputStream.class);
        ZipEntry zipEntry = PowerMock.createMock(ZipEntry.class);

        PowerMock.expectNew(FileInputStream.class, "foo.zip").andReturn(fileInputStream);
        PowerMock.expectNew(BufferedInputStream.class, fileInputStream).andReturn(
                bufferedInputStream);
        PowerMock.expectNew(ZipInputStream.class, bufferedInputStream).andReturn(zipInputStream);
        expect(zipInputStream.getNextEntry()).andReturn(zipEntry);

    }

}
