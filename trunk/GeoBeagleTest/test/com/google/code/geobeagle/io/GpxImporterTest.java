
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporter.ImportThreadDelegate;
import com.google.code.geobeagle.io.di.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.io.di.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.di.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

public class GpxImporterTest extends TestCase {

    public void testAbort() throws InterruptedException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);

        gpxLoader.abortLoad();
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

    public void testAbortThreadAlive() throws InterruptedException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        ToastFactory toastFactory = createMock(ToastFactory.class);
        ListActivity listActivity = createMock(ListActivity.class);

        gpxLoader.abortLoad();
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

    public void testImportThreadDelegateRun() throws FileNotFoundException, XmlPullParserException,
            IOException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        MessageHandler messageHandler = createMock(MessageHandler.class);

        gpxLoader.open(GpxLoader.GPX_PATH);
        gpxLoader.load();
        messageHandler.loadComplete();

        replay(gpxLoader);
        replay(messageHandler);
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxLoader,
                messageHandler, null);
        importThreadDelegate.run();
        verify(gpxLoader);
        verify(messageHandler);
    }

    public void testImportThreadDelegateRunFileNotFound() throws FileNotFoundException,
            XmlPullParserException, IOException {
        importThreadDelegateRunAndThrow(FileNotFoundException.class, R.string.error_opening_file);
    }

    public void testImportThreadDelegateXmlPullParserException() throws FileNotFoundException,
            XmlPullParserException, IOException {
        importThreadDelegateRunAndThrow(XmlPullParserException.class, R.string.error_parsing_file);
    }

    public void testImportThreadDelegateIOException() throws FileNotFoundException,
            XmlPullParserException, IOException {
        importThreadDelegateRunAndThrow(IOException.class, R.string.error_reading_file);
    }

    private <T> void importThreadDelegateRunAndThrow(Class<T> exceptionClass, int errorMessage)
            throws FileNotFoundException, XmlPullParserException, IOException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        Throwable e = (Throwable)createMock(exceptionClass);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        gpxLoader.open(GpxLoader.GPX_PATH);
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        errorDisplayer.displayError(errorMessage, GpxLoader.GPX_PATH);

        replay(errorDisplayer);
        replay(e);
        replay(gpxLoader);
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxLoader, null,
                errorDisplayer);
        importThreadDelegate.run();
        verify(e);
        verify(gpxLoader);
        verify(errorDisplayer);
    }

    public void testImportThreadDelegateRunAndThrowRandomException() throws FileNotFoundException,
            XmlPullParserException, IOException {
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        Exception e = createMock(RuntimeException.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);

        gpxLoader.open(GpxLoader.GPX_PATH);
        expectLastCall().andThrow(e);
        expect(e.fillInStackTrace()).andReturn(e);
        errorDisplayer.displayErrorAndStack(e);

        replay(errorDisplayer);
        replay(e);
        replay(gpxLoader);
        ImportThreadDelegate importThreadDelegate = new ImportThreadDelegate(gpxLoader, null,
                errorDisplayer);
        importThreadDelegate.run();
        verify(e);
        verify(gpxLoader);
        verify(errorDisplayer);
    }

}