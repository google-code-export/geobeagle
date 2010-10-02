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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxAndZipFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;
import com.google.code.geobeagle.xmlimport.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipFileIter;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        GpxAndZipFilenameFilter.class, File.class, GpxAndZipFiles.class, IGpxReader.class,
        GpxFileOpener.class, ZipFileIter.class, ZipFileOpener.class
})
public class GpxImporterTest {
    private GpxLoaderFromFile gpxLoaderFromFile;

    @Before
    public void setUp() {
        gpxLoaderFromFile = PowerMock.createMock(GpxLoaderFromFile.class);
    }

    @Test
    public void testAbort() {
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);

        gpxLoaderFromFile.abort();
        expect(importThreadWrapper.isAlive()).andReturn(false);
        messageHandler.abortLoad();

        PowerMock.replayAll();
        new GpxImporter(null, gpxLoaderFromFile, null, importThreadWrapper, messageHandler, null,
                null, null, null, null).abort();
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAbortThreadAlive() {
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        MessageHandler messageHandler = PowerMock.createMock(MessageHandler.class);
        ToastFactory toastFactory = PowerMock.createMock(ToastFactory.class);
        Provider<Context> contextProvider = PowerMock.createMock(Provider.class);
        Context context = PowerMock.createMock(Context.class);

        gpxLoaderFromFile.abort();
        expect(importThreadWrapper.isAlive()).andReturn(true);
        messageHandler.abortLoad();
        importThreadWrapper.join();
        expect(contextProvider.get()).andReturn(context);
        toastFactory.showToast(context, R.string.import_canceled, Toast.LENGTH_SHORT);

        PowerMock.replayAll();
        new GpxImporter(null, gpxLoaderFromFile, contextProvider, importThreadWrapper,
                messageHandler, toastFactory, null, null, null, null).abort();
        PowerMock.verifyAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImportGpxs() {
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        Provider<CacheListRefresh> cacheListRefreshProvider = PowerMock.createMock(Provider.class);
        ImportThreadWrapper importThreadWrapper = PowerMock.createMock(ImportThreadWrapper.class);
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);

        expect(cacheListRefreshProvider.get()).andReturn(cacheListRefresh);
        geocacheListPresenter.onPause();
        importThreadWrapper.open(cacheListRefresh, gpxLoaderFromFile, null, null, null);
        importThreadWrapper.start();

        PowerMock.replayAll();
        new GpxImporter(geocacheListPresenter, gpxLoaderFromFile, null, importThreadWrapper, null,
                null, null, null, cacheListRefreshProvider, null).importGpxs();
        PowerMock.verifyAll();
    }
}
