
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporter.ImportThread;
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
        GpxLoader.Factory gpxLoaderFactory = createMock(GpxLoader.Factory.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        GpxLoader gpxLoader = createMock(GpxLoader.class);
        ProgressDialogWrapper progressDialog = createMock(ProgressDialogWrapper.class);
        ListActivity listActivity = createMock(ListActivity.class);
        ImportThread.Factory importThreadFactory = createMock(ImportThread.Factory.class);
        ImportThread importThread = createMock(ImportThread.class);

        sqliteWrapper.openWritableDatabase(database);
        expect(gpxLoaderFactory.create(sqliteWrapper)).andReturn(gpxLoader);
        gpxLoader.open();
        progressDialog.show(listActivity, "Importing Caches...", "Please wait...");
        expect(importThreadFactory.create(cacheListDelegate, progressDialog, gpxLoader)).andReturn(
                importThread);

        replay(database);
        replay(importThreadFactory);
        replay(cacheListDelegate);
        replay(gpxLoaderFactory);
        GpxImporter gpxImporter = new GpxImporter(gpxLoaderFactory, database, null, listActivity,
                importThreadFactory, progressDialog, sqliteWrapper);
        gpxImporter.importGpxs(cacheListDelegate);
        verify(gpxLoaderFactory);
        verify(cacheListDelegate);
        verify(importThreadFactory);
        verify(database);
    }

}
