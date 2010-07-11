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


import com.google.code.geobeagle.GeoBeaglePackageModule;
import com.google.code.geobeagle.GeoBeaglePackageModule.DefaultSharedPreferences;
import com.google.code.geobeagle.GeoBeaglePackageModule.ExternalStorageDirectory;
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
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.ImportDirectory;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.LoadDetails;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.OldDetailsDirectory;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.WriteDetails;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.util.Arrays;
import java.util.regex.Pattern;

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
        private String getExternalStorageDir() {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        public String getDetailsDirectory() {
            return getExternalStorageDir() + "/" + DETAILS_DIR;
        }
        
        public String getVersionPath() {
            return getDetailsDirectory() + "/VERSION";
        }
    }

    @Provides
    @ExternalStorageDirectory
    String providesPicturesDirectory() {
        return providesPicturesDirectoryStatic();
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
    @OldDetailsDirectory
    String oldDetailsDirectoryProvider(@GeoBeaglePackageModule.ExternalStorageDirectory String externalStorageDirectory) {
        return externalStorageDirectory + "/" + "GeoBeagle";
    }

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

    static Pattern createEmotifierPattern(String[] emoticons) {
        StringBuffer keysBuffer = new StringBuffer();
        String escapeChars = "()|?}";
        for (String emoticon : emoticons) {
            String key = new String(emoticon);
            for (int i = 0; i < escapeChars.length(); i++) {
                char c = escapeChars.charAt(i);
                key = key.replaceAll("\\" + String.valueOf(c), "\\\\" + c);
            }
            keysBuffer.append("|" + key);
        }
        keysBuffer.deleteCharAt(0);
        final String keys = "\\[(" + keysBuffer.toString() + ")\\]";
        try {
        return Pattern.compile(keys);
        } catch (Exception e) {
            Log.d("GeoBeagle", e.getLocalizedMessage());
        }
        return null;
    }

    @Provides
    public Pattern providesEmotifierPattern() {
        Log.d("GeoBeagle", "PROVIDING EMOFIEOFJE");
        String emoticons[] = {
                ":(", ":o)", ":)", ":D", "8D", ":I", ":P", "}:)", ":)", ":D", "8D", ":I", ":P",
                "}:)", ";)", "B)", "8", ":)", "8)", ":O", ":(!", "xx(", "|)", ":X", "V", "?",
                "^"
        };
        return createEmotifierPattern(emoticons);
    }
    
}
