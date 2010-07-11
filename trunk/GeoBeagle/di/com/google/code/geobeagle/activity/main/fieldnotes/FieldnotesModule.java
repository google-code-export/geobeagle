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

package com.google.code.geobeagle.activity.main.fieldnotes;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeoBeaglePackageModule.DefaultSharedPreferences;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickOk;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.code.geobeagle.xmlimport.XmlimportModule;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class FieldnotesModule extends AbstractAndroidModule {

    @BindingAnnotation
    @Target( {
            FIELD, PARAMETER, METHOD
    })
    @Retention(RUNTIME)
    public static @interface FieldNotesFilename {
    }

    public static class OnClickOkFactory {
        private final CacheLogger cacheLogger;
        private final Geocache geocache;

        @Inject
        public OnClickOkFactory(Geocache geocache, CacheLogger cacheLogger) {
            this.geocache = geocache;
            this.cacheLogger = cacheLogger;
        }
        
        public OnClickOk create(EditText editText, boolean dnf) {
            return new OnClickOk(geocache.getId(), editText, cacheLogger, dnf);
        }
    }
    
    public static class DialogHelperSmsFactory {

        private final FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf;
        @Inject
        public DialogHelperSmsFactory(FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf) {
            this.fieldnoteStringsFVsDnf = fieldnoteStringsFVsDnf;
        }

        public DialogHelperSms create(int geocacheIdLength, boolean dnf) {
            return new DialogHelperSms(fieldnoteStringsFVsDnf, geocacheIdLength, dnf);
        }

    }
    
    public static class FieldnoteLoggerFactory {

        private final DialogHelperCommon dialogHelperCommon;
        private final DialogHelperFile dialogHelperFile;
        private final SharedPreferences sharedPreferences;

        @Inject
        public FieldnoteLoggerFactory(DialogHelperCommon dialogHelperCommon,
                DialogHelperFile dialogHelperFile,
                @DefaultSharedPreferences SharedPreferences sharedPreferences) {
            this.dialogHelperCommon = dialogHelperCommon;
            this.dialogHelperFile = dialogHelperFile;
            this.sharedPreferences = sharedPreferences;
        }
        
        public FieldnoteLogger create(DialogHelperSms dialogHelperSms) {
            return new FieldnoteLogger(dialogHelperCommon, dialogHelperFile, dialogHelperSms,
                    sharedPreferences);
        }

    }
    
    // Removed DateFormat: 10.8, 10.6.
    @Override
    protected void configure() {
    }
    
    public static class ToasterFactory {
        private final Context context;

        @Inject
        ToasterFactory(Context context) {
            this.context = context;
        }

        public Toaster create(int resource) {
            return new Toaster(context, resource, Toast.LENGTH_LONG);
        }
    }

    private static final String FIELDNOTES_FILE = "GeoBeagleFieldNotes.txt";

    @Provides
    @FieldNotesFilename
    String providesFieldNotesFilename(GeoBeagleEnvironment environment) {
        return environment.getExternalStorageDir() + "/" + FIELDNOTES_FILE;
    }
    
    static String getFieldNotesFilename() {
        return XmlimportModule.providesPicturesDirectoryStatic() + "/" + FIELDNOTES_FILE;
    }
    
}
