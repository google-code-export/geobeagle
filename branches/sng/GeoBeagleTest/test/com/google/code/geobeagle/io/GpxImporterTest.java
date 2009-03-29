
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporter.ImportThreadDelegate;
import com.google.code.geobeagle.io.GpxImporterDI.GpxFilenameFactory;
import com.google.code.geobeagle.io.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class GpxImporterTest {

    private <T> void importThreadDelegateRunAndThrow(Class<T> exceptionClass, int errorMessage)
            throws FileNotFoundException, XmlPullParserException, IOException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        Throwable e = (Throwable)createMock(exceptionClass);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        GpxFilenameFactory gpxFilenameFactory = createMock(GpxFilenameFactory.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);

        expect(gpxFilenameFactory.getFilenames()).andReturn(new String[] {
            "foo.gpx"
        });
        gpxLoader.start();
        gpxLoader.open("/sdcard/foo.gpx");
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        errorDisplayer.displayError(errorMessage, "/sdcard/foo.gpx");
        messageHandler.loadComplete();

        replay(errorDisplayer);
        replay(e);
        replay(gpxLoader);
        replay(gpxFilenameFactory);
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxLoader,
                messageHandler, errorDisplayer, gpxFilenameFactory);
        importThreadDelegate.run();
        verify(e);
        verify(gpxLoader);
        verify(errorDisplayer);
        verify(gpxFilenameFactory);
    }

    @Test
    public void testFilenameFilter() {
        assertFalse(GpxImporter.filenameFilter.accept(null, ".appledetritus010.gpx"));
        assertFalse(GpxImporter.filenameFilter.accept(null, "foo.bar"));
        assertTrue(GpxImporter.filenameFilter.accept(null, "01243.gpx"));
    }

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
        GpxImporter gpxImporter = new GpxImporter(gpxLoader, null, null, null, importThreadWrapper,
                messageHandler, null, null);
        gpxImporter.abort();
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
    public void testImportThreadDelegateRun() throws FileNotFoundException, XmlPullParserException,
            IOException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        GpxFilenameFactory gpxFilenameFactory = createMock(GpxFilenameFactory.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);

        expect(gpxFilenameFactory.getFilenames()).andReturn(new String[] {
            "foo.gpx"
        });
        gpxLoader.open("/sdcard/foo.gpx");
        gpxLoader.start();
        expect(gpxLoader.load()).andReturn(true);
        gpxLoader.end();
        messageHandler.loadComplete();

        replay(gpxLoader);
        replay(messageHandler);
        replay(gpxFilenameFactory);
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxLoader,
                messageHandler, null, gpxFilenameFactory);
        importThreadDelegate.run();
        verify(gpxLoader);
        verify(messageHandler);
        verify(gpxFilenameFactory);
    }

    public void testImportThreadDelegateRunAborted() throws FileNotFoundException,
            XmlPullParserException, IOException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        GpxFilenameFactory gpxFilenameFactory = createMock(GpxFilenameFactory.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);

        expect(gpxFilenameFactory.getFilenames()).andReturn(new String[] {
            "foo.gpx"
        });
        gpxLoader.open("/sdcard/foo.gpx");
        gpxLoader.start();
        expect(gpxLoader.load()).andReturn(false);
        messageHandler.loadComplete();

        replay(gpxLoader);
        replay(messageHandler);
        replay(gpxFilenameFactory);
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxLoader,
                messageHandler, null, gpxFilenameFactory);
        importThreadDelegate.run();
        verify(gpxLoader);
        verify(messageHandler);
        verify(gpxFilenameFactory);
    }

    @Test
    public void testImportThreadDelegateRunAndThrowRandomException() throws FileNotFoundException,
            XmlPullParserException, IOException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        Exception e = createMock(RuntimeException.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        GpxFilenameFactory gpxFilenameFactory = createMock(GpxFilenameFactory.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);

        expect(gpxFilenameFactory.getFilenames()).andReturn(new String[] {
            "foo.gpx"
        });
        gpxLoader.start();
        gpxLoader.open("/sdcard/foo.gpx");
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        errorDisplayer.displayErrorAndStack(e);
        messageHandler.loadComplete();

        replay(gpxFilenameFactory);
        replay(errorDisplayer);
        replay(e);
        replay(gpxLoader);
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxLoader,
                messageHandler, errorDisplayer, gpxFilenameFactory);
        importThreadDelegate.run();
        verify(e);
        verify(gpxLoader);
        verify(errorDisplayer);
        verify(gpxFilenameFactory);
    }

    @Test
    public void testImportThreadDelegateRunFileNotFound() throws FileNotFoundException,
            XmlPullParserException, IOException {
        importThreadDelegateRunAndThrow(FileNotFoundException.class, R.string.error_opening_file);
    }

    @Test
    public void testImportThreadDelegateRunNoFilesToImport() throws FileNotFoundException,
            XmlPullParserException, IOException {
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        GpxFilenameFactory gpxFilenameFactory = createMock(GpxFilenameFactory.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);

        expect(gpxFilenameFactory.getFilenames()).andReturn(new String[] {});
        errorDisplayer.displayError(R.string.error_no_gpx_files);

        replay(errorDisplayer);
        replay(gpxFilenameFactory);
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(null, messageHandler,
                errorDisplayer, gpxFilenameFactory);
        importThreadDelegate.run();
        verify(errorDisplayer);
        verify(gpxFilenameFactory);
    }
}
