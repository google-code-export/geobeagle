
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporter.ImportThreadWrapper;
import com.google.code.geobeagle.io.GpxImporter.ProgressDialogWrapper;
import com.google.code.geobeagle.ui.CacheListDelegate;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

public class GpxImporterTest extends TestCase {

    public void testImportGpxs() throws FileNotFoundException, XmlPullParserException, IOException {
        CacheListDelegate cacheListDelegate = createMock(CacheListDelegate.class);
        Database database = createMock(Database.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        ProgressDialogWrapper progressDialog = createMock(ProgressDialogWrapper.class);
        ListActivity listActivity = createMock(ListActivity.class);
        ImportThreadWrapper importThreadWrapper = createMock(ImportThreadWrapper.class);

        sqliteWrapper.openReadableDatabase(database);
        gpxLoader.open();
        progressDialog.show(listActivity, "Importing Caches...", "Please wait...");
        importThreadWrapper.open(cacheListDelegate, progressDialog, gpxLoader, null);
        importThreadWrapper.start();

        replay(database);
        replay(importThreadWrapper);
        replay(cacheListDelegate);
        GpxImporter gpxImporter = new GpxImporter(gpxLoader, database, null, listActivity,
                progressDialog, sqliteWrapper, importThreadWrapper);
        gpxImporter.importGpxs(cacheListDelegate);
        verify(cacheListDelegate);
        verify(importThreadWrapper);
        verify(database);
    }

    public void testAbort() throws InterruptedException {
        // TODO: test thread alive case.
        ProgressDialogWrapper progressDialog = createMock(ProgressDialogWrapper.class);
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        ImportThreadWrapper importThreadWrapper = createMock(ImportThreadWrapper.class);

        progressDialog.dismiss();
        gpxLoader.abortLoad();
        expect(importThreadWrapper.isAlive()).andReturn(false);

        replay(progressDialog);
        replay(gpxLoader);
        replay(importThreadWrapper);
        GpxImporter gpxImporter = new GpxImporter(gpxLoader, null, null, null, progressDialog,
                null, importThreadWrapper);
        gpxImporter.abort();
        verify(progressDialog);
        verify(gpxLoader);
        verify(importThreadWrapper);
    }
}
