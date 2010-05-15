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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxToCache.GpxToCacheFactory;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class XmlimportModule extends AbstractAndroidModule {

    @BindingAnnotation
    @Target( {
            FIELD, PARAMETER, METHOD
    })
    @Retention(RUNTIME)
    public static @interface GpxAnnotation {
    }

    @Override
    protected void configure() {
        bind(MessageHandler.class).in(ContextScoped.class);
        bind(GpxToCacheFactory.class).toProvider(
                FactoryProvider.newFactory(GpxToCacheFactory.class, GpxToCache.class));
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
}
