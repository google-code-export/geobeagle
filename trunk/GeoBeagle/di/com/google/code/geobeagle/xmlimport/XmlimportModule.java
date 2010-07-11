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


import com.google.code.geobeagle.GeoBeaglePackageModule.DefaultSharedPreferences;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.cachedetails.Emotifier;
import com.google.code.geobeagle.cachedetails.HtmlWriter;
import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.code.geobeagle.cachedetails.Writer;
import com.google.code.geobeagle.cachedetails.WriterWrapper;
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.GpxAnnotation;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.LoadDetails;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.WriteDetails;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.util.Arrays;

public class XmlimportModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(StringWriterWrapper.class).in(Singleton.class);
        bind(MessageHandler.class).in(ContextScoped.class);
        bind(XmlPullParserWrapper.class).in(ContextScoped.class);
        bind(GpxWriter.class).in(ContextScoped.class);
        bind(Writer.class).to(WriterWrapper.class);
        bind(EmotifierPatternProvider.class).in(Singleton.class);
    }

    @Provides
    @GpxAnnotation
    @ContextScoped
    EventHelper eventHelperGpxProvider(XmlPathBuilder xmlPathBuilder,
            @WriteDetails EventHandlerGpx eventHandlerGpx, XmlPullParserWrapper xmlPullParser,
            XmlWriter xmlWriter) {
        EventHandlerComposite eventHandlerComposite = new EventHandlerComposite(Arrays.asList(
                xmlWriter, eventHandlerGpx));

        return new EventHelper(xmlPathBuilder, eventHandlerComposite, xmlPullParser);
    }
    
    public static String providesPicturesDirectoryStatic() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    
    public static class GeoBeagleEnvironment {
        private final SharedPreferences sharedPreferences;

        @Inject
        GeoBeagleEnvironment(@DefaultSharedPreferences SharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
        }
        
        public String getExternalStorageDir() {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        public String getDetailsDirectory() {
            return getExternalStorageDir() + "/" + DETAILS_DIR;
        }
        
        public String getVersionPath() {
            return getDetailsDirectory() + "/VERSION";
        }
        
        public String getOldDetailsDirectory() {
            return getExternalStorageDir()  + "/" + "GeoBeagle";
        }

        public String getImportFolder() {
            String string = sharedPreferences.getString("import-folder", Environment
                    .getExternalStorageDirectory()
                    + "/Download");
            if ((!string.endsWith("/")))
                return string + "/";
            return string;
        }
    }

    private static final String DETAILS_DIR = "GeoBeagle/data/";

    @Provides
    @WriteDetails
    HtmlWriter htmlWriterWriteDetailsProvider(WriterWrapper writerWrapper) {
        return new HtmlWriter(writerWrapper);
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
    CacheDetailsWriter cacheDetailsWriterLoadDetailsProvider(@LoadDetails HtmlWriter htmlWriter,
            Emotifier emotifier, Context context) {
        return new CacheDetailsWriter(htmlWriter, emotifier, context);
    }

    @Provides
    @LoadDetails
    EventHandlerGpx eventHandlerGpxLoadDetailsProvider(CacheTagsToDetails cachePersisterFacade) {
        return new EventHandlerGpx(cachePersisterFacade);
    }

}
