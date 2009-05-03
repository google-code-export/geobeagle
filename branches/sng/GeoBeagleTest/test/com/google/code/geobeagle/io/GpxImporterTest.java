
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.gpx.IGpxReader;
import com.google.code.geobeagle.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.gpx.zip.ZipFileOpener;
import com.google.code.geobeagle.gpx.zip.ZipFileOpener.ZipFileIter;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.ui.cachelist.MenuActionRefresh;

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
import java.io.IOException;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GpxAndZipFilenameFilter.class, File.class, GpxAndZipFiles.class, IGpxReader.class,
        GpxFileOpener.class, ZipFileIter.class, ZipFileOpener.class
})
public class GpxImporterTest {

    @Test
    public void testAbort() throws InterruptedException {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);

        gpxLoader.abort();
        expect(importThreadWrapper.isAlive()).andReturn(false);
        messageHandler.abortLoad();

        PowerMock.replayAll();
        new GpxImporter(gpxLoader, null, null, null, importThreadWrapper, messageHandler, null,
                null, null).abort();
        PowerMock.verifyAll();
    }

    @Test
    public void testAbortThreadAlive() throws InterruptedException {
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        ToastFactory toastFactory = PowerMock.createMock(ToastFactory.class);
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);

        gpxLoader.abort();
        expect(importThreadWrapper.isAlive()).andReturn(true);
        messageHandler.abortLoad();
        importThreadWrapper.join();
        sqliteWrapper.close();
        toastFactory.showToast(listActivity, R.string.import_canceled, Toast.LENGTH_SHORT);

        PowerMock.replayAll();
        new GpxImporter(gpxLoader, null, sqliteWrapper, listActivity, importThreadWrapper,
                messageHandler, toastFactory, null, null).abort();
        PowerMock.verifyAll();
    }

    @Test
    public void testImportGpxs() throws FileNotFoundException, XmlPullParserException, IOException {
        MenuActionRefresh menuActionRefresh = PowerMock.createMock(MenuActionRefresh.class);
        Database database = PowerMock.createMock(Database.class);
        SQLiteWrapper sqliteWrapper = PowerMock.createMock(SQLiteWrapper.class);
        GpxLoader gpxLoader = PowerMock.createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        EventHandlers eventHandlers = PowerMock.createMock(EventHandlers.class);

        sqliteWrapper.openReadableDatabase(database);
        importThreadWrapper.open(menuActionRefresh, gpxLoader, eventHandlers, null);
        importThreadWrapper.start();

        PowerMock.replayAll();
        new GpxImporter(gpxLoader, database, sqliteWrapper, null, importThreadWrapper, null, null,
                eventHandlers, null).importGpxs(menuActionRefresh);
        PowerMock.verifyAll();
    }
}
