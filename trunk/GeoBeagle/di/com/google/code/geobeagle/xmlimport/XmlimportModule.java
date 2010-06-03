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
import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.GpxAnnotation;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.ImportFolder;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class XmlimportModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(MessageHandler.class).in(ContextScoped.class);
        bind(XmlPullParserWrapper.class).in(ContextScoped.class);
    }

    @Provides
    @GpxAnnotation
    EventHelper eventHelperGpxProvider(XmlPathBuilder xmlPathBuilder,
            EventHandlerGpx eventHandlerGpx, XmlPullParserWrapper xmlPullParser) {
        return new EventHelper(xmlPathBuilder, eventHandlerGpx, xmlPullParser);
    }

    @Provides
    WakeLock wakeLockProvider(PowerManager powerManager) {
        return powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Importing");
    }

    @Provides
    @ImportFolder
    String importFolderProvider(@DefaultSharedPreferences SharedPreferences sharedPreferences) {
        String string = sharedPreferences.getString("import-folder", "/sdcard/download");
        if ((!string.endsWith("/")))
            return string + "/";
        return string;
    }
}
