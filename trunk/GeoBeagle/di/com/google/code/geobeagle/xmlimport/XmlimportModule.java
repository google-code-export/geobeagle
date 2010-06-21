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

import com.google.code.geobeagle.activity.main.GeoBeagleModule.DefaultSharedPreferences;
import com.google.code.geobeagle.activity.main.GeoBeagleModule.ExternalStorageDirectory;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.cachedetails.HtmlWriter;
import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.code.geobeagle.cachedetails.Writer;
import com.google.code.geobeagle.cachedetails.WriterWrapper;
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.DetailsDirectory;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.GpxAnnotation;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.ImportDirectory;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.LoadDetails;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.OldDetailsDirectory;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.VersionPath;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.WriteDetails;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.content.SharedPreferences;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.util.Arrays;

public class XmlimportModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(StringWriterWrapper.class).in(Singleton.class);
        bind(MessageHandler.class).in(ContextScoped.class);
        bind(XmlPullParserWrapper.class).in(ContextScoped.class);
        bind(GpxWriter.class).in(ContextScoped.class);
        bind(Writer.class).to(WriterWrapper.class);
    }

    @Provides
    @GpxAnnotation
    EventHelper eventHelperGpxProvider(XmlPathBuilder xmlPathBuilder,
            @WriteDetails EventHandlerGpx eventHandlerGpx, XmlPullParserWrapper xmlPullParser,
            XmlWriter xmlWriter) {
        EventHandlerComposite eventHandlerComposite = new EventHandlerComposite(Arrays.asList(
                xmlWriter, eventHandlerGpx));

        return new EventHelper(xmlPathBuilder, eventHandlerComposite, xmlPullParser);
    }

    @Provides
    WakeLock wakeLockProvider(PowerManager powerManager) {
        return powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Importing");
    }

    @Provides
    @ImportDirectory
    String importFolderProvider(@DefaultSharedPreferences SharedPreferences sharedPreferences) {
        String string = sharedPreferences.getString("import-folder", Environment
                .getExternalStorageDirectory()
                + "/Download");
        if ((!string.endsWith("/")))
            return string + "/";
        return string;
    }

    private static final String DETAILS_DIR = "GeoBeagle/data/";

    @Provides
    @DetailsDirectory
    String detailsDirectoryProvider(@ExternalStorageDirectory String externalStorageDirectory) {
        return externalStorageDirectory + "/" + DETAILS_DIR;
    }

    @Provides
    @VersionPath
    String versionDirectoryProvider(@DetailsDirectory String detailsDirectory) {
        return detailsDirectory + "/VERSION";
    }

    @Provides
    @OldDetailsDirectory
    String oldDetailsDirectoryProvider(@ExternalStorageDirectory String externalStorageDirectory) {
        return externalStorageDirectory + "/" + "GeoBeagle";
    }

    @Provides
    @WriteDetails
    HtmlWriter htmlWriterWriteDetailsProvider(WriterWrapper writerWrapper) {
        return new HtmlWriter(writerWrapper);
    }

    @Provides
    @WriteDetails
    CacheDetailsWriter cacheDetailsWriterWriteDetailsProvider(@WriteDetails HtmlWriter htmlWriter) {
        return new CacheDetailsWriter(htmlWriter);
    }

    @Provides
    @WriteDetails
    EventHandlerGpx eventHandlerGpxWriteDetailsProvider(CachePersisterFacade cachePersisterFacade) {
        return new EventHandlerGpx(cachePersisterFacade);
    }

    @Provides
    @LoadDetails
    HtmlWriter htmlWriterLoadDetailsProvider(StringWriterWrapper stringWriterWrapper) {
        return new HtmlWriter(stringWriterWrapper);
    }

    @Provides
    @LoadDetails
    CacheDetailsWriter cacheDetailsWriterLoadDetailsProvider(@LoadDetails HtmlWriter htmlWriter) {
        return new CacheDetailsWriter(htmlWriter);
    }

    @Provides
    @LoadDetails
    EventHandlerGpx eventHandlerGpxLoadDetailsProvider(CacheTagsToDetails cachePersisterFacade) {
        return new EventHandlerGpx(cachePersisterFacade);
    }

    @Provides
    @LoadDetails
    EventHelper eventHelperGpxLoadDetailsProvider(XmlPathBuilder xmlPathBuilder,
            @LoadDetails EventHandlerGpx eventHandlerGpx, XmlPullParserWrapper xmlPullParser) {
        return new EventHelper(xmlPathBuilder, eventHandlerGpx, xmlPullParser);
    }
}
